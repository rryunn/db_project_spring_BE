package com.acm.server.adapter.in.rest;

import com.acm.server.application.user.port.in.GoogleLoginUseCase;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/**
 * ✅ 인증 관련 API 컨트롤러
 * - /google : 구글 id_token → 검증 후 우리 JWT 발급 (AT+RT)
 * - /refresh: RefreshToken 로테이션 후 AccessToken 재발급
 * - /logout : RefreshToken 삭제
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final GoogleLoginUseCase googleLoginUseCase;
    public record GoogleLoginRequest(@NotBlank String idToken) {}

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody GoogleLoginRequest req) {
        try {
            var tokenResponse = googleLoginUseCase.loginWithGoogle(req.idToken());
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenResponse.getRefreshTokenCookie())
                .body(Map.of("accessToken", tokenResponse.getAccessToken())); 
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "RT", required = false) String rtCookie) {
        if (rtCookie == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "No refresh token"));
        }
        try {
            var tokenResponse = googleLoginUseCase.refresh(rtCookie);
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenResponse.getRefreshTokenCookie())
                .body(Map.of("accessToken", tokenResponse.getAccessToken())); 
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "RT", required = false) String rtCookie) {
        googleLoginUseCase.logout(rtCookie);
        ResponseCookie clear = ResponseCookie.from("RT", "")
                .httpOnly(true).secure(true).sameSite("None")
                .path("/api/auth").maxAge(0).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clear.toString())
                .body(java.util.Map.of("message", "logged out"));
    }
}

