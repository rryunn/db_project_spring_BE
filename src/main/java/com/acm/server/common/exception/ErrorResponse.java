package com.acm.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final int status;      // HTTP status code
    private final String code;     // enum name (e.g., RESOURCE_NOT_FOUND)
    private final String message;  // 최종 사용자 메시지 (예외 메시지 우선)
    private final String path;     // 요청 경로
    private final Instant timestamp;
    private final Map<String, Object> details; // (선택) 필드 에러 등 추가 정보

    public static ErrorResponse of(ErrorCode code, String overrideMessage, String path) {
        String msg = (overrideMessage == null || overrideMessage.isBlank())
                ? code.getMessage() : overrideMessage;
        return ErrorResponse.builder()
                .status(code.getStatus())
                .code(code.name())
                .message(msg)
                .path(path)
                .timestamp(Instant.now())
                .details(null)
                .build();
    }

    public static ErrorResponse of(ErrorCode code, String overrideMessage, String path, Map<String, Object> details) {
        String msg = (overrideMessage == null || overrideMessage.isBlank())
                ? code.getMessage() : overrideMessage;
        return ErrorResponse.builder()
                .status(code.getStatus())
                .code(code.name())
                .message(msg)
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }
}
