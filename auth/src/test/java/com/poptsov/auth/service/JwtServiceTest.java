package com.poptsov.auth.service;

import com.poptsov.core.model.User;
import com.poptsov.core.model.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "p5e+9Z2R1mKXJgH6u6o8tM5jK1z8o9QWm8H8yT9f2kJ=";
    private final long expiration = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(user.getEmail(), claims.getSubject());
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        User user = new User();
        user.setEmail("test@example.com");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        User user = new User();
        user.setEmail("test@example.com");
        String invalidToken = "invalid.token.here";

        try {
            boolean isValid = jwtService.isTokenValid(invalidToken, user);
            assertFalse(isValid);
        } catch (Exception e) {
            assertTrue(e instanceof io.jsonwebtoken.security.SignatureException ||
                    e instanceof io.jsonwebtoken.MalformedJwtException);
        }
    }
    @Test
    void isTokenExpired_shouldReturnTrueForExpiredToken() throws InterruptedException {

        ReflectionTestUtils.setField(jwtService, "expiration", 1L);

        User user = new User();
        user.setEmail("test@example.com");
        String token = jwtService.generateToken(user);

        Thread.sleep(2);
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        String token = jwtService.generateToken(user);

        assertEquals(user.getEmail(), jwtService.extractEmail(token));
    }

    @Test
    void token_shouldContainCustomClaims() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ADMIN);
        user.setId(123L);

        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals(Role.ADMIN.name(), claims.get("role"));
        assertEquals(123L, claims.get("id"));
    }

}