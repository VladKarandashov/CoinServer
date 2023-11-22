package com.example.coinserver.api.binance.dto;

import com.binance.api.client.domain.market.Candlestick;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandlestickResponse {
    private Long openTime;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private Long closeTime;
    private String quoteAssetVolume;
    private Long numberOfTrades;
    private String takerBuyBaseAssetVolume;
    private String takerBuyQuoteAssetVolume;

    public CandlestickResponse(Candlestick candlestick) {
        this.openTime = candlestick.getOpenTime();
        this.open = candlestick.getOpen();
        this.high = candlestick.getHigh();
        this.low = candlestick.getLow();
        this.close = candlestick.getClose();
        this.volume = candlestick.getVolume();
        this.closeTime = candlestick.getCloseTime();
        this.quoteAssetVolume = candlestick.getQuoteAssetVolume();
        this.numberOfTrades = candlestick.getNumberOfTrades();
        this.takerBuyBaseAssetVolume = candlestick.getTakerBuyBaseAssetVolume();
        this.takerBuyQuoteAssetVolume = candlestick.getTakerBuyQuoteAssetVolume();
    }
}