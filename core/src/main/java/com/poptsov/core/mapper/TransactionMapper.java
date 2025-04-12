package com.poptsov.core.mapper;

import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.model.Transaction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.poptsov.core.util.CardNumberEncryptor;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "sourceCardMask", expression = "java(cardNumberEncryptor.mask(transaction.getSourceCard().getCardNumberEncrypted()))")
    @Mapping(target = "targetCardMask", expression = "java(transaction.getTargetCard() != null ? cardNumberEncryptor.mask(transaction.getTargetCard().getCardNumberEncrypted()) : null)")
    @Mapping(target = "type", expression = "java(transaction.getType().name())")
    @Mapping(target = "status", expression = "java(transaction.getStatus().name())")
    TransactionResponseDto toResponseDto(Transaction transaction, @Context CardNumberEncryptor cardNumberEncryptor);
}