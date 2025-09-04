package com.acm.server.application.user.service;

import com.acm.server.adapter.in.security.GoogleIdTokenVerifierService;
import com.acm.server.adapter.in.security.TokenProvider;
import com.acm.server.application.user.dto.TokenResponse;
import com.acm.server.application.user.port.in.GoogleLoginUseCase;
import com.acm.server.application.user.port.out.RefreshTokenPort;
import com.acm.server.application.user.port.out.UserRepositoryPort;
import com.acm.server.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements GoogleLoginUseCase{
    private final GoogleIdTokenVerifierService googleVerifier;
    private final TokenProvider tokenProvider;
    private final RefreshTokenPort rtPort;
    private final UserRepositoryPort userRepo;
    @Value("${jwt.refresh-token-validity-seconds}") long refreshTtl;

    @Transactional
    public TokenResponse loginWithGoogle(String idToken) throws Exception {
        var info = googleVerifier.verify(idToken);
        if (!info.isEmailVerified()) throw new IllegalArgumentException("Email not verified");

        // 존재하면 업데이트, 없으면 생성
        User user = userRepo.findByGoogleId(info.getSub())
                .map(u -> u.updateFromGoogle(info.getEmail(), info.getName(), info.getPicture()))
                .orElseGet(() -> User.createFromGoogle(info.getSub(), info.getEmail(), info.getName(), info.getPicture()));

        user = userRepo.save(user); // 도메인 저장

        String userId = String.valueOf(user.getId());
        String sid = UUID.randomUUID().toString();

        String at = tokenProvider.createAccessToken(userId, user.rolesAsStrings());
        String rt = tokenProvider.createRefreshToken(userId, sid);
        rtPort.save(userId, sid, rt, Duration.ofSeconds(refreshTtl));

        return new TokenResponse(at, buildRtCookie(rt).toString());
    }

    @Transactional
    public TokenResponse refresh(String rtCookie) {
        String userId = tokenProvider.getUserId(rtCookie);
        String oldSid = tokenProvider.getSessionId(rtCookie);

        if (!rtPort.exists(userId, oldSid, rtCookie)) {
            rtPort.deleteAllOfUser(userId);
            throw new IllegalArgumentException("Refresh token reused");
        }

        rtPort.delete(userId, oldSid);
        String newSid = UUID.randomUUID().toString();
        String newRt = tokenProvider.createRefreshToken(userId, newSid);
        rtPort.save(userId, newSid, newRt, Duration.ofSeconds(refreshTtl));

        var roles = userRepo.findById(Long.parseLong(userId))
                .map(User::rolesAsStrings).orElse(List.of("ROLE_USER"));
        String at = tokenProvider.createAccessToken(userId, roles);

        return new TokenResponse(at, buildRtCookie(newRt).toString());
    }

    @Transactional
    public void logout(String rtCookie) {
        if (rtCookie == null) return;
        try {
            String userId = tokenProvider.getUserId(rtCookie);
            String sid = tokenProvider.getSessionId(rtCookie);
            rtPort.delete(userId, sid);
        } catch (Exception ignore) {}
    }

    private ResponseCookie buildRtCookie(String rt) {
        return ResponseCookie.from("RT", rt)
                .httpOnly(true).secure(false).sameSite("Lax")
                .path("/api/auth").maxAge(refreshTtl).build();
    }
}
