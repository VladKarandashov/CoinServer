package com.example.coinserver.api.binance.config;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BinanceClientConfiguration {

    private final BinanceProperties binanceProperties;

    @Bean
    public BinanceApiRestClient binanceClient() {
        BinanceApiClientFactory factory =
                BinanceApiClientFactory.newInstance(binanceProperties.getKey(), binanceProperties.getSecret());
       return factory.newRestClient();
    }
}
