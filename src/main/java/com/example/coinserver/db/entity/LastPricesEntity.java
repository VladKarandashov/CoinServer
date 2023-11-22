package com.example.coinserver.db.entity;

import com.binance.api.client.domain.market.TickerPrice;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "last_prices")
public class LastPricesEntity {

    @Id
    @Column(name = "symbol")
    private String symbol;

    @Column(name = "price")
    private String price;

    public LastPricesEntity(TickerPrice tickerPrice) {
        this.symbol = tickerPrice.getSymbol();
        this.price = tickerPrice.getPrice();
    }
}
