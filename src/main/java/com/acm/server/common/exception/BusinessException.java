package com.acm.server.common.exception;

/** 도메인/비즈니스 규칙 위반 (409 등으로 매핑) */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
