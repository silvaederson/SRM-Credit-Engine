package org.example.srm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class SettlementController {

    // Cache em memória para transações
    private final Map<Long, Map<String, Object>> transactions = new HashMap<>();
    private Long nextId = 1L;

    @PostMapping
    public ResponseEntity<Map<String, Object>> settle(@RequestBody Map<String, Object> request) {
        log.info("Processing settlement request: {}", request);

        Long creditorId = request.get("creditorId") != null ?
                Long.valueOf(request.get("creditorId").toString()) : 1L;

        BigDecimal faceValue = request.get("faceValue") != null ?
                new BigDecimal(request.get("faceValue").toString()) : BigDecimal.valueOf(1000);

        String currencyCode = request.get("currencyCode") != null ?
                request.get("currencyCode").toString() : "BRL";

        String dueDateStr = request.get("dueDate") != null ?
                request.get("dueDate").toString() : LocalDate.now().plusMonths(3).toString();

        LocalDate dueDate = LocalDate.parse(dueDateStr);
        LocalDate settlementDate = LocalDate.now();

        // Calcular meses
        Period period = Period.between(settlementDate, dueDate);
        int months = period.getMonths() + (period.getYears() * 12);
        if (months < 1) months = 1;

        // Taxas
        BigDecimal baseRate = new BigDecimal("0.00875"); // Taxa mensal
        BigDecimal spread = new BigDecimal("0.015"); // 1.5% ao mês
        BigDecimal totalRate = baseRate.add(spread);

        // Calcular valor presente: VP = VF / (1 + taxa)^meses
        BigDecimal factor = BigDecimal.ONE.add(totalRate).pow(months);
        BigDecimal presentValue = faceValue.divide(factor, 2, RoundingMode.HALF_EVEN);

        // Conversão cambial (se houver)
        BigDecimal exchangeRate = null;
        String paymentCurrency = null;

        if (request.get("paymentCurrency") != null) {
            paymentCurrency = request.get("paymentCurrency").toString();
            if (!paymentCurrency.equals(currencyCode)) {
                // Taxa de câmbio mockada
                exchangeRate = new BigDecimal("5.2345");
                presentValue = presentValue.multiply(exchangeRate)
                        .setScale(2, RoundingMode.HALF_EVEN);
            }
        }

        // Criar transação
        Long id = nextId++;
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("id", id);
        transaction.put("creditorId", creditorId);
        transaction.put("creditorName", getCreditorName(creditorId));
        transaction.put("faceValue", faceValue);
        transaction.put("presentValue", presentValue);
        transaction.put("currencyCode", currencyCode);
        transaction.put("paymentCurrency", paymentCurrency);
        transaction.put("exchangeRateUsed", exchangeRate);
        transaction.put("dueDate", dueDate.toString());
        transaction.put("settlementDate", settlementDate.toString());
        transaction.put("baseRate", baseRate);
        transaction.put("appliedSpread", spread);
        transaction.put("status", "SETTLED");
        transaction.put("createdAt", LocalDateTime.now().toString());
        transaction.put("externalReference", request.get("externalReference"));
        transaction.put("notes", request.get("notes"));

        transactions.put(id, transaction);

        return ResponseEntity.status(201).body(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTransaction(@PathVariable Long id) {
        log.info("Fetching transaction: {}", id);

        Map<String, Object> transaction = transactions.get(id);

        if (transaction == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Transaction not found");
            error.put("id", id);
            return ResponseEntity.status(404).body(error);
        }

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Map<String, Object>> getTransactionByReference(@PathVariable String reference) {
        log.info("Fetching transaction by reference: {}", reference);

        // Buscar transação por referência (mock)
        for (Map<String, Object> tx : transactions.values()) {
            if (reference.equals(tx.get("externalReference"))) {
                return ResponseEntity.ok(tx);
            }
        }

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Transaction not found with reference: " + reference);
        return ResponseEntity.status(404).body(error);
    }

    private String getCreditorName(Long creditorId) {
        // Dados mockados
        if (creditorId == 1L) return "ABC Corporation";
        if (creditorId == 2L) return "XYZ Industries";
        if (creditorId == 3L) return "Brazilian Government";
        return "Unknown Creditor";
    }
}