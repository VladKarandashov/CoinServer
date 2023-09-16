package com.example.coinserver.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSellOrderRequest {

    @NotBlank
    private String assetsSymbol;

    @NotNull
    private BigDecimal assetsCount;
}
