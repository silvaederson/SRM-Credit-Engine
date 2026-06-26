package org.example.srm.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int calculateMonthsBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }

        Period period = Period.between(start, end);
        return period.getMonths() + (period.getYears() * 12);
    }

    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    public static boolean isValidBusinessDay(LocalDate date) {
        // Verifica se não é sábado ou domingo
        if (date == null) return false;
        return date.getDayOfWeek().getValue() <= 5;
    }

    public static LocalDate nextBusinessDay(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (!isValidBusinessDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }
}