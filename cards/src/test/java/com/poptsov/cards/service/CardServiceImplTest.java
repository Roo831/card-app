package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.exception.UserNotFoundException;
import com.poptsov.core.mapper.CardMapper;
import com.poptsov.core.model.*;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.UserRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.CardNumberGenerator;
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
    private CardNumberGenerator numberGenerator;
    @Mock
    private CardNumberEncryptor encryptor;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

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
}