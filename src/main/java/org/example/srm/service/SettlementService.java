package org.example.srm.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.srm.exception.EntityNotFoundException;
import org.example.srm.exception.InvalidOperationException;
import org.example.srm.metrics.TransactionMetrics;
import org.example.srm.model.dto.request.SettlementRequest;
import org.example.srm.model.dto.response.TransactionResponse;
import org.example.srm.model.entity.*;
import org.example.srm.model.enums.TransactionStatus;
import org.example.srm.repository.*;
import org.example.srm.strategy.PricingStrategy;
import org.example.srm.strategy.PricingStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final TransactionRepository transactionRepository;
    private final CreditorRepository creditorRepository;
    private final ReceivableTypeRepository receivableTypeRepository;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateService exchangeRateService;
    private final BaseRateService baseRateService;
    private final PricingStrategyFactory strategyFactory;
    private final TransactionMetrics metrics;

    @Transactional
    @CircuitBreaker(name = "settlementService")
    @Retry(name = "settlementService")
    public TransactionResponse settle(SettlementRequest request) {
        log.info("Processing settlement request: creditorId={}, amount={}",
                request.getCreditorId(), request.getFaceValue());

        Timer.Sample timer = metrics.startTimer();

        try {
            // 1. Buscar entidades
            Creditor creditor = creditorRepository.findById(request.getCreditorId())
                    .orElseThrow(() -> new EntityNotFoundException("Creditor not found with id: " + request.getCreditorId()));

            ReceivableType receivableType = receivableTypeRepository.findById(request.getReceivableTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("Receivable type not found with id: " + request.getReceivableTypeId()));

            Currency currency = currencyRepository.findById(request.getCurrencyCode())
                    .orElseThrow(() -> new EntityNotFoundException("Currency not found: " + request.getCurrencyCode()));

            // 2. Validar data de vencimento
            LocalDate now = LocalDate.now();
            if (request.getDueDate().isBefore(now)) {
                throw new InvalidOperationException("Due date must be in the future");
            }

            // 3. Calcular meses até o vencimento
            Period period = Period.between(now, request.getDueDate());
            int months = period.getMonths() + (period.getYears() * 12);
            if (months < 1) {
                months = 1; // Mínimo 1 mês
            }

            // 4. Obter taxa base (mensal)
            BigDecimal baseRate = baseRateService.getMonthlyBaseRate(currency.getCode());

            // 5. Calcular valor presente usando Strategy
            PricingStrategy strategy = strategyFactory.getStrategy(receivableType.getName());
            BigDecimal presentValue = strategy.calculatePresentValue(request.getFaceValue(), baseRate, months);

            // 6. Aplicar conversão cambial se necessário
            BigDecimal exchangeRate = null;
            String paymentCurrency = null;

            if (request.getPaymentCurrency() != null &&
                    !request.getPaymentCurrency().equals(currency.getCode())) {
                exchangeRate = exchangeRateService.getCurrentRate(currency.getCode(), request.getPaymentCurrency());
                presentValue = presentValue.multiply(exchangeRate);
                paymentCurrency = request.getPaymentCurrency();
                log.info("Applied exchange rate: {} {} to {} = {}", currency.getCode(),
                        request.getPaymentCurrency(), exchangeRate);
            }

            // 7. Criar e salvar transação
            Transaction transaction = new Transaction();
            transaction.setCreditor(creditor);
            transaction.setReceivableType(receivableType);
            transaction.setFaceValue(request.getFaceValue());
            transaction.setPresentValue(presentValue.setScale(2, RoundingMode.HALF_EVEN));
            transaction.setCurrency(currency);
            transaction.setDueDate(request.getDueDate());
            transaction.setSettlementDate(now);
            transaction.setBaseRate(baseRate);
            transaction.setAppliedSpread(strategy.getSpread());
            transaction.setExchangeRateUsed(exchangeRate);
            transaction.setPaymentCurrency(paymentCurrency);
            transaction.setStatus(TransactionStatus.SETTLED);
            transaction.setExternalReference(request.getExternalReference());
            transaction.setNotes(request.getNotes());

            Transaction saved = transactionRepository.save(transaction);

            // 8. Registrar métricas
            metrics.recordSettlement();
            timer.stop(metrics.getSettlementTimer());

            log.info("Transaction settled successfully: id={}, presentValue={}",
                    saved.getId(), saved.getPresentValue());

            return TransactionResponse.from(saved);

        } catch (Exception e) {
            log.error("Error processing settlement: {}", e.getMessage(), e);
            timer.stop(metrics.getSettlementTimer());
            throw e;
        }
    }

    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
    }

    public Transaction getTransactionByReference(String reference) {
        return transactionRepository.findByExternalReference(reference)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with reference: " + reference));
    }
}