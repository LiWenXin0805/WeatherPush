package com.lwx.weatherpush.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BusinessException extends RuntimeException {

    private final Integer code;

    private final String message;

    public BusinessException(String message) {
        super(message);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = message;
    }

    public BusinessException(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message);
    }
}
