package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.exception.CardAccessDeniedException;
import com.poptsov.core.exception.CardNotFoundException;

import com.poptsov.core.exception.UserNotFoundException;
import com.poptsov.core.mapper.CardMapper;
import com.poptsov.core.mapper.TransactionMapper;
import com.poptsov.core.model.Card;
import com.poptsov.core.model.CardStatus;
import com.poptsov.core.model.Transaction;
import com.poptsov.core.model.User;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionRepository;
import com.poptsov.core.repository.UserRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.CardNumberGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.poptsov.core.util.SecurityUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardNumberGenerator numberGenerator;
    private final CardNumberEncryptor encryptor;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository, CardMapper cardMapper, CardNumberGenerator numberGenerator, CardNumberEncryptor encryptor, TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
        this.numberGenerator = numberGenerator;
        this.encryptor = encryptor;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public CardResponseDto createCard(CardCreateDto request) {
        log.info("Creating card for user ID: {}", request.userId());
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", request.userId());
                    return new UserNotFoundException(request.userId());
                });

        String cardNumber = numberGenerator.generate();
        log.debug("Generated card number: {}", cardNumber);

        Card card = Card.builder()
                .user(user)
                .cardNumberEncrypted(encryptor.encrypt(cardNumber))
                .cardNumberMasked(encryptor.mask(cardNumber))
                .holderName(request.holderName())
                .expirationDate(request.expirationDate())
                .balance(request.initialBalance())
                .status(CardStatus.ACTIVE)
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Card created successfully with ID: {}", savedCard.getId());

        return cardMapper.toResponseDto(savedCard);
    }

    @Transactional(readOnly = true)
    public Page<CardResponseDto> getAllCards(String status, Pageable pageable) {
        log.debug("Fetching all cards with status: {}", status);

        Page<Card> cards = Optional.ofNullable(status)
                .map(s -> {
                    log.trace("Filtering by status: {}", s);
                    return cardRepository.findByStatus(CardStatus.valueOf(s), pageable);
                })
                .orElseGet(() -> {
                    log.trace("No status filter applied");
                    return cardRepository.findAll(pageable);
                });

        log.debug("Found {} cards", cards.getTotalElements());
        return cards.map(cardMapper::toResponseDto);
    }

    @Override
    public CardResponseDto changeCardStatus(Long cardId, CardStatus newStatus) {
        log.info("Changing status for card ID: {} to {}", cardId, newStatus);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (newStatus == CardStatus.ACTIVE && card.getExpirationDate().isBefore(LocalDate.now())) {
            log.warn("Attempt to activate expired card ID: {}", cardId);
            throw new IllegalStateException("Cannot activate expired card");
        }

        card.setStatus(newStatus);
        log.info("Card status updated successfully for card ID: {}", cardId);

        return cardMapper.toResponseDto(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getUserCards() {
        Long userId = SecurityUtils.getCurrentUser().getId();
        log.debug("Fetching cards for user ID: {}", userId);

        List<Card> cards = cardRepository.findByUserId(userId);
        log.debug("Found {} cards for user {}", cards.size(), userId);

        return cards.stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Override
    public CardResponseDto userBlockCard(Long cardId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        log.info("User ID: {} attempting to block card ID: {}", userId, cardId);
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> {
                    log.warn("Access denied for user ID: {} to card ID: {}", userId, cardId);
                    return new CardAccessDeniedException(cardId);
                });

        card.setStatus(CardStatus.BLOCKED);

        Card blockedCard = cardRepository.save(card);
        log.info("Card ID: {} blocked successfully by user ID: {}", cardId, userId);

        return cardMapper.toResponseDto(blockedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getCardTransactions(Long cardId, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        if (!cardRepository.existsByIdAndUserId(cardId, userId)) {
            throw new CardAccessDeniedException(cardId);
        }

        return convertToDtoPage(transactionRepository.findBySourceCardId(cardId, pageable), encryptor);
    }

    public Page<TransactionResponseDto> convertToDtoPage(Page<Transaction> transactions, CardNumberEncryptor cardNumberEncryptor) {
        List<TransactionResponseDto> dtoList = transactions.getContent().stream()
                .map(transaction -> transactionMapper.toResponseDto(transaction, cardNumberEncryptor))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, transactions.getPageable(), transactions.getTotalElements());
    }
}