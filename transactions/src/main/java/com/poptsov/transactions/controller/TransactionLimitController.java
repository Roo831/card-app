package com.poptsov.transactions.controller;

import com.poptsov.core.dto.SetLimitRequestDto;
import com.poptsov.transactions.service.TransactionLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards/limits")

public class TransactionLimitController {

    private final TransactionLimitService limitService;

    @Autowired
    public TransactionLimitController(TransactionLimitService limitService) {
        this.limitService = limitService;
    }

    @Operation(summary = "Set transaction limit", description = "Available only for ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limit set successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Card not found")
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