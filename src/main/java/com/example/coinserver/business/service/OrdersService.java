package com.example.coinserver.business.service;

import com.example.coinserver.api.binance.BinanceService;
import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.request.CreateBuyOrderRequest;
import com.example.coinserver.common.request.CreateSellOrderRequest;
import com.example.coinserver.common.response.AssetsBalance;
import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.entity.UserEntity;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BinanceService binanceService;

    public List<OrderEntity> getAllOrders() {
        var user = authService.getUser();
        return orderRepository.findAllByUser(user).stream()
                .sorted(Comparator.comparing(OrderEntity::getDate))
                .collect(Collectors.toList());
    }

    public List<OrderEntity> getOrdersBySymbol(String symbol) {
        var user = authService.getUser();
        return getOrdersBySymbol(symbol, user);
    }

    public List<OrderEntity> getOrdersBySymbol(String symbol, UserEntity user) {
        return orderRepository.findAllByUserAndAssetsSymbol(user, symbol).stream()
                .sorted(Comparator.comparing(OrderEntity::getDate))
                .collect(Collectors.toList());
    }

    public AssetsBalance getAssetsBalanceBySymbol(String symbol) {
        var user = authService.getUser();
        return getAssetsBalanceBySymbol(symbol, user);
    }

    public AssetsBalance getAssetsBalanceBySymbol(String symbol, UserEntity user) {
        var assetsCount = getOrdersBySymbol(symbol, user).stream()
                .map(OrderEntity::getAssetsCount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AssetsBalance(symbol, assetsCount);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void createBuyOrder(CreateBuyOrderRequest request) {

        var usdMoneyForCreateOrder = request.getUsdMoney();
        if (usdMoneyForCreateOrder.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoinServerException(1201, "VALIDATION_ERROR");
        }

        var user = authService.getUser();

        var tickerPrice = binanceService.getPriceBySymbol(request.getAssetsSymbol())
                .orElseThrow(() -> new CoinServerException(1301, "NOT_FOUND"));
        var price = new BigDecimal(tickerPrice.getPrice());
        var count = usdMoneyForCreateOrder.divide(price, 32, RoundingMode.FLOOR);
        var newUsdMoneyBalance = user.getMoney().subtract(usdMoneyForCreateOrder);

        var order = OrderEntity.builder()
                .user(user)
                .assetsSymbol(request.getAssetsSymbol())
                .assetsCount(count)
                .assetsMoney(usdMoneyForCreateOrder)
                .date(LocalDateTime.now())
                .build();
        user.setMoney(newUsdMoneyBalance);
        orderRepository.save(order);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public void createSellOrder(CreateSellOrderRequest request) {

        var assetsCountForSail = request.getAssetsCount();
        if (assetsCountForSail.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoinServerException(1201, "VALIDATION_ERROR");
        }

        var user = authService.getUser();
        var balance = getAssetsBalanceBySymbol(request.getAssetsSymbol(), user);
        var assetsCountFromBalance = balance.getAssetsCount();
        if (assetsCountForSail.compareTo(assetsCountFromBalance) < 0) {
            throw new CoinServerException(1302, "LACK_OF_RESOURCES");
        }


        var tickerPrice = binanceService.getPriceBySymbol(request.getAssetsSymbol())
                .orElseThrow(() -> new CoinServerException(1301, "NOT_FOUND"));
        var price = new BigDecimal(tickerPrice.getPrice());


        // TODO
        var count = assetsCountForSail.divide(price, 32, RoundingMode.FLOOR);
        var newUsdMoneyBalance = user.getMoney().subtract(assetsCountForSail);

        var order = OrderEntity.builder()
                .user(user)
                .assetsSymbol(request.getAssetsSymbol())
                .assetsCount(count)
                .assetsMoney(assetsCountForSail)
                .date(LocalDateTime.now())
                .build();
        user.setMoney(newUsdMoneyBalance);
        orderRepository.save(order);
        userRepository.save(user);
    }
}
