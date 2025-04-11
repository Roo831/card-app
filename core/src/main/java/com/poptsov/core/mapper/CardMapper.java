package com.poptsov.core.mapper;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardResponseDto toResponseDto(Card card);

    // Если преобразование не нужно, можно удалить
    default Card toEntity(CardCreateDto dto) {
        return null;
    }
}