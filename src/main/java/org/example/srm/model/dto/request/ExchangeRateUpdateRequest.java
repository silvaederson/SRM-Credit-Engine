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
public class ExchangeRateUpdateRequest {
    @NotBlank(message = "From currency is required")
    @Size(min = 3, max = 3)
    private String fromCurrency;

    @NotBlank(message = "To currency is required")
    @Size(min = 3, max = 3)
    private String toCurrency;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.000001", message = "Rate must be greater than 0")
    @DecimalMax(value = "999999.999999", message = "Rate exceeds maximum limit")
    private BigDecimal rate;

    @NotNull(message = "Effective date is required")
    @PastOrPresent(message = "Effective date cannot be in the future")
    private LocalDate effectiveDate;
}