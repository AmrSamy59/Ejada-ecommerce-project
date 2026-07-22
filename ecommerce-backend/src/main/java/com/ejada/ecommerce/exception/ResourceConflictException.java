package com.ejada.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends AppException {
    public ResourceConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
