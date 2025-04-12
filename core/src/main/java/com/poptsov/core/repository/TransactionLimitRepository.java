package com.poptsov.core.repository;

import com.poptsov.core.model.LimitType;
import com.poptsov.core.model.TransactionLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, Long> {
    Optional<TransactionLimit> findByCardIdAndLimitType(Long cardId, LimitType limitType);
}