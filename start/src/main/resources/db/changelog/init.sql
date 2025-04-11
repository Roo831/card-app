--liquibase formatted sql

--changeset rpoptsov:1
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       card_number_encrypted VARCHAR(500) NOT NULL,
                       card_number_masked VARCHAR(19) NOT NULL,
                       holder_name VARCHAR(255) NOT NULL,
                       expiration_date DATE NOT NULL,
                       status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
                       balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transaction_limits (
                                    id BIGSERIAL PRIMARY KEY,
                                    card_id INT NOT NULL,
                                    limit_type VARCHAR(20) NOT NULL CHECK (limit_type IN ('DAILY', 'MONTHLY')),
                                    amount DECIMAL(15,2) NOT NULL,
                                    reset_period TIMESTAMP WITH TIME ZONE NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              source_card_id INT NOT NULL,
                              target_card_id INT,
                              amount DECIMAL(15,2) NOT NULL,
                              transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('TRANSFER', 'PAYMENT', 'REFUND')),
                              status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
                              description VARCHAR(500),
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (source_card_id) REFERENCES cards(id) ON DELETE CASCADE,
                              FOREIGN KEY (target_card_id) REFERENCES cards(id) ON DELETE SET NULL
);

CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE UNIQUE INDEX idx_unique_card_limit ON transaction_limits(card_id, limit_type);