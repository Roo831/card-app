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

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TransactionServiceImpl implements TransactionService {

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
        Long userId = SecurityUtils.getCurrentUser().getId();
        Card sourceCard = cardRepository.findByIdAndUserId(request.sourceCardId(), userId)
                .orElseThrow(() -> new CardAccessDeniedException(request.sourceCardId()));

        Card targetCard = cardRepository.findById(request.targetCardId())
                .orElseThrow(() -> new CardNotFoundException(request.targetCardId()));

        transactionLimitService.checkLimit(request.sourceCardId(), request.amount(), LimitType.DAILY);
        transactionLimitService.checkLimit(request.sourceCardId(), request.amount(), LimitType.MONTHLY);

        if (sourceCard.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Not enough money on the card");
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(request.amount()));
        targetCard.setBalance(targetCard.getBalance().add(request.amount()));

        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);

        Transaction transaction = Transaction.builder()
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .amount(request.amount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .description(request.description())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponseDto(savedTransaction, cardNumberEncryptor);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getCardTransactions(Long cardId, Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        if (!cardRepository.existsByIdAndUserId(cardId, userId)) {
            throw new CardAccessDeniedException(cardId);
        }

        return convertToDtoPage(transactionRepository.findBySourceCardIdOrTargetCardId(cardId, cardId, pageable), cardNumberEncryptor);
    }

    public Page<TransactionResponseDto> convertToDtoPage(Page<Transaction> transactions, CardNumberEncryptor cardNumberEncryptor) {
        List<TransactionResponseDto> dtoList = transactions.getContent().stream()
                .map(transaction -> transactionMapper.toResponseDto(transaction, cardNumberEncryptor))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, transactions.getPageable(), transactions.getTotalElements());
    }
}