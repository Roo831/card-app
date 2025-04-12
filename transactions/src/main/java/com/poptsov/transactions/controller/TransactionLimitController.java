ackage com.poptsov.transactions.controller;

import com.poptsov.core.dto.SetLimitRequestDto;
import com.poptsov.transactions.service.TransactionLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/limits")
@Tag(name = "Лимиты транзакций", description = "API для управления лимитами транзакций")
public class TransactionLimitController {

    private final TransactionLimitService limitService;

    @Autowired
    public TransactionLimitController(TransactionLimitService limitService) {
        this.limitService = limitService;
    }

    @Operation(summary = "Установка лимита транзакций", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лимит успешно установлен"),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setLimit(
            @Valid @RequestBody SetLimitRequestDto request
    ) {
        limitService.setLimit(request);
        return ResponseEntity.ok().build();
    }
}