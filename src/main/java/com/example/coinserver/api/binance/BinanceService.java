package com.example.coinserver.api.binance;

import com.binance.api.client.domain.market.TickerPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceApiService binanceApiService;

    public Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return binanceApiService.getAllPrices().stream()
                .filter(price -> symbol.equalsIgnoreCase(price.getSymbol()))
                .findFirst();
    }
}
