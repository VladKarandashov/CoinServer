package com.example.coinserver.business.service;

import com.example.coinserver.auth.service.AuthService;
import com.example.coinserver.db.entity.OrderEntity;
import com.example.coinserver.db.entity.UserEntity;
import com.example.coinserver.db.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrderRepository orderRepository;
    private final AuthService authService;

    public List<OrderEntity> getAllOrders() {
        var user = authService.getUser();
        return getAllOrders(user);
    }

    public List<OrderEntity> getAllOrders(UserEntity user) {
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
}
