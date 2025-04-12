package com.poptsov.transactions.service;

import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.dto.TransferRequestDto;
import com.poptsov.core.exception.CardAccessDeniedException;
import com.poptsov.core.exception.CardNotFoundException;
import com.poptsov.core.exception.InsufficientFundsException;
import com.poptsov.core.mapper.TransactionMapper;
import com.poptsov.core.model.Card;
import com.poptsov.core.model.Transaction;
import com.poptsov.core.model.TransactionStatus;
import com.poptsov.core.model.TransactionType;
import com.poptsov.core.repository.CardRepository;
import com.poptsov.core.repository.TransactionRepository;
import com.poptsov.core.util.CardNumberEncryptor;
import com.poptsov.core.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface TransactionService {


    TransactionResponseDto transferBetweenCards(TransferRequestDto request);

    Page<TransactionResponseDto> getCardTransactions(Long cardId, Pageable pageable);

    Page<TransactionResponseDto> convertToDtoPage(Page<Transaction> transactions, CardNumberEncryptor cardNumberEncryptor);
}
