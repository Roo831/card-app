package com.poptsov.transactions.service;

import com.poptsov.core.dto.TransferRequestDto;
import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.exception.*;
import com.poptsov.core.mapper.TransactionMapper;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.poptsov.core.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;
    private final CardNumberEncryptor cardNumberEncryptor;
    private final TransactionLimitService transactionLimitService;


    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, CardRepository cardRepository, TransactionMapper transactionMapper, CardNumberEncryptor cardNumberEncryptor, TransactionLimitService transactionLimitService) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.transactionMapper = transactionMapper;
        this.cardNumberEncryptor = cardNumberEncryptor;
        this.transactionLimitService = transactionLimitService;
    }

    @Transactional
    public TransactionResponseDto transferBetweenCards(TransferRequestDto request) {
        log.info("Initiating transfer from card {} to card {} for amount {}",
                request.sourceCardId(), request.targetCardId(), request.amount());

        Long userId = SecurityUtils.getCurrentUser().getId();
        log.debug("Current user ID: {}", userId);

        Card sourceCard = cardRepository.findByIdAndUserId(request.sourceCardId(), userId)
                .orElseThrow(() -> {
                    log.error("User {} doesn't have access to source card {}", userId, request.sourceCardId());
                    return new CardAccessDeniedException(request.sourceCardId());
                });

        log.debug("Source card found: {}", sourceCard.getId());

        if (sourceCard.getBalance().compareTo(request.amount()) < 0) {
            log.error("Insufficient funds on card {}. Balance: {}, Requested: {}",
                    sourceCard.getId(), sourceCard.getBalance(), request.amount());
            throw new InsufficientFundsException("Not enough money on the card");
        }

        Card targetCard = cardRepository.findById(request.targetCardId())
                .orElseThrow(() -> {
                    log.error("Target card not found: {}", request.targetCardId());
                    return new CardNotFoundException(request.targetCardId());
                });

        log.debug("Target card found: {}", targetCard.getId());

        try {
            transactionLimitService.checkLimit(request.sourceCardId(), request.amount(), LimitType.DAILY);
            transactionLimitService.checkLimit(request.sourceCardId(), request.amount(), LimitType.MONTHLY);
            log.debug("Limit checks passed for card {}", request.sourceCardId());
        } catch (LimitNotSetException | LimitExceededException | LimitExpiredException e) {
            log.warn("Limit violation for card {}: {}", request.sourceCardId(), e.getMessage());
            throw e;
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(request.amount()));
        targetCard.setBalance(targetCard.getBalance().add(request.amount()));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);
        log.debug("Balances updated - Source: {}, Target: {}", sourceCard.getBalance(), targetCard.getBalance());

        Transaction transaction = Transaction.builder()
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .amount(request.amount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.description())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction completed successfully. Transaction ID: {}", savedTransaction.getId());

        return transactionMapper.toResponseDto(savedTransaction, cardNumberEncryptor);
    }
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getCardTransactions(Long cardId, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        log.debug("Getting transactions for cardId: {}, userId: {}", cardId, userId);

        if (!cardRepository.existsByIdAndUserId(cardId, userId)) {
            log.warn("Card access denied for cardId: {}, userId: {}", cardId, userId);
            throw new CardAccessDeniedException(cardId);
        }

        Page<Transaction> transactions = transactionRepository.findBySourceCardIdOrTargetCardId(cardId, cardId, pageable);
        log.debug("Found {} transactions", transactions.getTotalElements());

        return convertToDtoPage(transactions, cardNumberEncryptor);
    }

    public Page<TransactionResponseDto> convertToDtoPage(Page<Transaction> transactions, CardNumberEncryptor cardNumberEncryptor) {
        List<TransactionResponseDto> dtoList = transactions.getContent().stream()
                .map(transaction -> transactionMapper.toResponseDto(transaction, cardNumberEncryptor))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, transactions.getPageable(), transactions.getTotalElements());
    }
}