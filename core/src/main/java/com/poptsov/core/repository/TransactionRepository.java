package com.poptsov.core.repository;


import com.poptsov.core.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = {"sourceCard", "targetCard"})
    Page<Transaction> findBySourceCardIdOrTargetCardId(Long sourceCardId, Long targetCardId, Pageable pageable);
    @EntityGraph(attributePaths = {"sourceCard", "targetCard"})
    Page<Transaction> findBySourceCardId(Long sourceCardId, Pageable pageable);

}
