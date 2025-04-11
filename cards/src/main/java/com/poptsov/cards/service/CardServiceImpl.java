package com.poptsov.cards.service;

import com.poptsov.core.repository.UserRepository;
import com.poptsov.cards.repository.CardRepository;
import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.mapper.CardMapper;
import com.poptsov.core.model.Card;
import com.poptsov.core.model.User;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.CardNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardNumberEncryptor encryptor;
    private final UserRepository userRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final CardMapper cardMapper;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, CardNumberEncryptor encryptor, UserRepository userRepository, CardNumberGenerator cardNumberGenerator, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.encryptor = encryptor;
        this.userRepository = userRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.cardMapper = cardMapper;
    }

    public CardResponseDto createCard(CardCreateDto dto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        String cardNumber = cardNumberGenerator.generate();

        Card card = new Card();
        card.setUser(user);
        card.setCardNumberEncrypted(encryptor.encrypt(cardNumber));
        card.setCardNumberMasked(encryptor.mask(cardNumber));
        card.setHolderName(dto.getHolderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setBalance(dto.getInitialBalance());
        card.setStatus("ACTIVE");

        cardRepository.save(card);
        return cardMapper.toResponseDto(card);
    }

    public CardResponseDto getCardForUser(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId).orElseThrow();
        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Card doesn't belong to the user");
        }
        return cardMapper.toResponseDto(card);
    }

    @Override
    public void blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId).orElseThrow();
        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Card doesn't belong to the user");
        }
        card.setStatus("BLOCKED");
        cardRepository.save(card);
    }
}