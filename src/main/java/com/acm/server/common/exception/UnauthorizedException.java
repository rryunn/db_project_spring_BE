package com.acm.server.common.exception;

/** 인증되지 않은 경우 (401) */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}
