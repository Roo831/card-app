package com.poptsov.core.exception;

public class CardAccessDeniedException extends RuntimeException {
    public CardAccessDeniedException(Long cardId) {
        super("Access denied to card with id: " + cardId);
    }
}