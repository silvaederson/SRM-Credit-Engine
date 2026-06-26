package org.example.srm.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {
    @NotNull(message = "Creditor ID is required")
    private Long creditorId;

    @NotNull(message = "Receivable type ID is required")
    private Long receivableTypeId;

    @NotNull(message = "Face value is required")
    @DecimalMin(value = "0.01", message = "Face value must be greater than 0.01")
    @DecimalMax(value = "999999999.99", message = "Face value exceeds maximum limit")
    private BigDecimal faceValue;

    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    private String currencyCode;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @Size(min = 3, max = 3, message = "Payment currency must be exactly 3 characters")
    private String paymentCurrency;

    @Size(max = 100)
    private String externalReference;

    @Size(max = 500)
    private String notes;
}