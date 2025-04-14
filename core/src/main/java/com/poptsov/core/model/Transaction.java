package com.poptsov.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_created_at", columnList = "created_at")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_card_id", nullable = false)
    private Card sourceCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_card_id")
    private Card targetCard;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Transaction(Long id, Card sourceCard, Card targetCard, BigDecimal amount, TransactionType type, TransactionStatus status, String description, LocalDateTime createdAt) {
        this.id = id;
        this.sourceCard = sourceCard;
        this.targetCard = targetCard;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Transaction() {
    }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return this.id;
    }

    public Card getSourceCard() {
        return this.sourceCard;
    }

    public Card getTargetCard() {
        return this.targetCard;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public TransactionType getType() {
        return this.type;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSourceCard(Card sourceCard) {
        this.sourceCard = sourceCard;
    }

    public void setTargetCard(Card targetCard) {
        this.targetCard = targetCard;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class TransactionBuilder {
        private Long id;
        private Card sourceCard;
        private Card targetCard;
        private BigDecimal amount;
        private TransactionType type;
        private TransactionStatus status;
        private String description;
        private LocalDateTime createdAt;

        TransactionBuilder() {
        }

        public TransactionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TransactionBuilder sourceCard(Card sourceCard) {
            this.sourceCard = sourceCard;
            return this;
        }

        public TransactionBuilder targetCard(Card targetCard) {
            this.targetCard = targetCard;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public TransactionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TransactionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Transaction build() {
            return new Transaction(this.id, this.sourceCard, this.targetCard, this.amount, this.type, this.status, this.description, this.createdAt);
        }

        public String toString() {
            return "Transaction.TransactionBuilder(id=" + this.id + ", sourceCard=" + this.sourceCard + ", targetCard=" + this.targetCard + ", amount=" + this.amount + ", type=" + this.type + ", status=" + this.status + ", description=" + this.description + ", createdAt=" + this.createdAt + ")";
        }
    }
}