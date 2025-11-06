package com.acm.server.adapter.in.rest;

import com.acm.server.adapter.in.dto.MyInfoResponse;
import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.application.user.port.in.FindUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final FindUserUseCase findUserUseCase;

    // access token decode해서 안에 있는 user_id로 수행, 따로 user_id 받지않음
    /** GET /api/users/me */
    @GetMapping("/me")
    public MyInfoResponse getMyInfo(@AuthenticationPrincipal JwtUserPrincipal principal) {
        var user = findUserUseCase.getMyInfo(principal.userId());
        return MyInfoResponse.from(user);
    }
}