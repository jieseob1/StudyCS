package studycs.javalab.examples.standard;

import java.util.List;
import java.util.Locale;

public final class StringUtilitiesExample {
    private StringUtilitiesExample() {
    }

    public static String normalizeWords(String text) {
        return text.trim()
            .replaceAll("\\s+", " ")
            .toLowerCase(Locale.ROOT);
    }

    public static String joinWithComma(List<String> values) {
        return String.join(",", values);
    }

    public static String buildMessage(String prefix, int count) {
        return new StringBuilder()
            .append(prefix)
            .append(":")
            .append(count)
            .toString();
    }

    public static void runDemo() {
        System.out.println("String normalizeWords: " + normalizeWords("  Java   Utility API  "));
        System.out.println("String.join: " + joinWithComma(List.of("java", "stream", "map")));
        System.out.println("StringBuilder: " + buildMessage("count", 3));
    }
}
