//google로그인이랑 연결해야됨
package com.acm.server.application.auth.port.out;

import com.acm.server.domain.User;

public interface LoginUserPort {
    public User findByUserId(String userId);
}
