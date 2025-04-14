package com.poptsov.core.util;

import com.poptsov.core.exception.EntityNotFoundException;

import com.poptsov.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("No authenticated user found in security context");
            throw new EntityNotFoundException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            log.error("Invalid principal type: {}", principal.getClass().getName());
            throw new EntityNotFoundException("Invalid user principal");
        }

        log.trace("Retrieved current user: {}", user.getId());
        return user;
    }
}