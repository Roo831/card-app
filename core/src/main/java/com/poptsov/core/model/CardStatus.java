package com.poptsov.core.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус карты")
public enum CardStatus {
    @Schema(description = "Активная") ACTIVE,
    @Schema(description = "Заблокированная") BLOCKED,
    @Schema(description = "Просроченная") EXPIRED
}
