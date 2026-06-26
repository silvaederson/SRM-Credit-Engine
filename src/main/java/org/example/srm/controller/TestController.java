package org.example.srm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "SRM Credit Engine is running!");
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody Map<String, Object> request) {
        log.info("Received transaction: {}", request);

        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("status", "SETTLED");
        response.put("received", request);
        response.put("message", "Transaction created successfully!");

        return ResponseEntity.ok(response);
    }
}