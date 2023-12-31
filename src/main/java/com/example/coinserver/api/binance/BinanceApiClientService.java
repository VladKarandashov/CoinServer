package com.example.coinserver.api.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.coinserver.api.binance.dto.CandlestickResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.coinserver.api.binance.config.CacheConfig.CANDLESTICK_BARS_BY_SYMBOL_CACHE;
import static com.example.coinserver.api.binance.config.CacheConfig.PRICES_CACHE;
import static com.example.coinserver.api.binance.config.CacheConfig.TICKER_STATISTICS_BY_SYMBOL_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceApiClientService {

    public static final String mainCurrency = "USDT";
    public static final Set<String> wrongSymbolEndSubstrings = Set.of("USD", "BUSD");

    public static final Set<String> BLACK_LIST = Set.of("BCC", "BCHABC", "BCHSV", "NANO", "COCOS", "MCO", "BULL", "BEAR",
            "ETHBULL", "ETHBEAR", "EOSBULL", "EOSBEAR", "XRPBULL", "XRPBEAR", "BNBBULL", "BNBBEAR", "XZC", "GXS", "REP",
            "DAI", "XTZDOWN", "TRXDOWN", "DOTDOWN", "LTCDOWN", "HNT", "UNIUP", "UNIDOWN", "SUSHIDOWN", "XLMDOWN", "BTCST",
            "AUTO", "BTG", "TORN", "RGT", "ANY", "ARB");

    private final BinanceApiRestClient binanceClient;

    @Cacheable(PRICES_CACHE)
    public List<TickerPrice> getAllPrices() {
        return binanceClient.getAllPrices().stream()
                .filter(ticker -> Double.parseDouble(ticker.getPrice())>=1)
                .filter(this::isTickerSymbolNeed)
                .filter(this::isTickerSymbolNotWrong)
                .map(this::remakeToCorrectFormat)
                .filter(ticker -> !BLACK_LIST.contains(ticker.getSymbol()))
                .collect(Collectors.toList());
    }

    @Cacheable(TICKER_STATISTICS_BY_SYMBOL_CACHE)
    public TickerStatistics getTickerStatisticsBySymbol(String symbol) {
        symbol += mainCurrency;
        return binanceClient.get24HrPriceStatistics(symbol);
    }

    @Cacheable(CANDLESTICK_BARS_BY_SYMBOL_CACHE)
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
