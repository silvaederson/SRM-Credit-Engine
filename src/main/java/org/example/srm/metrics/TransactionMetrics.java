package org.example.srm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class TransactionMetrics {

    private final Counter settlementCounter;
    private final Counter failedSettlementCounter;
    private final Timer settlementTimer;

    public TransactionMetrics(MeterRegistry registry) {
        this.settlementCounter = Counter.builder("srm.transactions.settled")
                .description("Number of successfully settled transactions")
                .register(registry);

        this.failedSettlementCounter = Counter.builder("srm.transactions.failed")
                .description("Number of failed transaction settlements")
                .register(registry);

        this.settlementTimer = Timer.builder("srm.transactions.settlement.duration")
                .description("Time taken to settle a transaction")
                .register(registry);
    }

    public void recordSettlement() {
        settlementCounter.increment();
    }

    public void recordFailedSettlement() {
        failedSettlementCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public Timer getSettlementTimer() {
        return settlementTimer;
    }

    public Counter getSettlementCounter() {
        return settlementCounter;
    }

    public Counter getFailedSettlementCounter() {
        return failedSettlementCounter;
    }
}