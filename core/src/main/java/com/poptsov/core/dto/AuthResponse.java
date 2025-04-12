package com.poptsov.core.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с токеном аутентификации")
public record AuthResponse(
        @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}