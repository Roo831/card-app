package com.poptsov.core.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус транзакции")
public enum TransactionStatus {
    @Schema(description = "В обработке") PENDING,
    @Schema(description = "Завершена") COMPLETED,
    @Schema(description = "Не удалась") FAILED
}