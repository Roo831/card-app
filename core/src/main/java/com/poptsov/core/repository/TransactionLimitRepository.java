package com.poptsov.core.repository;

import com.poptsov.core.model.LimitType;
import com.poptsov.core.model.TransactionLimit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionLimitRepository extends JpaRepository<TransactionLimit, Long> {
    @EntityGraph(attributePaths = {"card"})
    Optional<TransactionLimit> findByCardIdAndLimitType(Long cardId, LimitType limitType);
}