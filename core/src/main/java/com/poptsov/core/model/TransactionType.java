package com.poptsov.core.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип транзакции")
public enum TransactionType {
    @Schema(description = "Перевод") TRANSFER,
    @Schema(description = "Платеж") PAYMENT,
    @Schema(description = "Возврат") REFUND
}