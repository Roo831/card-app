package com.poptsov.auth.service;

import com.poptsov.core.dto.AuthResponse;
import com.poptsov.core.dto.UserChangeRoleDto;
import com.poptsov.core.mapper.UserMapperImpl;
import com.poptsov.core.repository.UserRepository;
import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.model.Role;
import com.poptsov.core.model.User;
import com.poptsov.core.util.SecurityUtils;
import jakarta.persistence.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final SecurityUtils securityUtils;
    private final UserMapperImpl userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityUtils securityUtils, UserMapperImpl userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
    }

    public User createUser(RegisterDto request) {
        log.info("Creating new user: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already in use: {}", request.email());
            throw new EntityExistsException("Email already in use");
        }

        var user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    @Override
    public AuthResponse changeRole(UserChangeRoleDto dto) {
        log.info("Getting user from security context");
        User user = securityUtils.getCurrentUser();
        user.setRole(dto.role());
        log.info("return updated user");
        return userMapper.userToAuthResponse(userRepository.save(user));
    }
}