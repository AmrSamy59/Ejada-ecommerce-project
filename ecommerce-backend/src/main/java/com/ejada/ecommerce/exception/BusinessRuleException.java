package com.ejada.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends AppException {
    public BusinessRuleException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
