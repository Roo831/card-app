package com.poptsov.core.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип лимита")
public enum LimitType {

    @Schema(description = "Дневной лимит") DAILY,
    @Schema(description = "Месячный лимит") MONTHLY
}
