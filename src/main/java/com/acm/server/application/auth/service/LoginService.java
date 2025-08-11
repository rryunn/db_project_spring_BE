//구글 로그인 검증(구글 access토큰 가지고) 후 회원가입 or 로그인처리 성공 시 토큰 발급
package com.acm.server.application.auth.service;

import org.springframework.stereotype.Service;

import com.acm.server.adapter.in.dto.LoginDto;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.auth.port.in.LoginUseCase;
import com.acm.server.application.auth.port.out.LoginUserPort;
import com.acm.server.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {
    private final LoginUserPort loginUserPort;

    @Override
    public Response login(LoginDto loginDto) throws Exception {
        // 유저 정보 찾기
        User user = findByUserId(loginDto);
        if(user == null) {
            return new Response(200, "USER_NOT_EXIST");
        }

        return new Response(100, "LOGIN_SUCCESS", user);
    }

    public User findByUserId(LoginDto loginDto) throws Exception {
        Long userId = loginDto.getUserId();
        return loginUserPort.findByUserId(userId);
    }
}
