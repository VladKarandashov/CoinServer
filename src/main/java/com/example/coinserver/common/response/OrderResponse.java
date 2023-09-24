package com.example.coinserver.common.response;

import com.example.coinserver.db.entity.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Integer id;

    private String assetsSymbol;

    private BigDecimal assetsCount;

    private BigDecimal money;

    private LocalDateTime date;

    public OrderResponse(OrderEntity order) {
        this.id = order.getId();
        this.assetsSymbol = order.getAssetsSymbol();
        this.assetsCount = order.getAssetsCount();
        this.money = order.getMoney();
        this.date = order.getDate();
    }
}