package com.example.coinserver.api.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceService {

    public static final Set<String> needSymbolSubstrings = Set.of("USD");

    private final BinanceApiRestClient binanceClient;

    public List<TickerPrice> getAllPrices() {
        return binanceClient.getAllPrices().stream()
                .filter(this::isPriceNeed)
                .collect(Collectors.toList());
    }

    public Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return binanceClient.getAllPrices().stream()
                .filter(price -> symbol.equalsIgnoreCase(price.getSymbol()))
                .findFirst();
    }

    public TickerStatistics getTickerStatisticsBySymbol(String symbol) {
        return binanceClient.get24HrPriceStatistics(symbol);
    }

    public List<CandlestickResponse> getCandlestickBarsBySymbol(CandlestickInterval interval, String symbol) {
        return binanceClient.getCandlestickBars(symbol, interval).stream()
                .map(CandlestickResponse::new)
                .collect(Collectors.toList());
    }

    private boolean isPriceNeed(TickerPrice price) {
        var symbol = price.getSymbol();
        return needSymbolSubstrings.stream().anyMatch(symbol::contains);
    }
}
