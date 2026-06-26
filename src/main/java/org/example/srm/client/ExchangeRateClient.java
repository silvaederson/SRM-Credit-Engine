package org.example.srm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateClient {

    private final RestTemplate restTemplate;

    @Value("${exchange.api.url:https://api.exchangerate-api.com/v4/latest}")
    private String apiUrl;

    @Value("${exchange.api.key:}")
    private String apiKey;

    @Value("${exchange.api.enabled:false}")
    private boolean apiEnabled;

    /**
     * Busca a taxa de câmbio atual de uma API externa
     */
    public BigDecimal fetchExchangeRate(String fromCurrency, String toCurrency) {
        if (!apiEnabled) {
            log.warn("External exchange API is disabled, using fallback");
            return getFallbackRate(fromCurrency, toCurrency);
        }

        try {
            String url = String.format("%s/%s", apiUrl, fromCurrency.toUpperCase());
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "?apiKey=" + apiKey;
            }

            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

            if (response != null && response.getRates() != null) {
                BigDecimal rate = response.getRates().get(toCurrency.toUpperCase());
                if (rate != null) {
                    log.info("Fetched exchange rate from {} to {}: {}", fromCurrency, toCurrency, rate);
                    return rate;
                }
            }

            log.warn("Rate not found in API response for {} to {}", fromCurrency, toCurrency);
            return getFallbackRate(fromCurrency, toCurrency);

        } catch (Exception e) {
            log.error("Error fetching exchange rate from API: {}", e.getMessage());
            return getFallbackRate(fromCurrency, toCurrency);
        }
    }

    private BigDecimal getFallbackRate(String fromCurrency, String toCurrency) {
        // Taxas de fallback para desenvolvimento
        if (fromCurrency.equalsIgnoreCase("USD") && toCurrency.equalsIgnoreCase("BRL")) {
            return new BigDecimal("5.2345");
        }
        if (fromCurrency.equalsIgnoreCase("BRL") && toCurrency.equalsIgnoreCase("USD")) {
            return new BigDecimal("0.1910");
        }
        if (fromCurrency.equalsIgnoreCase("EUR") && toCurrency.equalsIgnoreCase("BRL")) {
            return new BigDecimal("6.0123");
        }
        if (fromCurrency.equalsIgnoreCase("USD") && toCurrency.equalsIgnoreCase("EUR")) {
            return new BigDecimal("0.8710");
        }
        if (fromCurrency.equalsIgnoreCase("GBP") && toCurrency.equalsIgnoreCase("BRL")) {
            return new BigDecimal("7.2345");
        }
        // Fallback genérico
        return new BigDecimal("1.0000");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateResponse {
        private String base;
        private Map<String, BigDecimal> rates;

        @JsonProperty("base")
        public String getBase() { return base; }
        public void setBase(String base) { this.base = base; }

        @JsonProperty("rates")
        public Map<String, BigDecimal> getRates() { return rates; }
        public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
    }
}