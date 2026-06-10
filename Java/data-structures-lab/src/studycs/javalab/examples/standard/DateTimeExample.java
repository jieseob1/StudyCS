package studycs.javalab.examples.standard;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

public final class DateTimeExample {
    private DateTimeExample() {
    }

    public static long daysBetween(String startInclusive, String endExclusive) {
        return ChronoUnit.DAYS.between(LocalDate.parse(startInclusive), LocalDate.parse(endExclusive));
    }

    public static String yearMonth(String date) {
        return YearMonth.from(LocalDate.parse(date)).toString();
    }

    public static void runDemo() {
        System.out.println("LocalDate daysBetween: " + daysBetween("2026-06-01", "2026-06-10"));
        System.out.println("YearMonth.from: " + yearMonth("2026-06-09"));
    }
}
