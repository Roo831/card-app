package com.poptsov.transactions.service;

import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.dto.TransferRequestDto;
import com.poptsov.core.exception.*;
import com.poptsov.core.mapper.TransactionMapper;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransactionLimitService limitService;
    @Mock
    private CardNumberEncryptor encryptor;
    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void setupAuthenticatedUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContext context = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void transferBetweenCards_shouldCompleteTransfer() {
        TransferRequestDto request = new TransferRequestDto(1L, 2L,
                BigDecimal.valueOf(500), "Test transfer");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        Card sourceCard = new Card();
        sourceCard.setId(1L);
        sourceCard.setUser(user);
        sourceCard.setBalance(BigDecimal.valueOf(1000));

        Card targetCard = new Card();
        targetCard.setId(2L);
        targetCard.setBalance(BigDecimal.valueOf(200));

        Transaction transaction = Transaction.builder()
                .sourceCard(sourceCard)
                .targetCard(targetCard)
                .amount(request.amount())
                .build();

        TransactionResponseDto responseDto = new TransactionResponseDto(
                1L, "****1234", "****5678", request.amount(),
                "TRANSFER", "COMPLETED", "Test transfer", null);

        when(cardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(sourceCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(targetCard));
        when(transactionRepository.save(any())).thenReturn(transaction);
        when(transactionMapper.toResponseDto(any(), any())).thenReturn(responseDto);

        setupAuthenticatedUser(user);

        var result = transactionService.transferBetweenCards(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(500), sourceCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), targetCard.getBalance());
        verify(limitService).checkLimit(1L, BigDecimal.valueOf(500), LimitType.DAILY);
        verify(limitService).checkLimit(1L, BigDecimal.valueOf(500), LimitType.MONTHLY);
    }

    @Test
    void transferBetweenCards_shouldThrowWhenInsufficientFunds() {
        TransferRequestDto request = new TransferRequestDto(1L, 2L,
                BigDecimal.valueOf(1500), "Test transfer");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        Card sourceCard = new Card();
        sourceCard.setId(1L);
        sourceCard.setUser(user);
        sourceCard.setBalance(BigDecimal.valueOf(1000));

        when(cardRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(sourceCard));

        setupAuthenticatedUser(user);

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transferBetweenCards(request));

        verify(cardRepository, never()).findById(any());
        verify(limitService, never()).checkLimit(any(), any(), any());
    }
}