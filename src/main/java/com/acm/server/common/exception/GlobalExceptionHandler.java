package com.acm.server.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== 커스텀 예외 =====
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e,
                                                        HttpServletRequest req) {
        ErrorResponse body = ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e,
                                                            HttpServletRequest req) {
        ErrorResponse body = ErrorResponse.of(ErrorCode.UNAUTHORIZED, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e,
                                                         HttpServletRequest req) {
        ErrorResponse body = ErrorResponse.of(ErrorCode.FORBIDDEN, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e,
                                                        HttpServletRequest req) {
        ErrorResponse body = ErrorResponse.of(ErrorCode.BUSINESS_ERROR, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // ===== 스프링/검증 예외 =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                      HttpServletRequest req) {
        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err ->
                details.put(err.getField(), err.getDefaultMessage()));
        ErrorResponse body = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, null, req.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e,
                                                                   HttpServletRequest req) {
        Map<String, Object> details = new HashMap<>();
        e.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), v.getMessage()));
        ErrorResponse body = ErrorResponse.of(ErrorCode.VALIDATION_ERROR, null, req.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e,
                                                            HttpServletRequest req) {
        Map<String, Object> details = Map.of(e.getParameterName(), "required");
        ErrorResponse body = ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getMessage(), req.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e,
                                                            HttpServletRequest req) {
        Map<String, Object> details = Map.of(
                "name", e.getName(),
                "requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown",
                "value", String.valueOf(e.getValue())
        );
        ErrorResponse body = ErrorResponse.of(ErrorCode.BAD_REQUEST, "파라미터 타입이 올바르지 않습니다.", req.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
                                                                HttpServletRequest req) {
        ErrorResponse body = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    // ===== 마지막 방어선 =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception e, HttpServletRequest req) {
        // 필요시 로깅: log.error("Unhandled exception", e);
        ErrorResponse body = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
