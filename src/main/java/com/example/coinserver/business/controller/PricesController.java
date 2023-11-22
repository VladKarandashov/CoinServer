package com.example.coinserver.business.controller;

import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.coinserver.api.binance.BinanceApiClientService;
import com.example.coinserver.api.binance.BinanceUtils;
import com.example.coinserver.api.binance.CoinPricesHolder;
import com.example.coinserver.api.binance.dto.CandlestickResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coins")
public class PricesController {

    private final BinanceApiClientService binanceApiClientService;

    @GetMapping("/prices")
    public List<TickerPrice> getAllPrices() {
        return CoinPricesHolder.getAllPrices();
    }

    @GetMapping("/prices/{symbol}")
    public List<CandlestickResponse> getCandlestickBarsBySymbol(
            @PathVariable("symbol") String symbol,
            @RequestParam(required = false, defaultValue = "3d")
            String interval) {
        return binanceApiClientService.getCandlestickBarsBySymbol(
                BinanceUtils.getCandlestickIntervalByString(interval), symbol);
    }

    @GetMapping("/statistics/{symbol}")
    public TickerStatistics getAllPrices(@PathVariable("symbol") String symbol) {
        return binanceApiClientService.getTickerStatisticsBySymbol(symbol);
    }
}
