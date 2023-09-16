package com.example.coinserver.business.service;

import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.response.AssetsBalance;
import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AuthService authService;
    private final OrdersService ordersService;

    public AssetsBalance getAssetsBalanceBySymbol(String symbol) {
        var user = authService.getUser();
        return getAssetsBalanceBySymbol(symbol, user);
    }

    public AssetsBalance getAssetsBalanceBySymbol(String symbol, UserEntity user) {
        var assetsCount = ordersService.getOrdersBySymbol(symbol, user).stream()
                .map(OrderEntity::getAssetsCount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AssetsBalance(symbol, assetsCount);
    }
}
