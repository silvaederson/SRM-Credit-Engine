package org.example.srm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@Slf4j
public class ReportController {

    @GetMapping("/settlements")
    public ResponseEntity<Map<String, Object>> getSettlementReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long creditorId,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Generating settlement report - startDate: {}, endDate: {}, creditorId: {}, currencyCode: {}, status: {}",
                startDate, endDate, creditorId, currencyCode, status);

        // Dados mockados para teste
        List<Map<String, Object>> transactions = new ArrayList<>();

        Map<String, Object> tx1 = new HashMap<>();
        tx1.put("transactionId", 1);
        tx1.put("creditorName", "ABC Corporation");
        tx1.put("receivableType", "Commercial Invoice");
        tx1.put("faceValue", new BigDecimal("10000.00"));
        tx1.put("presentValue", new BigDecimal("9274.15"));
        tx1.put("currencyCode", "BRL");
        tx1.put("settlementDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        tx1.put("status", "SETTLED");
        tx1.put("discountRate", "7.26%");
        transactions.add(tx1);

        Map<String, Object> tx2 = new HashMap<>();
        tx2.put("transactionId", 2);
        tx2.put("creditorName", "XYZ Industries");
        tx2.put("receivableType", "Post-Dated Check");
        tx2.put("faceValue", new BigDecimal("5000.00"));
        tx2.put("presentValue", new BigDecimal("4523.12"));
        tx2.put("currencyCode", "USD");
        tx2.put("settlementDate", LocalDate.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE));
        tx2.put("status", "SETTLED");
        tx2.put("discountRate", "9.54%");
        transactions.add(tx2);

        Map<String, Object> response = new HashMap<>();
        response.put("content", transactions);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", transactions.size());
        response.put("totalPages", 1);
        response.put("filters", Map.of(
                "startDate", startDate != null ? startDate.toString() : "null",
                "endDate", endDate != null ? endDate.toString() : "null",
                "creditorId", creditorId != null ? creditorId : "null",
                "currencyCode", currencyCode != null ? currencyCode : "null",
                "status", status != null ? status : "null"
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/settlements/count")
    public ResponseEntity<Map<String, Object>> countTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long creditorId,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) String status) {

        log.info("Counting transactions - startDate: {}, endDate: {}, creditorId: {}, currencyCode: {}, status: {}",
                startDate, endDate, creditorId, currencyCode, status);

        Map<String, Object> response = new HashMap<>();
        response.put("count", 15);
        response.put("message", "Mock count for testing");
        response.put("filters", Map.of(
                "startDate", startDate != null ? startDate.toString() : "null",
                "endDate", endDate != null ? endDate.toString() : "null",
                "creditorId", creditorId != null ? creditorId : "null",
                "currencyCode", currencyCode != null ? currencyCode : "null",
                "status", status != null ? status : "null"
        ));

        return ResponseEntity.ok(response);
    }
}