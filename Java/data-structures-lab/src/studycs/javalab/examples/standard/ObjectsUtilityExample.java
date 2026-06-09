package studycs.javalab.examples.standard;

import java.util.Objects;

public final class ObjectsUtilityExample {
    private ObjectsUtilityExample() {
    }

    public static String defaultName(String name) {
        return Objects.requireNonNullElse(name, "unknown");
    }

    public static boolean safeEquals(String left, String right) {
        return Objects.equals(left, right);
    }

    public static int stableHash(String name, int score) {
        return Objects.hash(name, score);
    }

    public static void runDemo() {
        System.out.println("Objects.requireNonNullElse: " + defaultName(null));
        System.out.println("Objects.equals: " + safeEquals("java", new String("java")));
        System.out.println("Objects.hash: " + stableHash("java", 100));
    }
}
