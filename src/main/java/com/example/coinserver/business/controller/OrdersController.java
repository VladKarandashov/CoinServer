package com.example.coinserver.business.controller;

import com.example.coinserver.auth.annotation.LoginAdmission;
import com.example.coinserver.business.service.OrdersService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.request.CreateBuyOrderRequest;
import com.example.coinserver.common.request.CreateSellOrderRequest;
import com.example.coinserver.db.entity.OrderEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @GetMapping
    @LoginAdmission
    public List<OrderEntity> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @GetMapping("/{symbol}")
    @LoginAdmission
    public List<OrderEntity> getOrdersBySymbol(@PathVariable String symbol) {
        return ordersService.getOrdersBySymbol(symbol);
    }

    @PostMapping("/create")
    @LoginAdmission
    public GenericResponse<?> createBuyOrder(@Valid @RequestBody CreateBuyOrderRequest request) {
        ordersService.createBuyOrder(request);
        return new GenericResponse<>(0, "SUCCESS");
    }

    @PostMapping("/sail")
    @LoginAdmission
    public GenericResponse<?> createSellOrder(@Valid @RequestBody CreateSellOrderRequest request) {
        ordersService.createSellOrder(request);
        return new GenericResponse<>(0, "SUCCESS");
    }
}
