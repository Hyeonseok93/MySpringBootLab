package com.rookies5.myspringbootlab.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BOOK_NOT_FOUND("도서를 찾을 수 없습니다: %s", HttpStatus.NOT_FOUND),
    ISBN_DUPLICATE("이미 존재하는 ISBN입니다: %s", HttpStatus.CONFLICT);

    private final String messageFormat;
    private final HttpStatus httpStatus;

    public String getFormattedMessage(Object... args) {
        return String.format(messageFormat, args);
    }
}
