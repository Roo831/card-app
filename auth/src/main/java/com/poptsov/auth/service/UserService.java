package com.poptsov.auth.service;

import com.poptsov.core.dto.AuthResponse;
import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.dto.UserChangeRoleDto;
import com.poptsov.core.model.User;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User createUser(RegisterDto request);

    AuthResponse changeRole(@Valid UserChangeRoleDto dto);
}