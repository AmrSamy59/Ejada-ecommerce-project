package com.ejada.ecommerce.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ejada.ecommerce.exception.ErrorCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String error;
    private ErrorCode errorCode;
    private Object message;
}
