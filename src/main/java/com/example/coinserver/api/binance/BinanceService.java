package com.example.coinserver.api.binance;

import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Deprecated: use
 * {@link  CoinPricesHolder  CoinPricesHolder}
 */
@Service
@Deprecated(forRemoval = true)
public class BinanceService {

    public Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return CoinPricesHolder.getPriceBySymbol(symbol);
    }
}
