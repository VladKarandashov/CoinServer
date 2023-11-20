package com.example.coinserver.business.controller;

import com.example.coinserver.auth.annotation.LoginAdmission;
import com.example.coinserver.business.service.ManageOrdersService;
import com.example.coinserver.business.service.OrdersService;
import com.example.coinserver.common.GenericResponse;
import com.example.coinserver.common.request.CreateBuyOrderRequest;
import com.example.coinserver.common.request.CreateSellAllOrderRequest;
import com.example.coinserver.common.request.CreateSellOrderRequest;
import com.example.coinserver.common.response.OrderResponse;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final ManageOrdersService manageOrdersService;

    @GetMapping
    @LoginAdmission
    public GenericResponse<List<OrderResponse>> getAllOrders() {
        return new GenericResponse<>(0, "SUCCESS", ordersService.getAllOrders()
                .stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{symbol}")
    @LoginAdmission
    public GenericResponse<List<OrderResponse>> getOrdersBySymbol(@PathVariable String symbol) {
        return new GenericResponse<>(0, "SUCCESS", ordersService.getOrdersBySymbol(symbol)
                .stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList()));
    }

    @PostMapping("/create")
    @LoginAdmission
    public GenericResponse<?> createBuyOrder(@Valid @RequestBody CreateBuyOrderRequest request) {
        manageOrdersService.createBuyOrder(request);
        return new GenericResponse<>(0, "SUCCESS");
    }

    @PostMapping("/sell")
    @LoginAdmission
    public GenericResponse<?> createSellOrder(@Valid @RequestBody CreateSellOrderRequest request) {
        manageOrdersService.createSellOrder(request);
        return new GenericResponse<>(0, "SUCCESS");
    }

    @PostMapping("/sell/all")
    @LoginAdmission
    public GenericResponse<?> createSellAllOrder(@Valid @RequestBody CreateSellAllOrderRequest request) {
        manageOrdersService.createSellOrder(request);
        return new GenericResponse<>(0, "SUCCESS");
    }
}
