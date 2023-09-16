package com.example.coinserver.business.service;

import com.example.coinserver.api.binance.BinanceService;
import com.example.coinserver.auth.annotation.LoginAdmission;
import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.common.request.CreateOrderRequest;
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

    @LoginAdmission
    public List<OrderEntity> getAllOrders() {
        var user = authService.getUser();
        return orderRepository.findAllByUser(user).stream()
                .sorted(Comparator.comparing(OrderEntity::getDate))
                .collect(Collectors.toList());
    }

    @LoginAdmission
    public List<OrderEntity> getOrdersBySymbol(String symbol) {
        var user = authService.getUser();
        return orderRepository.findAllByUserAndAssetsSymbol(user, symbol).stream()
                .sorted(Comparator.comparing(OrderEntity::getDate))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    @LoginAdmission
    public void createOrder(CreateOrderRequest request) {

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
}
