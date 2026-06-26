package org.example.srm.strategy;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePresentValue(BigDecimal faceValue, BigDecimal baseRate, int months);
    BigDecimal getSpread();
    String getStrategyName();
}