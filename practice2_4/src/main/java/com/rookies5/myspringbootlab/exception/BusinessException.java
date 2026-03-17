package com.rookies5.myspringbootlab.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String message;
    private final HttpStatus httpStatus;

    public BusinessException(String message) {
        this(message, HttpStatus.EXPECTATION_FAILED);
    }

    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.message = errorCode.getFormattedMessage(args);
        this.httpStatus = errorCode.getHttpStatus();
    }
}
