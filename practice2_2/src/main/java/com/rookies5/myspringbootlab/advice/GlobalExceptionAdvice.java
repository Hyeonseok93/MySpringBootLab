package com.rookies5.myspringbootlab.advice;

import com.rookies5.myspringbootlab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    //  401 - 인증 실패 (Spring Security)
//    @ExceptionHandler(AuthenticationException.class)
//    protected ResponseEntity<ErrorObject> handleAuthenticationException(AuthenticationException e) {
//        ErrorObject errorObject = new ErrorObject();
//        errorObject.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//        errorObject.setMessage("인증이 필요합니다: " + e.getMessage());
//        log.error(e.getMessage(), e);
//        return new ResponseEntity<>(errorObject, HttpStatus.UNAUTHORIZED);
//    }

    //  403 - 권한 없음 (Spring Security)
//    @ExceptionHandler(AccessDeniedException.class)
//    protected ResponseEntity<ErrorObject> handleAccessDeniedException(AccessDeniedException e) {
//        ErrorObject errorObject = new ErrorObject();
//        errorObject.setStatusCode(HttpStatus.FORBIDDEN.value());
//        errorObject.setMessage("접근 권한이 없습니다: " + e.getMessage());
//        log.error(e.getMessage(), e);
//        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
//    }

    //  400 - 요청 파라미터 누락 (required 파라미터가 없을 때)
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("필수 파라미터가 누락되었습니다: " + ex.getParameterName());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  400 - 요청 바디 파싱 실패 (JSON 형식 오류, 타입 불일치 등)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("요청 데이터 형식이 올바르지 않습니다: " + ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  400 - @Valid, @Validated 유효성 검사 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // 여러 필드 에러를 모두 수집
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("입력값 유효성 검사 실패: " + errors);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  404 - 존재하지 않는 경로 요청
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("요청한 리소스를 찾을 수 없습니다: " + ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  405 - 지원하지 않는 HTTP Method 요청
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String supportedMethods = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().toString()
                : "없음";

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("지원하지 않는 HTTP Method입니다. 지원 메서드: " + supportedMethods);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  415 - 지원하지 않는 Content-Type (예: XML 요청인데 JSON만 처리)
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage("지원하지 않는 미디어 타입입니다: " + ex.getContentType());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errorObject, status);
    }

    //  비즈니스 예외 처리 (커스텀)
    @ExceptionHandler(BusinessException.class)
    protected ProblemDetail handleException(BusinessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
        problemDetail.setTitle("Business Exception");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss E a", Locale.KOREA)
                        .format(LocalDateTime.now()));
        return problemDetail;
    }

    //  500 - 위에서 처리되지 않은 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorObject> handleException(Exception e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage("서버 내부 오류가 발생했습니다: " + e.getMessage());
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}