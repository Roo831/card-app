package com.poptsov.core.exception;

import com.poptsov.core.model.LimitType;

public class LimitExpiredException extends RuntimeException {
    private final LimitType limitType;

    public LimitExpiredException(LimitType limitType) {
        super(String.format("Limit expired for type: %s", limitType));
        this.limitType = limitType;
    }

    public LimitType getLimitType() {
        return limitType;
    }
}