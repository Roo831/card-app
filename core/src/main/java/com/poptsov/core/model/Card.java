package com.poptsov.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;

@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_cards_user_id", columnList = "user_id")})
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number_encrypted", nullable = false)
    private String cardNumberEncrypted;

    @Column(name = "card_number_masked", nullable = false)
    private String cardNumberMasked;

    @Column(nullable = false, length = 255)
    private String holderName;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal balance;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    public Card(Long id, User user, String cardNumberEncrypted, String cardNumberMasked, String holderName, LocalDate expirationDate, BigDecimal balance, CardStatus status) {
        this.id = id;
        this.user = user;
        this.cardNumberEncrypted = cardNumberEncrypted;
        this.cardNumberMasked = cardNumberMasked;
        this.holderName = holderName;
        this.expirationDate = expirationDate;
        this.balance = balance;
        this.status = status;
    }

    public Card() {
    }

    public static CardBuilder builder() {
        return new CardBuilder();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCardNumberEncrypted() {
        return cardNumberEncrypted;
    }

    public void setCardNumberEncrypted(String cardNumberEncrypted) {
        this.cardNumberEncrypted = cardNumberEncrypted;
    }

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }


    public static class CardBuilder {
        private Long id;
        private User user;
        private String cardNumberEncrypted;
        private String cardNumberMasked;
        private String holderName;
        private LocalDate expirationDate;
        private BigDecimal balance;
        private CardStatus status;

        CardBuilder() {
        }

        public CardBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CardBuilder user(User user) {
            this.user = user;
            return this;
        }

        public CardBuilder cardNumberEncrypted(String cardNumberEncrypted) {
            this.cardNumberEncrypted = cardNumberEncrypted;
            return this;
        }

        public CardBuilder cardNumberMasked(String cardNumberMasked) {
            this.cardNumberMasked = cardNumberMasked;
            return this;
        }

        public CardBuilder holderName(String holderName) {
            this.holderName = holderName;
            return this;
        }

        public CardBuilder expirationDate(LocalDate expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public CardBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public CardBuilder status(CardStatus status) {
            this.status = status;
            return this;
        }

        public Card build() {
            return new Card(this.id, this.user, this.cardNumberEncrypted, this.cardNumberMasked, this.holderName, this.expirationDate, this.balance, this.status);
        }

        public String toString() {
            return "Card.CardBuilder(id=" + this.id + ", user=" + this.user + ", cardNumberEncrypted=" + this.cardNumberEncrypted + ", cardNumberMasked=" + this.cardNumberMasked + ", holderName=" + this.holderName + ", expirationDate=" + this.expirationDate + ", balance=" + this.balance + ", status=" + this.status + ")";
        }
    }

}
