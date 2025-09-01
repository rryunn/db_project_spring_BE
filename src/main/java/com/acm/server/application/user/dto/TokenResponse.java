package com.acm.server.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 인증 응답 DTO
 * - accessToken: 클라이언트가 Authorization 헤더에 실어 보낼 토큰
 * - refreshTokenCookie: 서버가 Set-Cookie 헤더로 내려줄 RT 쿠키 값
 *   (HttpOnly, Secure, SameSite 등 속성 포함 문자열)
 *
 * 컨트롤러에서는 보통 accessToken만 body에 내려주고,
 * refreshTokenCookie는 Response Header에 넣어줌.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshTokenCookie;
}
