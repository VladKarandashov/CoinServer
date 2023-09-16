package com.example.coinserver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserScore {

    private String login;

    private ChangeCost changeCost;
}
