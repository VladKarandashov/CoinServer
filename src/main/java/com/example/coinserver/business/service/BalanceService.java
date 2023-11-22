package com.example.coinserver.business.service;

import com.example.coinserver.api.binance.BinanceService;
import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.dto.AssetsBalance;
import com.example.coinserver.common.dto.BalanceInfo;
import com.example.coinserver.common.dto.ChangeCost;
import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.entity.UserEntity;
import com.example.coinserver.exception.CoinServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.coinserver.exception.ExceptionBasis.NOT_FOUND;
import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AuthService authService;
    private final OrdersService ordersService;
    private final BinanceService binanceService;

    public BalanceInfo getBalance() {
        var user = authService.getUser();
        return getBalance(user);
    }

    public BalanceInfo getBalance(UserEntity user) {
        var spentUsd = getSpentUsdMoney(user);
        var assetsBalanceList = getAssetsBalance(user);
        var assetsUsdCost = getUsdAssetsCost(assetsBalanceList);

        return new BalanceInfo(user.getMoney(), assetsBalanceList, new ChangeCost(
                spentUsd,
                assetsUsdCost,
                getChangeCostPercent(spentUsd, assetsUsdCost)
        ));
    }

    public BigDecimal getUsdBalance() {
        return authService.getUser().getMoney();
    }

    public AssetsBalance getAssetsBalance(String symbol) {
        var user = authService.getUser();
        return getAssetsBalance(symbol, user);
    }

    public AssetsBalance getAssetsBalance(String symbol, UserEntity user) {
        var orders = ordersService.getOrdersBySymbol(symbol, user);
        return getAssetsBalance(symbol, orders);
    }

    public List<AssetsBalance> getAssetsBalance() {
        var user = authService.getUser();
        return getAssetsBalance(user);
    }

    public AssetsBalance getAssetsBalance(String symbol, List<OrderEntity> orders) {
        var assetsCount = orders.stream()
                .map(OrderEntity::getAssetsCount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var spentUsdMoney = orders.stream()
                .map(OrderEntity::getMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var tickerPrice = binanceService.getPriceBySymbol(symbol)
                .orElseThrow(() -> {
                    log.error("Не хватает {}", symbol);
                    return new CoinServerException(NOT_FOUND);
                });
        var price = new BigDecimal(tickerPrice.getPrice());
        var usdAssetsCost = assetsCount.multiply(price);
        return new AssetsBalance(symbol, assetsCount, new ChangeCost(
                spentUsdMoney,
                usdAssetsCost,
                getChangeCostPercent(spentUsdMoney, usdAssetsCost)
        ));
    }

    public List<AssetsBalance> getAssetsBalance(UserEntity user) {
        return getAssetsBalance(ordersService.getAllOrders(user));
    }

    public List<AssetsBalance> getAssetsBalance(List<OrderEntity> orders) {
        return orders.stream()
                .collect(groupingBy(OrderEntity::getAssetsSymbol))
                .entrySet()
                .stream()
                .map(ordersGroup -> getAssetsBalance(ordersGroup.getKey(), ordersGroup.getValue()))
                .collect(Collectors.toList());
    }

    public BigDecimal getSpentUsdMoney() {
        return getSpentUsdMoney(ordersService.getAllOrders());
    }

    public BigDecimal getSpentUsdMoney(UserEntity user) {
        return getSpentUsdMoney(ordersService.getAllOrders(user));
    }

    public BigDecimal getSpentUsdMoney(List<OrderEntity> orders) {
        return orders.stream()
                .map(OrderEntity::getMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getUsdAssetsCost(List<AssetsBalance> assetsBalanceList) {
        return assetsBalanceList.stream()
                .map(AssetsBalance::getChangeCost)
                .map(ChangeCost::getCostUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getChangeCostPercent(BigDecimal spentUsd, BigDecimal costUsd) {
        if (spentUsd.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return costUsd
                .divide(spentUsd, 32, RoundingMode.FLOOR)
                .subtract(BigDecimal.ONE)
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }
}
