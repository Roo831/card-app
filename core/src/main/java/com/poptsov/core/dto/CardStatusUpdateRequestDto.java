package com.poptsov.core.dto;

import com.poptsov.core.model.CardStatus;
import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequestDto(
        @NotNull CardStatus status) {}