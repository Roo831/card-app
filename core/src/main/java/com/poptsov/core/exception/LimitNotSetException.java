package com.poptsov.core.exception;

import com.poptsov.core.model.LimitType;

public class LimitNotSetException extends RuntimeException {
    private final LimitType limitType;

    public LimitNotSetException(LimitType limitType) {
        super(String.format("Limit not set for type: %s", limitType));
        this.limitType = limitType;
    }

    public LimitType getLimitType() {
        return limitType;
    }
}