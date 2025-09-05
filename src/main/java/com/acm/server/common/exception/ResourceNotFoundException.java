package com.acm.server.common.exception;

/** 리소스를 찾을 수 없는 경우 (404) */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
