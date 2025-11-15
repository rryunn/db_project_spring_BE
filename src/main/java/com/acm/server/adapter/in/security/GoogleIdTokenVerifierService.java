package com.acm.server.adapter.in.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.id.Audience;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 *  구글에서 발급한 id_token 을 서버에서 직접 검증하는 서비스
 * - iss: 반드시 "https://accounts.google.com" 또는 "accounts.google.com"
 * - aud: 우리 앱의 client_id(화이트리스트)
 * - exp: 만료시간 확인
 * - 서명: 구글 공개키(JWKS)로 RS256 검증
 */
@Component
public class GoogleIdTokenVerifierService {

    // 유효한 issuer 값
    private static final Set<String> VALID_ISS =
            Set.of("https://accounts.google.com", "accounts.google.com");

    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private final List<Audience> allowedAud;

    /**
     * 생성자에서 Google 공개키(JWKS) 주소 등록 + audience 화이트리스트 설정
     */
    public GoogleIdTokenVerifierService(
            @Value("${google.oauth.client-ids}") List<String> clientIds) throws Exception {

        // 구글이 제공하는 공개키(JWK) 세트 주소
        var jwkSetURL = new URL("https://www.googleapis.com/oauth2/v3/certs");
        var keySource = new RemoteJWKSet<SecurityContext>(jwkSetURL);

        // RS256 알고리즘으로만 검증 허용
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);

        var processor = new DefaultJWTProcessor<SecurityContext>();
        processor.setJWSKeySelector(keySelector);
        this.jwtProcessor = processor;

        // 허용할 audience 목록 (google cloud console 에서 발급받은 clientId)
        this.allowedAud = clientIds.stream().map(Audience::new).toList();
    }

    /**
     * id_token 검증 후 사용자 정보를 담은 DTO 반환
     */
    public GoogleUserInfo verify(String idToken) throws Exception {
        JWTClaimsSet claims = jwtProcessor.process(idToken, null);

        // iss 체크
        if (!VALID_ISS.contains(claims.getIssuer())) {
            throw new IllegalArgumentException("Invalid issuer");
        }

        // aud 체크
        List<String> audiences = claims.getAudience();
        boolean audOk = audiences.stream().anyMatch(a ->
                allowedAud.stream().anyMatch(x -> x.getValue().equals(a)));
        if (!audOk) throw new IllegalArgumentException("Invalid audience");

        // exp 체크
        if (Instant.now().isAfter(claims.getExpirationTime().toInstant())) {
            throw new IllegalArgumentException("Expired token");
        }

        Boolean emailVerified = (Boolean) claims.getClaim("email_verified");

        return new GoogleUserInfo(
                claims.getSubject(),                          // sub: 구글 고유 사용자ID
                (String) claims.getClaim("email"),           // 이메일
                emailVerified != null && emailVerified,      // 이메일 검증 여부
                (String) claims.getClaim("name"),            // 이름
                (String) claims.getClaim("picture")          // 프로필 사진
        );
    }

    /**
     * 구글 사용자 정보 DTO
     */
    @Getter
    public static class GoogleUserInfo {
        private final String sub;
        private final String email;
        private final boolean emailVerified;
        private final String name;
        private final String picture;

        public GoogleUserInfo(String sub, String email, boolean emailVerified, String name, String picture) {
            this.sub = sub;
            this.email = email;
            this.emailVerified = emailVerified;
            this.name = name;
            this.picture = picture;
        }
    }
}
