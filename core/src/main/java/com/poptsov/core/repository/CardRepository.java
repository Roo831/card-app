package com.poptsov.core.repository;

import com.poptsov.core.model.Card;
import com.poptsov.core.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Card> findByUserId(Long userId);

    @Query("SELECT c FROM Card c WHERE (:status IS NULL OR c.status = :status)")
    Page<Card> findByStatus(@Param("status") CardStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"transactions", "user"})
    Optional<Card> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}