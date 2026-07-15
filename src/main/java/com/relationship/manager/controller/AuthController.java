package com.relationship.manager.controller;

import com.relationship.manager.common.ApiResponse;
import com.relationship.manager.dto.LoginRequest;
import com.relationship.manager.dto.LoginResponse;
import com.relationship.manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.wechatLogin(request.getCode()));
    }
}
