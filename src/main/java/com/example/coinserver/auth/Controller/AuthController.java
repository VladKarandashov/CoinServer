package com.example.coinserver.auth.Controller;

import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.request.LoginRequest;
import com.example.coinserver.common.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
