package org.example.srm.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    public static final int DEFAULT_SCALE = 2;
    public static final int HIGH_PRECISION_SCALE = 10;

    public static BigDecimal roundToTwoDecimals(BigDecimal value) {
        return value != null ? value.setScale(DEFAULT_SCALE, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
    }

    public static BigDecimal round(BigDecimal value, int scale) {
        return value != null ? value.setScale(scale, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
    }

    public static BigDecimal percentageOf(BigDecimal total, double percentage) {
        if (total == null) return BigDecimal.ZERO;
        BigDecimal pct = BigDecimal.valueOf(percentage);
        return total.multiply(pct).divide(BigDecimal.valueOf(100), DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    }

    public static boolean isGreaterThan(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) > 0;
    }

    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) < 0;
    }

    public static boolean isBetween(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value != null && min != null && max != null &&
                value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    public static BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.add(b);
    }

    public static BigDecimal safeSubtract(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.subtract(b);
    }
}