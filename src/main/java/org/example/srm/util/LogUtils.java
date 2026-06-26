package org.example.srm.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LogUtils {

    private static final String CORRELATION_ID = "correlationId";
    private static final String USER_ID = "userId";
    private static final String TRANSACTION_ID = "transactionId";

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public static void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId != null ? correlationId : generateCorrelationId());
    }

    public static void setUserId(String userId) {
        MDC.put(USER_ID, userId);
    }

    public static void setTransactionId(Long transactionId) {
        MDC.put(TRANSACTION_ID, String.valueOf(transactionId));
    }

    public static void clearContext() {
        MDC.clear();
    }

    public static Map<String, String> getContext() {
        Map<String, String> context = new HashMap<>();
        String correlationId = MDC.get(CORRELATION_ID);
        String userId = MDC.get(USER_ID);
        String transactionId = MDC.get(TRANSACTION_ID);

        if (correlationId != null) context.put(CORRELATION_ID, correlationId);
        if (userId != null) context.put(USER_ID, userId);
        if (transactionId != null) context.put(TRANSACTION_ID, transactionId);

        return context;
    }

    public static void logWithContext(String message, Object... args) {
        Map<String, String> context = getContext();
        log.info("[{}] {} - {}",
                context.getOrDefault(CORRELATION_ID, "N/A"),
                message,
                args.length > 0 ? args : "");
    }
}