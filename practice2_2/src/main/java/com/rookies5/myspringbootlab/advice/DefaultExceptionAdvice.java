package com.rookies5.myspringbootlab.advice;

import com.rookies5.myspringbootlab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice {

//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorObject> handleResourceNotFoundException(BusinessException ex) {
//        ErrorObject errorObject = new ErrorObject();
//        errorObject.setStatusCode(ex.getHttpStatus().value());
//        errorObject.setMessage(ex.getMessage());
//
//        log.error(ex.getMessage(), ex);
//
//        //ResponseEntity = body + statuscode + header
//        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
//    }

    /*
        Spring6 버전에 추가된 ProblemDetail 객체에 에러정보를 담아서 리턴하는 방법
     */
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
        problemDetail.setTitle("Not Found");
        problemDetail.setDetail(e.getMessage());
        //사용자가 임의로 정의하는 에러코드와 값
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss E a", Locale.KOREA)
                        .format(LocalDateTime.now()));
        return problemDetail;
    }

    //숫자타입의 값에 문자열타입의 값을 입력으로 받았을때 발생하는 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(RuntimeException.class)
//    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
//        ErrorObject errorObject = new ErrorObject();
//        //internal server error 코드 - 500
//        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorObject.setMessage(e.getMessage());
//
//        log.error(e.getMessage(), e);
//
//        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
//    }

    // RuntimeException → Exception으로 변경
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
        ErrorObject errorObject = new ErrorObject();
        // 예외 타입에 따라 적절한 status code 동적 결정
        HttpStatus status = resolveHttpStatus(e);
        errorObject.setStatusCode(status.value());
        errorObject.setMessage(e.getMessage());
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(errorObject, status);
    }

    private HttpStatus resolveHttpStatus(Exception e) {
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return HttpStatus.METHOD_NOT_ALLOWED; // 405
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return HttpStatus.UNSUPPORTED_MEDIA_TYPE; // 415
        } else if (e instanceof MissingServletRequestParameterException) {
            return HttpStatus.BAD_REQUEST; // 400
//        } else if (e instanceof NoResourceFoundException) {
//            return HttpStatus.NOT_FOUND; // 404
//        } else if (e instanceof AccessDeniedException) {
//            return HttpStatus.FORBIDDEN; // 403
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // 500 (기본값) }
    }
}