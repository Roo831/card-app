package com.poptsov.auth.service;

import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User createUser(RegisterDto request);
}