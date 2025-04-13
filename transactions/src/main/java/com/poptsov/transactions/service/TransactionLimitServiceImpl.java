package com.poptsov.transactions.service;

import com.poptsov.core.dto.SetLimitRequestDto;
import com.poptsov.core.exception.*;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionLimitRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(TransactionLimitServiceImpl.class);

    @Autowired
    public TransactionLimitServiceImpl(TransactionLimitRepository limitRepository, CardRepository cardRepository) {
        this.limitRepository = limitRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void setLimit(SetLimitRequestDto request) {
        log.info("Setting {} limit for card ID: {}", request.limitType(), request.cardId());
        Card card = cardRepository.findById(request.cardId())
                .orElseThrow(() -> {
                    log.error("Card not found with ID: {}", request.cardId());
                    return new CardNotFoundException(request.cardId());
                });

        LocalDateTime resetPeriod = calculateResetPeriod(request.limitType());
        log.debug("Calculated reset period: {}", resetPeriod);

        limitRepository.findByCardIdAndLimitType(request.cardId(), request.limitType())
                .ifPresentOrElse(
                        limit -> {
                            limit.setAmount(request.amount());
                            limit.setResetPeriod(resetPeriod);
                            limitRepository.save(limit);
                            log.info("Updated existing limit for card ID: {}", request.cardId());
                        },
                        () -> {
                            TransactionLimit newLimit = TransactionLimit.builder()
                                    .card(card)
                                    .limitType(request.limitType())
                                    .amount(request.amount())
                                    .resetPeriod(resetPeriod)
                                    .build();
                            limitRepository.save(newLimit);
                            log.info("Created new limit for card ID: {}", request.cardId());
                        }
                );
    }

    @Transactional(readOnly = true)
    public void checkLimit(Long cardId, BigDecimal amount, LimitType limitType) {
        log.debug("Checking {} limit for card ID: {}", limitType, cardId);
        TransactionLimit limit = limitRepository.findByCardIdAndLimitType(cardId, limitType)
                .orElseThrow(() -> {
                    log.warn("{} limit not set for card ID: {}", limitType, cardId);
                    return new LimitNotSetException(limitType);
                });

        if (limit.getResetPeriod().isBefore(LocalDateTime.now())) {
            log.warn("{} limit expired for card ID: {}", limitType, cardId);
            throw new LimitExpiredException(limitType);
        }

        if (limit.getAmount().compareTo(amount) < 0) {
            log.warn("{} limit exceeded for card ID: {} (limit: {}, attempted: {})",
                    limitType, cardId, limit.getAmount(), amount);
            throw new LimitExceededException(limitType, limit.getAmount());
        }
        log.debug("Limit check passed for card ID: {}", cardId);
    }

    private LocalDateTime calculateResetPeriod(LimitType limitType) {
        return switch (limitType) {
            case DAILY -> LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
            case MONTHLY -> LocalDateTime.now().plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        };
    }
}