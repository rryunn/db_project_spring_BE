// application/user/service/FindUserService.java
package com.acm.server.application.user.service;

import com.acm.server.application.user.port.in.FindUserUseCase;
import com.acm.server.application.user.port.out.LoadUserPort;
import com.acm.server.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindUserService implements FindUserUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public User getMyInfo(Long userId) {
        return loadUserPort.loadById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));
    }
}
