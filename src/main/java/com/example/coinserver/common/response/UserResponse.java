package com.example.coinserver.common.response;

import com.example.coinserver.db.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;

    private String login;

    private String token;

    private BigDecimal money;

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.token = user.getToken();
        this.money = user.getMoney();
    }
}