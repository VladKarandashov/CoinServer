package com.example.coinserver.business.controller;

import com.example.coinserver.auth.annotation.LoginAdmission;
import com.example.coinserver.business.service.BalanceService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.dto.AssetsBalance;
import com.example.coinserver.common.dto.BalanceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balance")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    @LoginAdmission
    public GenericResponse<BigDecimal> getUsdBalance() {
        return new GenericResponse<>(0, "SUCCESS", balanceService.getUsdBalance());
    }

    @GetMapping("/assets")
    @LoginAdmission
    public GenericResponse<List<AssetsBalance>> getAssetsBalance() {
        return new GenericResponse<>(0, "SUCCESS", balanceService.getAssetsBalance());
    }

    @GetMapping("/assets/{symbol}")
    @LoginAdmission
    public GenericResponse<AssetsBalance> getAssetsBalance(@PathVariable String symbol) {
        return new GenericResponse<>(0, "SUCCESS", balanceService.getAssetsBalance(symbol));
    }

    @GetMapping("/info")
    @LoginAdmission
    public GenericResponse<BalanceInfo> getBalance() {
        return new GenericResponse<>(0, "SUCCESS", balanceService.getBalance());
    }
}
