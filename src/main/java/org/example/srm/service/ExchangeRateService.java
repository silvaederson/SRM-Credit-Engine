package org.example.srm.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.srm.exception.BusinessException;
import org.example.srm.exception.EntityNotFoundException;
import org.example.srm.model.dto.request.ExchangeRateUpdateRequest;
import org.example.srm.model.entity.Currency;
import org.example.srm.model.entity.ExchangeRate;
import org.example.srm.repository.CurrencyRepository;
import org.example.srm.repository.ExchangeRateRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '_' + #toCurrency")
    @CircuitBreaker(name = "exchangeRateService")
    @Retry(name = "exchangeRateService")
    public BigDecimal getCurrentRate(String fromCurrency, String toCurrency) {
        log.info("Fetching exchange rate from {} to {}", fromCurrency, toCurrency);

        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        ExchangeRate rate = exchangeRateRepository.findLatestRate(fromCurrency, toCurrency, LocalDate.now())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Exchange rate not found for %s to %s", fromCurrency, toCurrency)));

        return rate.getRate();
    }

    @Transactional
    @CacheEvict(value = "exchangeRates", allEntries = true)
    public void updateRate(ExchangeRateUpdateRequest request) {
        log.info("Updating exchange rate: {} -> {} = {}",
                request.getFromCurrency(), request.getToCurrency(), request.getRate());

        Currency fromCurrency = currencyRepository.findById(request.getFromCurrency())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found: " + request.getFromCurrency()));

        Currency toCurrency = currencyRepository.findById(request.getToCurrency())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found: " + request.getToCurrency()));

        // Verificar se já existe taxa para esta data
        Optional<ExchangeRate> existingRate = exchangeRateRepository.findByDate(
                request.getFromCurrency(), request.getToCurrency(), request.getEffectiveDate());

        ExchangeRate rate;
        if (existingRate.isPresent()) {
            rate = existingRate.get();
            rate.setRate(request.getRate());
        } else {
            rate = new ExchangeRate();
            rate.setFromCurrency(fromCurrency);
            rate.setToCurrency(toCurrency);
            rate.setRate(request.getRate());
            rate.setEffectiveDate(request.getEffectiveDate());
        }

        exchangeRateRepository.save(rate);
        log.info("Exchange rate updated successfully");
    }

    @Scheduled(cron = "0 0 10 * * *") // Todos os dias às 10:00
    @Transactional
    public void updateRatesFromExternalApi() {
        log.info("Scheduled exchange rate update started");

        try {
            // Simula chamada para API externa
            // Em produção, implementar chamada real para serviços como:
            // - BCB (Banco Central do Brasil)
            // - OpenExchangeRates
            // - ou outra API de câmbio

            // Mock para demonstração
            updateRate(new ExchangeRateUpdateRequest("USD", "BRL", new BigDecimal("5.2345"), LocalDate.now()));
            updateRate(new ExchangeRateUpdateRequest("EUR", "BRL", new BigDecimal("6.0123"), LocalDate.now()));
            updateRate(new ExchangeRateUpdateRequest("USD", "EUR", new BigDecimal("0.8710"), LocalDate.now()));

            log.info("Scheduled exchange rate update completed");

        } catch (Exception e) {
            log.error("Error updating exchange rates: {}", e.getMessage(), e);
        }
    }
}