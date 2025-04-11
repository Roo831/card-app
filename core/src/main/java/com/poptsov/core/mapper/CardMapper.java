package com.poptsov.core.mapper;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Mapping(source = "cardNumberMasked", target = "maskedNumber")
    CardResponseDto toResponseDto(Card card);

    Card toEntity(CardCreateDto cardCreateDto);
}