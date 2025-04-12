package com.poptsov.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на аутентификацию")
public record AuthRequest(
        @Schema(description = "Email пользователя", example = "user@example.com")
        @Email
        @NotBlank
        String email,

        @Schema(description = "Пароль", example = "mySecurePassword123")
        @NotBlank
        @Size(min = 8)
        String password
) {}