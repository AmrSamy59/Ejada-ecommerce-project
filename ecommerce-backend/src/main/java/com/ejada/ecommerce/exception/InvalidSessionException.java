package com.ejada.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidSessionException extends AppException {
    public InvalidSessionException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
