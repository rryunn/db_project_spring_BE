// application/user/port/out/LoadUserPort.java
package com.acm.server.application.user.port.out;

import com.acm.server.domain.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadById(Long userId);
}
