package com.poptsov.auth.controller;


import com.poptsov.auth.service.UserService;
import com.poptsov.core.dto.AuthResponse;
import com.poptsov.core.dto.UserChangeRoleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Выдать любую роль. Необходим только для тестирования",
            description = "Меняет роль и возвращает JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping("/changeRole")
    public ResponseEntity<AuthResponse> changeRole(@Valid @RequestBody UserChangeRoleDto dto) {
        return ResponseEntity.ok(userService.changeRole(dto));
    }
}
