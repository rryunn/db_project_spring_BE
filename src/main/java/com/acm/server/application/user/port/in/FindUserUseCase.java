// application/user/port/in/FindUserUseCase.java
package com.acm.server.application.user.port.in;

import com.acm.server.domain.User;

public interface FindUserUseCase {
    User getMyInfo(Long userId);
}
