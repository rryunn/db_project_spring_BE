package com.acm.server.application.user.port.out;

import java.util.List;

public interface MemberRepositoryPort {
    List<Long> findOperatorClubIdsByUserId(Long userId);
}
