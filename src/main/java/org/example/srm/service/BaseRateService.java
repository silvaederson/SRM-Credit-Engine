package org.example.srm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class BaseRateService {

    private final Map<String, BigDecimal> baseRates = new HashMap<>();
    private final Random random = new Random();

    public BaseRateService() {
        initializeBaseRates();
    }

    @Cacheable(value = "baseRates", key = "#currencyCode")
    public BigDecimal getCurrentBaseRate(String currencyCode) {
        log.info("Fetching base rate for currency: {}", currencyCode);

        BigDecimal rate = baseRates.get(currencyCode.toUpperCase());
        if (rate == null) {
            // Simulação de taxa base para moedas não cadastradas
            rate = new BigDecimal("0.01").add(BigDecimal.valueOf(random.nextDouble() * 0.005));
            baseRates.put(currencyCode.toUpperCase(), rate);
            log.warn("Base rate not found for {}, using generated rate: {}", currencyCode, rate);
        }

        return rate;
    }

    @Scheduled(cron = "0 0 12 * * *") // Atualiza diariamente ao meio-dia
    public void updateBaseRates() {
        log.info("Updating base rates");

        // Simula variação das taxas
        BigDecimal variation = new BigDecimal("0.0005");
        baseRates.replaceAll((currency, rate) -> {
            BigDecimal newRate = rate.add(
                    BigDecimal.valueOf(random.nextDouble() * 0.002 - 0.001)
                            .setScale(4, RoundingMode.HALF_EVEN)
            );
            // Garantir que não fique negativo
            return newRate.max(BigDecimal.ZERO);
        });

        log.info("Base rates updated: {}", baseRates);
    }

    public void initializeBaseRates() {
        baseRates.put("BRL", new BigDecimal("0.1050")); // CDI ~10.5% a.a.
        baseRates.put("USD", new BigDecimal("0.0450")); // SOFR ~4.5% a.a.
        baseRates.put("EUR", new BigDecimal("0.0325")); // EURIBOR ~3.25% a.a.
        baseRates.put("GBP", new BigDecimal("0.0425")); // GBP LIBOR ~4.25% a.a.
        baseRates.put("JPY", new BigDecimal("0.0010")); // JPY LIBOR ~0.1% a.a.
        log.info("Base rates initialized: {}", baseRates);
    }

    // Converter taxa anual para mensal
    public BigDecimal getMonthlyBaseRate(String currencyCode) {
        BigDecimal annualRate = getCurrentBaseRate(currencyCode);
        return annualRate.divide(new BigDecimal("12"), 4, RoundingMode.HALF_EVEN);
    }
}