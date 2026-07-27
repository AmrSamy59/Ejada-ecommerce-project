package com.ejada.ecommerce.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {
    USER_NOT_FOUND(1001), // 1xxx not found errors
    PRODUCT_NOT_FOUND(1002),
    PRODUCT_DELETED(1003),
    ORDER_NOT_FOUND(1004),
    USERNAME_ALREADY_EXISTS(2001), // 2xxx already exists errors
    EMAIL_ALREADY_EXISTS(2002),
    PRODUCT_NAME_ALREADY_EXISTS(2003),
    TOKEN_EXPIRED(3001), // 3xxx token errors
    TOKEN_INVALID(3002),
    ACCOUNT_DEACTIVATED(4001), // 4xxx account errors
    INSUFFICIENT_STOCK(5001), // 5xxx business errors
    ORDER_ALREADY_CANCELLED(5002),
    VALIDATION_ERROR(6001), // 6xxx validation errors
    UNAUTHORIZED(7001), // 7xxx auth errors
    ACCESS_DENIED(7002),
    INTERNAL_SERVER_ERROR(8001); // 8xxx internal errors

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
