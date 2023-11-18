package com.example.coinserver.auth.controller;

import com.example.coinserver.auth.annotation.LoginAdmission;
import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.request.LoginRequest;
import com.example.coinserver.common.response.LoginResponse;
import com.example.coinserver.common.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public GenericResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return new GenericResponse<>(0, "SUCCESS", authService.login(request));
    }

    @PostMapping("/registration")
    public GenericResponse<LoginResponse> register(@Valid @RequestBody LoginRequest request) {
        return new GenericResponse<>(0, "SUCCESS", authService.registration(request));
    }

    @PostMapping("/logout")
    @LoginAdmission
    public GenericResponse<?> logout() {
        authService.logout();
        return new GenericResponse<>(0, "SUCCESS");
    }

    @GetMapping("/whoami")
    @LoginAdmission
    public GenericResponse<UserResponse> whoami() {
        return new GenericResponse<>(0, "SUCCESS", new UserResponse(authService.getUser()));
    }
}
