package com.example.coinserver.api.binance;

import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinanceUtils {

    public static final CandlestickInterval DEFAULT_INTERVAL =  CandlestickInterval.THREE_DAILY;

    public CandlestickInterval getCandlestickIntervalByString(String intervalString) {
        for (CandlestickInterval interval : CandlestickInterval.values()) {
            if (interval.getIntervalId().equals(intervalString)) {
                return interval;
            }
        }
        return DEFAULT_INTERVAL;
    }
}
