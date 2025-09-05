package com.acm.server.common.exception;

/** 권한이 부족한 경우 (403) */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
