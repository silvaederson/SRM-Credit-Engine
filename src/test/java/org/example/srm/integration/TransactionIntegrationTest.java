package org.example.srm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.srm.SrmCreditEngineApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SrmCreditEngineApplication.class)
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveTransaction() throws Exception {
        // 1. Criar transação
        Map<String, Object> request = new HashMap<>();
        request.put("creditorId", 1);
        request.put("faceValue", 10000.00);
        request.put("currencyCode", "BRL");
        request.put("dueDate", "2026-09-30");
        request.put("paymentCurrency", "USD");
        request.put("externalReference", "INT-TEST-001");

        String response = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("SETTLED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. Extrair ID da resposta
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long transactionId = Long.valueOf(responseMap.get("id").toString());

        // 3. Buscar transação por ID
        mockMvc.perform(get("/api/v1/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.creditorName").value("ABC Corporation"));
    }

    @Test
    void shouldReturn404ForNonExistentTransaction() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Transaction not found"));
    }


}