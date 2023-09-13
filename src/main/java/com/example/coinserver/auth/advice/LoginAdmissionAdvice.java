package com.example.coinserver.auth.advice;

import com.example.coinserver.exception.CoinServerException;
import com.example.coinserver.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginAdmissionAdvice {

    @Before("@annotation(com.example.coinserver.auth.annotation.LoginAdmission)")
    public void checkLogin(JoinPoint joinPoint) {

        var token = getToken();


    }

    private static Optional<String> getToken() {
        var requestOpt = HttpUtils.getHttpRequest();
        return requestOpt.map(httpServletRequest -> httpServletRequest.getHeader("token"));
    }
}
