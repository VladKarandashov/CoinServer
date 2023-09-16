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

    public static final String mainCurrency = "USDT";
    public static final Set<String> wrongSymbolEndSubstrings = Set.of("USD", "BUSD");

    private final BinanceApiRestClient binanceClient;

    public List<TickerPrice> getAllPrices() {
        return binanceClient.getAllPrices().stream()
                .filter(this::isTickerSymbolNeed)
                .filter(this::isTickerSymbolNotWrong)
                .map(this::remakeToCorrectFormat)
                .collect(Collectors.toList());
    }

    public Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return getAllPrices().stream()
                .filter(price -> symbol.equalsIgnoreCase(price.getSymbol()))
                .findFirst();
    }

    public TickerStatistics getTickerStatisticsBySymbol(String symbol) {
        symbol += mainCurrency;
        return binanceClient.get24HrPriceStatistics(symbol);
    }

    public List<CandlestickResponse> getCandlestickBarsBySymbol(CandlestickInterval interval, String symbol) {
        symbol += mainCurrency;
        return binanceClient.getCandlestickBars(symbol, interval).stream()
                .map(CandlestickResponse::new)
                .collect(Collectors.toList());
    }

    private boolean isTickerSymbolNeed(TickerPrice ticker) {
        var symbol = ticker.getSymbol();
        return symbol.endsWith(mainCurrency);
    }

    private boolean isTickerSymbolNotWrong(TickerPrice ticker) {
        var symbol = ticker.getSymbol();
        return wrongSymbolEndSubstrings.stream().noneMatch(symbol::endsWith);
    }

    private TickerPrice remakeToCorrectFormat(TickerPrice ticker) {
        var currencySymbol = ticker.getSymbol();
        var rightSymbol = currencySymbol.substring(0, currencySymbol.length()-mainCurrency.length());
        ticker.setSymbol(rightSymbol);
        return ticker;
    }
}
