package com.poptsov.transactions.service;

import com.poptsov.core.dto.SetLimitRequestDto;
import com.poptsov.core.exception.*;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionLimitRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service

public class TransactionLimitServiceImpl implements TransactionLimitService {

    private final TransactionLimitRepository limitRepository;
    private final CardRepository cardRepository;

    @Autowired
    public TransactionLimitServiceImpl(TransactionLimitRepository limitRepository, CardRepository cardRepository) {
        this.limitRepository = limitRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void setLimit(SetLimitRequestDto request) {
        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() -> new CardNotFoundException(request.cardId()));

        LocalDateTime resetPeriod = calculateResetPeriod(request.limitType());

        limitRepository.findByCardIdAndLimitType(request.cardId(), request.limitType())
                .ifPresentOrElse(
                        limit -> {
                            limit.setAmount(request.amount());
                            limit.setResetPeriod(resetPeriod);
                            limitRepository.save(limit);
                        },
                        () -> {
                            TransactionLimit newLimit = TransactionLimit.builder()
                                    .card(card)
                                    .limitType(request.limitType())
                                    .amount(request.amount())
                                    .resetPeriod(resetPeriod)
                                    .build();
                            limitRepository.save(newLimit);
                        }
                );
    }

    @Transactional(readOnly = true)
    public void checkLimit(Long cardId, BigDecimal amount, LimitType limitType) {
        TransactionLimit limit = limitRepository.findByCardIdAndLimitType(cardId, limitType)
                .orElseThrow(() -> new LimitNotSetException(limitType));

        if (limit.getResetPeriod().isBefore(LocalDateTime.now())) {
            throw new LimitExpiredException(limitType);
        }

        if (limit.getAmount().compareTo(amount) < 0) {
            throw new LimitExceededException(limitType, limit.getAmount());
        }
    }

    private LocalDateTime calculateResetPeriod(LimitType limitType) {
        return switch (limitType) {
            case DAILY -> LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
            case MONTHLY -> LocalDateTime.now().plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        };
    }
}