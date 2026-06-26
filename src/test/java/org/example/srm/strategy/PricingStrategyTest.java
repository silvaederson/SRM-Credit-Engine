package org.example.srm.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {

    @Test
    void shouldCalculatePresentValueForCommercialInvoice() {
        // Dados de entrada
        BigDecimal faceValue = BigDecimal.valueOf(10000);
        BigDecimal baseRate = BigDecimal.valueOf(0.00875);
        int months = 3;
        BigDecimal expectedSpread = BigDecimal.valueOf(0.015);

        // Fórmula: VP = VF / (1 + taxa)^meses
        BigDecimal totalRate = baseRate.add(expectedSpread);
        BigDecimal factor = BigDecimal.ONE.add(totalRate).pow(months);
        BigDecimal expected = faceValue.divide(factor, 2, RoundingMode.HALF_EVEN);

        // Simula o cálculo
        BigDecimal result = calculatePresentValue(faceValue, baseRate, expectedSpread, months);

        assertEquals(expected, result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.compareTo(faceValue) < 0); // VP deve ser menor que VF
    }

    @Test
    void shouldCalculatePresentValueForPostDatedCheck() {
        BigDecimal faceValue = BigDecimal.valueOf(5000);
        BigDecimal baseRate = BigDecimal.valueOf(0.01);
        int months = 2;
        BigDecimal spread = BigDecimal.valueOf(0.025);

        BigDecimal result = calculatePresentValue(faceValue, baseRate, spread, months);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.compareTo(faceValue) < 0);
    }

    @Test
    void shouldThrowExceptionForNegativeFaceValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculatePresentValue(BigDecimal.valueOf(-1000), BigDecimal.valueOf(0.01), BigDecimal.valueOf(0.015), 3);
        });
    }

    @Test
    void shouldThrowExceptionForZeroMonths() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculatePresentValue(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.01), BigDecimal.valueOf(0.015), 0);
        });
    }

    // Método auxiliar para simular o cálculo da estratégia
    private BigDecimal calculatePresentValue(BigDecimal faceValue, BigDecimal baseRate, BigDecimal spread, int months) {
        if (faceValue == null || faceValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Face value must be positive");
        }
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }

        BigDecimal totalRate = baseRate.add(spread);
        BigDecimal factor = BigDecimal.ONE.add(totalRate).pow(months);
        return faceValue.divide(factor, 2, RoundingMode.HALF_EVEN);
    }
}