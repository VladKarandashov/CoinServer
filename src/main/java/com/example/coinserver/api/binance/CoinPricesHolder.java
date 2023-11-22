package com.example.coinserver.api.binance;

import com.binance.api.client.domain.market.TickerPrice;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CoinPricesHolder {

    private final BinanceApiClientService binanceApiClientService;

    @Getter
    private static final ConcurrentHashMap<String, TickerPrice> prices = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 2000)
    private void updatePrices() {
        binanceApiClientService.getAllPrices().forEach(tickerPrice -> prices.put(tickerPrice.getSymbol(), tickerPrice));
    }

    public static Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return Optional.ofNullable(prices.get(symbol));
    }

    public static List<TickerPrice> getAllPrices() {
        return new ArrayList<>(prices.values());
    }
}
