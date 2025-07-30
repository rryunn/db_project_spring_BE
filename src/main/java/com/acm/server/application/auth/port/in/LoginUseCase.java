package com.acm.server.application.auth.port.in;

import com.acm.server.adapter.in.dto.LoginDto;
import com.acm.server.adapter.in.response.Response;

public interface LoginUseCase {
    public Response login(LoginDto loginDto) throws Exception;
}
