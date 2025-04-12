package com.poptsov.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;
@Schema(description = "Роли пользователей")
public enum Role implements GrantedAuthority {
    @Schema(description = "Администратор") ADMIN,
    @Schema(description = "Пользователь") USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
