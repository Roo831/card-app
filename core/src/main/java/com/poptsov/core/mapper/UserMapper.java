package com.poptsov.core.mapper;

import com.poptsov.core.model.User;
import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.dto.AuthRequest;
import com.poptsov.core.dto.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User registerDtoToUser(RegisterDto registerDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User authRequestToUser(AuthRequest authRequest);

    @Mapping(target = "token", ignore = true)
    AuthResponse userToAuthResponse(User user);
}