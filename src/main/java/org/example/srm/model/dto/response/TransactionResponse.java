package org.example.srm.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.srm.model.entity.Transaction;
import org.example.srm.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long creditorId;
    private String creditorName;
    private Long receivableTypeId;
    private String receivableTypeName;
    private BigDecimal faceValue;
    private BigDecimal presentValue;
    private String currencyCode;
    private LocalDate dueDate;
    private LocalDate settlementDate;
    private BigDecimal baseRate;
    private BigDecimal appliedSpread;
    private BigDecimal exchangeRateUsed;
    private String paymentCurrency;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private String externalReference;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .creditorId(transaction.getCreditor().getId())
                .creditorName(transaction.getCreditor().getName())
                .receivableTypeId(transaction.getReceivableType().getId())
                .receivableTypeName(transaction.getReceivableType().getName())
                .faceValue(transaction.getFaceValue())
                .presentValue(transaction.getPresentValue())
                .currencyCode(transaction.getCurrency().getCode())
                .dueDate(transaction.getDueDate())
                .settlementDate(transaction.getSettlementDate())
                .baseRate(transaction.getBaseRate())
                .appliedSpread(transaction.getAppliedSpread())
                .exchangeRateUsed(transaction.getExchangeRateUsed())
                .paymentCurrency(transaction.getPaymentCurrency())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .externalReference(transaction.getExternalReference())
                .build();
    }
}