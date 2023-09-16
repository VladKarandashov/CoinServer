package com.example.coinserver.auth.service;

import com.example.coinserver.auth.config.AuthProperties;
import com.example.coinserver.common.request.LoginRequest;
import com.example.coinserver.common.response.LoginResponse;
import com.example.coinserver.db.entity.UserEntity;
import com.example.coinserver.db.repository.UserRepository;
import com.example.coinserver.exception.CoinServerException;
import com.example.coinserver.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.coinserver.exception.ExceptionBasis.LOGIN_ALREADY_EXIST;
import static com.example.coinserver.exception.ExceptionBasis.TOKEN_EXPIRED_NEED_LOGIN;
import static com.example.coinserver.exception.ExceptionBasis.TOKEN_HEADER_NOT_FOUND;
import static com.example.coinserver.exception.ExceptionBasis.WRONG_LOGIN_OR_PASSWORD;
import static com.example.coinserver.exception.ExceptionBasis.WRONG_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthProperties authProperties;

    public String getToken() {
        return HttpUtils.getToken()
                .orElseThrow(() -> new CoinServerException(TOKEN_HEADER_NOT_FOUND));
    }

    public UserEntity getUser() {
        return getUser(getToken());
    }

    public void checkAndUpdateUserLogin() {
        var user = getUser();
        checkExpiredToken(user);
        updateLoginDateTime(user);
    }

    public LoginResponse login(LoginRequest request) {
        var user = getUser(request.getLogin(), request.getPassword());
        var token = UUID.randomUUID().toString();
        user.setLoginDateTime(LocalDateTime.now());
        user.setToken(token);
        userRepository.save(user);
        return new LoginResponse(token);
    }

    public LoginResponse registration(LoginRequest request) {
        var existUser = userRepository.findUserEntityByLogin(request.getLogin());
        if (existUser.isEmpty()) {
            throw new CoinServerException(LOGIN_ALREADY_EXIST);
        }

        var token = UUID.randomUUID().toString();
        var user = UserEntity.builder()
            .login(request.getLogin())
            .password(request.getPassword())
            .loginDateTime(LocalDateTime.now())
            .token(token)
            .build();
        userRepository.save(user);
        return new LoginResponse(token);

    }

    private void checkExpiredToken(UserEntity user) {
        var liveTime = Duration.between(user.getLoginDateTime(), LocalDateTime.now());
        var expiredTime = authProperties.getExpiredTimeInSec();
        if (liveTime.getSeconds() > expiredTime) {
            throw new CoinServerException(TOKEN_EXPIRED_NEED_LOGIN);
        }
    }

    private UserEntity getUser(String token) {
        var user = userRepository.findUserEntityByToken(token)
                .orElseThrow(() -> new CoinServerException(WRONG_TOKEN));
        log.info("found user: {} {}", user.getId(), user.getLogin());
        return user;
    }

    private UserEntity getUser(String login, String password) {
        var user = userRepository.findUserEntityByLoginAndPassword(login, password)
                .orElseThrow(() -> new CoinServerException(WRONG_LOGIN_OR_PASSWORD));
        log.info("found user: {} {}", user.getId(), user.getLogin());
        return user;
    }

    private void updateLoginDateTime(UserEntity user) {
        user.setLoginDateTime(LocalDateTime.now());
        userRepository.save(user);
    }
}
