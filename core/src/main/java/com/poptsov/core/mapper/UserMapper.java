package com.poptsov.core.mapper;

import com.poptsov.core.model.User;
import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.dto.AuthRequest;
import com.poptsov.core.dto.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {java.math.BigDecimal.class, java.time.LocalDate.class}, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Преобразование из RegisterDto в User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User registerDtoToUser(RegisterDto registerDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User authRequestToUser(AuthRequest authRequest);

    AuthResponse userToAuthResponse(User user);
}