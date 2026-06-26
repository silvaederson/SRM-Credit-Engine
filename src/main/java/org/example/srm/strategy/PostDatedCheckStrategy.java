package org.example.srm.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component("postDatedCheckStrategy")
@Slf4j
public class PostDatedCheckStrategy implements PricingStrategy {

    private static final BigDecimal SPREAD = new BigDecimal("0.025"); // 2.5% a.m.

    @Override
    public BigDecimal calculatePresentValue(BigDecimal faceValue, BigDecimal baseRate, int months) {
        log.debug("Calculating present value for Post-Dated Check: faceValue={}, baseRate={}, months={}",
                faceValue, baseRate, months);

        if (faceValue == null || faceValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Face value must be positive");
        }

        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }

        BigDecimal totalRate = baseRate.add(SPREAD);
        BigDecimal factor = BigDecimal.ONE.add(totalRate).pow(months, new MathContext(10));

        BigDecimal presentValue = faceValue.divide(factor, 2, RoundingMode.HALF_EVEN);

        log.debug("Calculated present value: {}", presentValue);
        return presentValue;
    }

    @Override
    public BigDecimal getSpread() {
        return SPREAD;
    }

    @Override
    public String getStrategyName() {
        return "Post-Dated Check";
    }
}