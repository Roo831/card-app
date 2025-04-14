package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.exception.CardAccessDeniedException;
import com.poptsov.core.exception.UserNotFoundException;
import com.poptsov.core.mapper.CardMapper;
import com.poptsov.core.mapper.TransactionMapper;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionRepository;
import com.poptsov.core.repository.UserRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.CardNumberGenerator;
import com.poptsov.core.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CardNumberGenerator numberGenerator;
    @Mock
    private CardNumberEncryptor encryptor;
    @Mock
    private CardMapper cardMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private SecurityUtils securityUtils;

    @Test
    void createCard_shouldCreateNewCard() {
        CardCreateDto dto = new CardCreateDto(1L, "IVAN IVANOV",
                LocalDate.now().plusYears(2), BigDecimal.valueOf(1000));

        User user = new User();
        user.setId(1L);

        Card savedCard = Card.builder()
                .id(1L)
                .user(user)
                .cardNumberEncrypted("encrypted")
                .cardNumberMasked("**** **** **** 3456")
                .holderName("IVAN IVANOV")
                .expirationDate(LocalDate.now().plusYears(2))
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .build();

        CardResponseDto responseDto = new CardResponseDto(
                1L, "**** **** **** 3456", "IVAN IVANOV",
                LocalDate.now().plusYears(2), BigDecimal.valueOf(1000), CardStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(numberGenerator.generate()).thenReturn("1234 5678 9012 3456");
        when(encryptor.encrypt(any())).thenReturn("encrypted");
        when(encryptor.mask(any())).thenReturn("**** **** **** 3456");
        when(cardRepository.save(any())).thenReturn(savedCard);
        when(cardMapper.toResponseDto(any())).thenReturn(responseDto);

        var result = cardService.createCard(dto);

        assertNotNull(result);
        assertEquals("**** **** **** 3456", result.maskedNumber());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponseDto(savedCard);
    }

    @Test
    void createCard_shouldThrowExceptionWhenUserNotFound() {
        CardCreateDto dto = new CardCreateDto(1L, "IVAN IVANOV",
                LocalDate.now().plusYears(2), BigDecimal.valueOf(1000));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.createCard(dto));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void getAllCards_shouldReturnFilteredByStatus() {
        String status = "ACTIVE";
        Pageable pageable = Pageable.unpaged();
        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findByStatus(CardStatus.valueOf(status), pageable)).thenReturn(page);
        when(cardMapper.toResponseDto(any())).thenReturn(new CardResponseDto(1L, "masked", "holder",
                LocalDate.now(), BigDecimal.ZERO, CardStatus.ACTIVE));

        var result = cardService.getAllCards(status, pageable);

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByStatus(CardStatus.valueOf(status), pageable);
    }

    @Test
    void getAllCards_shouldReturnAllWhenNoStatus() {

        Pageable pageable = Pageable.unpaged();
        Card card = new Card();
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(pageable)).thenReturn(page);
        when(cardMapper.toResponseDto(any())).thenReturn(new CardResponseDto(1L, "masked", "holder",
                LocalDate.now(), BigDecimal.ZERO, CardStatus.ACTIVE));

        var result = cardService.getAllCards(null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void changeCardStatus_shouldUpdateStatus() {
        Long cardId = 1L;
        CardStatus newStatus = CardStatus.BLOCKED;
        Card card = new Card();
        card.setExpirationDate(LocalDate.now().plusDays(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponseDto(any())).thenReturn(new CardResponseDto(1L, "masked", "holder",
                LocalDate.now(), BigDecimal.ZERO, newStatus));

        var result = cardService.changeCardStatus(cardId, newStatus);

        assertEquals(newStatus, result.status());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void changeCardStatus_shouldThrowWhenActivatingExpiredCard() {

        Long cardId = 1L;
        CardStatus newStatus = CardStatus.ACTIVE;
        Card card = new Card();
        card.setExpirationDate(LocalDate.now().minusDays(1));

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(IllegalStateException.class,
                () -> cardService.changeCardStatus(cardId, newStatus));
    }

    @Test
    void getUserCards_shouldReturnUserCards() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Card card = new Card();
        card.setId(1L);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(cardRepository.findByUserId(userId)).thenReturn(List.of(card));
        when(cardMapper.toResponseDto(any())).thenReturn(new CardResponseDto(1L, "masked", "holder",
                LocalDate.now(), BigDecimal.ZERO, CardStatus.ACTIVE));

        var result = cardService.getUserCards();

        assertEquals(1, result.size());
        verify(cardRepository).findByUserId(userId);
    }


    @Test
    void userBlockCard_shouldBlockOwnCard() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Card card = new Card();
        card.setId(1L);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(cardRepository.findByIdAndUserId(card.getId(), userId)).thenReturn(Optional.of(card));
        when(cardMapper.toResponseDto(any())).thenReturn(new CardResponseDto(1L, "masked", "holder",
                LocalDate.now(), BigDecimal.ZERO, CardStatus.BLOCKED));

        var result = cardService.userBlockCard(card.getId());

        assertEquals(CardStatus.BLOCKED, result.status());
        verify(cardRepository).findByIdAndUserId(card.getId(), userId);
    }

    @Test
    void userBlockCard_shouldThrowForForeignCard() {
        Long cardId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        assertThrows(CardAccessDeniedException.class,
                () -> cardService.userBlockCard(cardId));
    }
    @Test
    void getCardTransactions_shouldReturnOwnTransactions() {
        Long cardId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        String sourceCardMask = "**** **** **** 1234";
        String targetCardMask = "**** **** **** 1235";
        Card sourceCard = new Card();
        sourceCard.setCardNumberMasked(sourceCardMask);
        Card targetCard = new Card();
        targetCard.setCardNumberMasked(targetCardMask);
        sourceCard.setId(cardId);

        Long transactionId = 1L;
        TransactionType transactionType = TransactionType.TRANSFER;
        TransactionStatus status = TransactionStatus.COMPLETED;
        String description = "description";
        LocalDateTime testTime = LocalDateTime.now();


        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setSourceCard(sourceCard);
        transaction.setTargetCard(targetCard);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setType(transactionType);
        transaction.setType(transactionType);
        transaction.setStatus(status);
        transaction.setDescription(description);
        transaction.setCreatedAt(testTime);

        TransactionResponseDto transactionResponseDto = new TransactionResponseDto(
                transactionId, sourceCardMask, targetCardMask, BigDecimal.TEN, transactionType.name(), status.name(), description, testTime
        );


        Pageable pageable = Pageable.unpaged();

        Page<Transaction> page = new PageImpl<>(List.of(transaction));


        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(cardRepository.existsByIdAndUserId(cardId, userId)).thenReturn(true);
        when(transactionRepository.findBySourceCardId(cardId, pageable)).thenReturn(page);
        when(transactionMapper.toResponseDto(transaction, encryptor)).thenReturn(transactionResponseDto);
        var result = cardService.getCardTransactions(cardId, pageable);

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).existsByIdAndUserId(cardId, userId);
    }

    @Test
    void getCardTransactions_shouldThrowForForeignCard() {

        Long cardId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Pageable pageable = Pageable.unpaged();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(cardRepository.existsByIdAndUserId(cardId, userId)).thenReturn(false);

        assertThrows(CardAccessDeniedException.class,
                () -> cardService.getCardTransactions(cardId, pageable));
    }
}