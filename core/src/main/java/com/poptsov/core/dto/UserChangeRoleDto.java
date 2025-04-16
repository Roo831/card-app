package com.poptsov.core.dto;

import com.poptsov.core.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на смену роли")
public record UserChangeRoleDto (
    @Schema(description = "Целевая роль") Role role
 ){}
