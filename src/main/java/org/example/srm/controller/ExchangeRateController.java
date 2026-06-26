package org.example.srm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@Slf4j
public class ExchangeRateController {

    // Cache em memória para testes
    private final Map<String, BigDecimal> rates = new HashMap<>();

    public ExchangeRateController() {
        // Taxas iniciais
        rates.put("USD-BRL", new BigDecimal("5.2345"));
        rates.put("EUR-BRL", new BigDecimal("6.0123"));
        rates.put("GBP-BRL", new BigDecimal("7.2345"));
        rates.put("USD-EUR", new BigDecimal("0.8710"));
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateRate(@RequestBody Map<String, Object> request) {
        log.info("Updating exchange rate: {}", request);

        String fromCurrency = (String) request.get("fromCurrency");
        String toCurrency = (String) request.get("toCurrency");
        BigDecimal rate = new BigDecimal(request.get("rate").toString());

        String key = fromCurrency + "-" + toCurrency;
        rates.put(key, rate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Exchange rate updated successfully!");
        response.put("fromCurrency", fromCurrency);
        response.put("toCurrency", toCurrency);
        response.put("rate", rate);
        response.put("effectiveDate", request.get("effectiveDate"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        log.info("Fetching exchange rate: {} -> {}", fromCurrency, toCurrency);

        String key = fromCurrency + "-" + toCurrency;
        BigDecimal rate = rates.get(key);

        Map<String, Object> response = new HashMap<>();
        response.put("fromCurrency", fromCurrency);
        response.put("toCurrency", toCurrency);

        if (rate != null) {
            response.put("rate", rate);
            response.put("status", "FOUND");
        } else {
            response.put("rate", new BigDecimal("1.0000"));
            response.put("status", "NOT_FOUND - Using default");
            response.put("message", "Using default rate 1.0");
        }

        return ResponseEntity.ok(response);
    }
}