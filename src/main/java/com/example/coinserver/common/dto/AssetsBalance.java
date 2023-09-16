package com.example.coinserver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AssetsBalance {

    private String assetsSymbol;

    private BigDecimal assetsCount;

    private ChangeCost changeCost;
}
