package com.acm.server.adapter.in.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * JWT 기반의 완전 Stateless 보안 설정.
 * - 세션 미사용
 * - 허용 경로: /api/auth/google, /api/auth/refresh, /api/auth/logout, /actuator/health
 * - 기타 경로는 AT 필요 (Authorization: Bearer <AT>)
 * - 인증/인가 실패 시 JSON 응답
 * - JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞단에 삽입
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationEntryPoint authEntryPoint;   // 401
    private final AccessDeniedHandler accessDeniedHandler;   // 403

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 세션 비활성화 (모든 요청은 JWT로 인증)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // CSRF는 쿠키-세션 기반이 아니라면 보통 비활성화
            .csrf(csrf -> csrf.disable())
            // CORS 기본 동작: 아래 corsConfigurationSource() 사용
            .cors(Customizer.withDefaults())
            // 경로별 접근 제어
            .authorizeHttpRequests(req -> req
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/auth/google", "/api/auth/refresh", "/api/auth/logout").permitAll()
                // Swagger는 운영에서 막거나 ADMIN 제한 권장
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 인증/인가 실패 핸들러
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // JWT 필터 삽입
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 정책:
     * - allowCredentials(true): RT를 HttpOnly 쿠키로 주고받기 위함
     * - AllowedOriginPatterns: 실제 프런트 도메인으로 교체
     * - Authorization 헤더 노출(ExposedHeaders)로 클라에서 AT 읽기 가능
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        // TODO: 운영 시 반드시 실제 프런트 도메인만 허용
        cfg.setAllowedOriginPatterns(List.of(
            "https://*.your-domain.com",
            "https://your-gh-pages.github.io",
            "http://localhost:5173",          // 로컬 개발용(예시)
            "http://localhost:3000"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        cfg.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
