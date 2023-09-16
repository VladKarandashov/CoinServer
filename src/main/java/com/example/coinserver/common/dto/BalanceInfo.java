package com.example.coinserver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class BalanceInfo {

    private BigDecimal usdMoney;

    private List<AssetsBalance> assets;

    private ChangeCost changeCost;
}
