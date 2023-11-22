package com.example.coinserver.api.binance;

import com.binance.api.client.domain.market.TickerPrice;
import com.example.coinserver.db.entity.LastPricesEntity;
import com.example.coinserver.db.repository.LastPricesRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinPricesHolder {

    private final BinanceApiClientService binanceApiClientService;

    private final LastPricesRepository lastPricesRepository;

    @Getter
    private static final ConcurrentHashMap<String, TickerPrice> prices = new ConcurrentHashMap<>();

    @PostConstruct
    private void initPrices() {
        log.info("Начинаю подгрузку старых цен из БД");
        lastPricesRepository.findAll().forEach(lastPrice -> {
            var tickerPrice = new TickerPrice();
            tickerPrice.setSymbol(lastPrice.getSymbol());
            tickerPrice.setPrice(lastPrice.getPrice());
            prices.put(lastPrice.getSymbol(), tickerPrice);
        });
        log.info("Старые цены подгружены. Обновляю их с биржы");
        updatePrices();
        log.info("Цены обновлены");
    }

    @Scheduled(fixedDelay = 2000)
    private void updatePrices() {
        binanceApiClientService.getAllPrices()
                .forEach(tickerPrice -> prices.put(tickerPrice.getSymbol(), tickerPrice));
    }

    @Scheduled(fixedDelay = 10*60*1000)
    private void backupPrices() {
        log.info("Выполняю backup цен монет");
        var currentPrices = new LinkedList<>(prices.values().stream()
                .map(LastPricesEntity::new)
                .toList());
        lastPricesRepository.saveAll(currentPrices);
        log.info("Backup цен завершён");
    }

    public static Optional<TickerPrice> getPriceBySymbol(String symbol) {
        return Optional.ofNullable(prices.get(symbol));
    }

    public static List<TickerPrice> getAllPrices() {
        return new ArrayList<>(prices.values());
    }
}
