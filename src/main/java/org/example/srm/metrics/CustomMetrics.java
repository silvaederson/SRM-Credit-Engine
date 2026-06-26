package org.example.srm.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeTransactions = new AtomicInteger(0);
    private final AtomicInteger pendingTransactions = new AtomicInteger(0);

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        meterRegistry.gauge("srm.transactions.active", activeTransactions);
        meterRegistry.gauge("srm.transactions.pending", pendingTransactions);
    }

    public void incrementActiveTransactions() {
        activeTransactions.incrementAndGet();
    }

    public void decrementActiveTransactions() {
        activeTransactions.decrementAndGet();
    }

    public void setPendingTransactions(int count) {
        pendingTransactions.set(count);
    }

    public void recordTransactionByCurrency(String currency) {
        meterRegistry.counter("srm.transactions.by.currency", Tags.of("currency", currency))
                .increment();
    }

    public void recordTransactionByStatus(String status) {
        meterRegistry.counter("srm.transactions.by.status", Tags.of("status", status))
                .increment();
    }
}