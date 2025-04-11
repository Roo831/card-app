package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.exception.EntityNotFoundException;
import com.poptsov.core.mapper.CardMapper;
import com.poptsov.core.model.Card;
import com.poptsov.core.model.Role;
import com.poptsov.core.model.User;
import com.poptsov.cards.repository.CardRepository;
import com.poptsov.core.repository.UserRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.CardNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardNumberGenerator numberGenerator;
    private final CardNumberEncryptor encryptor;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository,
                           UserRepository userRepository,
                           CardMapper cardMapper,
                           CardNumberGenerator numberGenerator,
                           CardNumberEncryptor encryptor) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardMapper = cardMapper;
        this.numberGenerator = numberGenerator;
        this.encryptor = encryptor;
    }

    @Override
    @Transactional
    public CardResponseDto createCard(CardCreateDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Генерация и обработка номера карты
        String cardNumber = numberGenerator.generate();
        String encryptedNumber = encryptor.encrypt(cardNumber);
        String maskedNumber = encryptor.mask(cardNumber);

        // Создание и сохранение карты
        Card card = new Card();
        card.setUser(user);
        card.setCardNumberEncrypted(encryptedNumber);
        card.setCardNumberMasked(maskedNumber);
        card.setHolderName(dto.getHolderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setBalance(dto.getInitialBalance());
        card.setStatus("ACTIVE");

        cardRepository.save(card);

        return cardMapper.toResponseDto(card);
    }

    @Override
    public List<CardResponseDto> getUserCards(Long userId) {
        return cardRepository.findByUserId(userId).stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Override
    public CardResponseDto getCardForUser(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have access to this card");
        }

        return cardMapper.toResponseDto(card);
    }

    @Override
    @Transactional
    public CardResponseDto blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can't block this card");
        }

        card.setStatus("BLOCKED");
        cardRepository.save(card);
        return cardMapper.toResponseDto(card);
    }

    @Override
    @Transactional
    public CardResponseDto activateCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (!card.getUser().getId().equals(userId) &&
                !userRepository.findById(userId).get().getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You can't activate this card");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            card.setStatus("EXPIRED");
        } else {
            card.setStatus("ACTIVE");
        }

        cardRepository.save(card);
        return cardMapper.toResponseDto(card);
    }

    @Override
    public List<CardResponseDto> getAllCards() {
        return cardRepository.findAll().stream()
                .map(cardMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new EntityNotFoundException("Card not found");
        }
        cardRepository.deleteById(cardId);
    }
}