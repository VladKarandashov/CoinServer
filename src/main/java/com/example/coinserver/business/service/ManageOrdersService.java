package com.example.coinserver.business.service;

import com.example.coinserver.api.binance.BinanceService;
import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.request.CreateBuyOrderRequest;
import com.example.coinserver.common.request.CreateSellAllOrderRequest;
import com.example.coinserver.common.request.CreateSellOrderRequest;
import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.repository.OrderRepository;
import com.example.coinserver.db.repository.UserRepository;
import com.example.coinserver.exception.CoinServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static com.example.coinserver.exception.ExceptionBasis.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageOrdersService {

    private final BinanceService binanceService;
    private final AuthService authService;
    private final BalanceService balanceService;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void createBuyOrder(CreateBuyOrderRequest request) {
        var symbol = request.getAssetsSymbol();
        var usdMoneyForCreateOrder = request.getUsdMoney();
        var user = authService.getUser();

        // дополнительная валидация запроса
        if (usdMoneyForCreateOrder.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoinServerException(VALIDATION_ERROR_USD);
        }
        if (usdMoneyForCreateOrder.compareTo(user.getMoney()) > 0) {
            throw new CoinServerException(VALIDATION_ERROR_USD);
        }

        // по какой цене покупаем?
        var tickerPrice = binanceService.getPriceBySymbol(symbol).orElseThrow(() -> new CoinServerException(NOT_FOUND));
        var assetsPriceForBuy = new BigDecimal(tickerPrice.getPrice());

        // сколько пользователь получит ресурсов после покупки?
        var orderAssetsCount = usdMoneyForCreateOrder.divide(assetsPriceForBuy, 32, RoundingMode.FLOOR);
        // какой у пользователя новый баланс?
        var newUsdMoneyBalance = user.getMoney().subtract(usdMoneyForCreateOrder);

        var order = OrderEntity.builder().user(user).assetsSymbol(symbol).assetsCount(orderAssetsCount).money(usdMoneyForCreateOrder.negate()).date(LocalDateTime.now()).build();
        user.setMoney(newUsdMoneyBalance);
        orderRepository.save(order);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void createSellOrder(CreateSellOrderRequest request) {
        var symbol = request.getAssetsSymbol();
        var assetsCountForSail = request.getAssetsCount();

        // дополнительная валидация запроса
        if (assetsCountForSail.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoinServerException(VALIDATION_ERROR_ASSETS);
        }

        // а у пользователя есть столько ресурсов, чтобы их продать?
        var user = authService.getUser();
        var balance = balanceService.getAssetsBalance(symbol, user);
        var userAssetsCount = balance.getAssetsCount();
        if (assetsCountForSail.compareTo(userAssetsCount) > 0) {
            throw new CoinServerException(LACK_OF_RESOURCES);
        }

        // по какой цене продаём?
        var tickerPrice = binanceService.getPriceBySymbol(symbol).orElseThrow(() -> new CoinServerException(NOT_FOUND));
        var assetsPriceForSail = new BigDecimal(tickerPrice.getPrice());

        // сколько пользователь получит денег после продажи?
        var orderUsdMoneyPrice = assetsPriceForSail.multiply(assetsCountForSail);
        // какой у пользователя новый баланс?
        var newUsdMoneyBalance = user.getMoney().add(orderUsdMoneyPrice);

        var order = OrderEntity.builder().user(user).assetsSymbol(symbol).assetsCount(assetsCountForSail.negate()).money(orderUsdMoneyPrice).date(LocalDateTime.now()).build();
        user.setMoney(newUsdMoneyBalance);
        orderRepository.save(order);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void createSellOrder(CreateSellAllOrderRequest request) {
        var symbol = request.getAssetsSymbol();
        var balance = balanceService.getAssetsBalance(symbol);
        var userAssetsCount = balance.getAssetsCount();

        createSellOrder(new CreateSellOrderRequest(symbol, userAssetsCount));
    }
}
