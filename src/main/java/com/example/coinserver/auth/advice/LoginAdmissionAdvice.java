package com.example.coinserver.auth.advice;

import com.example.coinserver.auth.config.AuthProperties;
import com.example.coinserver.db.entity.UserEntity;
import com.example.coinserver.db.repository.UserRepository;
import com.example.coinserver.exception.CoinServerException;
import com.example.coinserver.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginAdmissionAdvice {

    private final UserRepository userRepository;

    private final AuthProperties authProperties;

    @Before("@annotation(com.example.coinserver.auth.annotation.LoginAdmission)")
    public void checkLogin(JoinPoint joinPoint) {

        log.info("check login start");

        var token = HttpUtils.getToken()
                .orElseThrow(() -> new CoinServerException(1101, "TOKEN_HEADER_NOT_FOUND"));

        var user = userRepository.findUserEntityByToken(token)
                .orElseThrow(() -> new CoinServerException(1102, "WRONG_TOKEN"));
        log.info("found user: {} {}", user.getId(), user.getLogin());

        checkExpiredToken(user);

        user.setLoginDateTime(LocalDateTime.now());
        userRepository.save(user);
        log.info("check login finish");
    }

    private void checkExpiredToken(UserEntity user) {
        var liveTime = Duration.between(user.getLoginDateTime(), LocalDateTime.now());
        var expiredTime = authProperties.getExpiredTimeInSec();
        if (liveTime.getSeconds() > expiredTime) {
            throw new CoinServerException(1103, "TOKEN_EXPIRED_NEED_LOGIN");
        }
    }
}
