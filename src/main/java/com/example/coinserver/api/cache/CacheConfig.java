package com.example.coinserver.api.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

   public final static String PRICES_CACHE = "allPricesCache";
   public final static String TICKER_STATISTICS_BY_SYMBOL_CACHE = "tickerStatisticsBySymbolCache";
   public final static String CANDLESTICK_BARS_BY_SYMBOL_CACHE = "candlestickBarsBySymbolCache";

   @Bean
   public Cache allPricesCache() {
      return new CaffeineCache(PRICES_CACHE, Caffeine.newBuilder()
              .expireAfterWrite(5, TimeUnit.SECONDS)
              .build());
   }

   @Bean
   public Cache tickerStatisticsBySymbolCache() {
      return new CaffeineCache(TICKER_STATISTICS_BY_SYMBOL_CACHE, Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build());
   }

   @Bean
   public Cache candlestickBarsBySymbolCache() {
      return new CaffeineCache(CANDLESTICK_BARS_BY_SYMBOL_CACHE, Caffeine.newBuilder()
              .expireAfterWrite(10, TimeUnit.SECONDS)
              .build());
   }
}