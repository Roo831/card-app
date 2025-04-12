package com.poptsov.transactions.service;

import com.poptsov.core.dto.SetLimitRequestDto;
import com.poptsov.core.exception.CardNotFoundException;
import com.poptsov.core.model.Card;
import com.poptsov.core.model.LimitType;
import com.poptsov.core.model.TransactionLimit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionLimitService {

    public void setLimit(SetLimitRequestDto request);

    public void checkLimit(Long cardId, BigDecimal amount, LimitType limitType);
}
