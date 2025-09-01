package com.acm.server.application.user.port.in;

import com.acm.server.adapter.in.dto.TokenResponse;

public interface GoogleLoginUseCase {
    TokenResponse loginWithGoogle(String idToken);
    public TokenResponse refresh(String rtCookie);
    public void logout(String rtCookie);
}
