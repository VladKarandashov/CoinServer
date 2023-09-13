package com.example.coinserver.auth.advice;

import com.example.coinserver.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginAdmissionAdvice {

    private final AuthService authService;

    @Before("@annotation(com.example.coinserver.auth.annotation.LoginAdmission)")
    public void checkLogin(JoinPoint joinPoint) {

        log.info("check login start");

        authService.checkAndUpdateUserLogin();

        log.info("check login finish");
    }
}
