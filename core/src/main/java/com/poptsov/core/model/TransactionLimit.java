package com.poptsov.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_limits", indexes = {
        @Index(name = "idx_unique_card_limit", columnList = "card_id, limit_type", unique = true)
})
public class TransactionLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitType limitType;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal amount;

    @Column(name = "reset_period", nullable = false)
    private LocalDateTime resetPeriod;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public TransactionLimit(Long id, Card card, LimitType limitType, BigDecimal amount, LocalDateTime resetPeriod, LocalDateTime createdAt) {
        this.id = id;
        this.card = card;
        this.limitType = limitType;
        this.amount = amount;
        this.resetPeriod = resetPeriod;
        this.createdAt = createdAt;
    }

    public TransactionLimit() {
    }

    public static TransactionLimitBuilder builder() {
        return new TransactionLimitBuilder();
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

    public Card getCard() {
        return this.card;
    }

    public LimitType getLimitType() {
        return this.limitType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public LocalDateTime getResetPeriod() {
        return this.resetPeriod;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setLimitType(LimitType limitType) {
        this.limitType = limitType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setResetPeriod(LocalDateTime resetPeriod) {
        this.resetPeriod = resetPeriod;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class TransactionLimitBuilder {
        private Long id;
        private Card card;
        private LimitType limitType;
        private BigDecimal amount;
        private LocalDateTime resetPeriod;
        private LocalDateTime createdAt;

        TransactionLimitBuilder() {
        }

        public TransactionLimitBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TransactionLimitBuilder card(Card card) {
            this.card = card;
            return this;
        }

        public TransactionLimitBuilder limitType(LimitType limitType) {
            this.limitType = limitType;
            return this;
        }

        public TransactionLimitBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionLimitBuilder resetPeriod(LocalDateTime resetPeriod) {
            this.resetPeriod = resetPeriod;
            return this;
        }

        public TransactionLimitBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TransactionLimit build() {
            return new TransactionLimit(this.id, this.card, this.limitType, this.amount, this.resetPeriod, this.createdAt);
        }

        public String toString() {
            return "TransactionLimit.TransactionLimitBuilder(id=" + this.id + ", card=" + this.card + ", limitType=" + this.limitType + ", amount=" + this.amount + ", resetPeriod=" + this.resetPeriod + ", createdAt=" + this.createdAt + ")";
        }
    }
}