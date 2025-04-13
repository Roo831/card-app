package com.poptsov.auth.service;

import com.poptsov.core.dto.AuthRequest;
import com.poptsov.core.dto.AuthResponse;
import com.poptsov.core.dto.RegisterDto;
import com.poptsov.core.model.User;
import com.poptsov.core.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateNewUser() {
        RegisterDto dto = new RegisterDto("test@example.com", "password");
        User mockUser = new User();
        mockUser.setEmail(dto.email());
        mockUser.setRole(Role.USER);

        when(userService.createUser(dto)).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("jwtToken");

        AuthResponse response = authService.register(dto);

        verify(userService).createUser(dto);
        assertEquals("jwtToken", response.token());
    }

    @Test
    void authenticate_shouldReturnToken() {
        AuthRequest request = new AuthRequest("test@example.com", "password");
        User mockUser = new User();
        mockUser.setEmail(request.email());

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userService.loadUserByUsername(request.email())).thenReturn(mockUser);
        when(jwtService.generateToken(mockUser)).thenReturn("jwtToken");

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).loadUserByUsername(request.email());
        assertEquals("jwtToken", response.token());
    }
}