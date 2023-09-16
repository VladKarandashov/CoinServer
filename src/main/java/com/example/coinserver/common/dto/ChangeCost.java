package com.example.coinserver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class ChangeCost {

    private BigDecimal spentUsd;

    private BigDecimal costUsd;

    private BigDecimal percent;

    public ChangeCost(BigDecimal spentUsd, BigDecimal costUsd) {
        this(
                spentUsd,
                costUsd,
                costUsd
                        .divide(spentUsd, 32, RoundingMode.FLOOR)
                        .subtract(BigDecimal.ONE)
                        .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN))
        );
    }
}
