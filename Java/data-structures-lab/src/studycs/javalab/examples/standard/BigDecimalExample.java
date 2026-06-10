package studycs.javalab.examples.standard;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BigDecimalExample {
    private BigDecimalExample() {
    }

    public static String addMoney(String left, String right) {
        return new BigDecimal(left)
            .add(new BigDecimal(right))
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString();
    }

    public static String divideHalfUp(String left, String right) {
        return new BigDecimal(left)
            .divide(new BigDecimal(right), 2, RoundingMode.HALF_UP)
            .toPlainString();
    }

    public static void runDemo() {
        System.out.println("BigDecimal add money: " + addMoney("0.10", "0.20"));
        System.out.println("BigDecimal divide half-up: " + divideHalfUp("10", "3"));
    }
}
