package org.example.srm.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportDTO {
    private Long transactionId;
    private String creditorName;
    private String receivableType;
    private BigDecimal faceValue;
    private BigDecimal presentValue;
    private String currencyCode;
    private LocalDate settlementDate;
    private String status;
    private BigDecimal discountRate;
}