package studycs.javalab.examples.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CollectionsUtilityExample {
    private CollectionsUtilityExample() {
    }

    public static List<String> reversedCopy(List<String> values) {
        List<String> copy = new ArrayList<>(values);
        Collections.reverse(copy);
        return copy;
    }

    public static int frequency(List<String> values, String target) {
        return Collections.frequency(values, target);
    }

    public static List<String> unmodifiableCopy(List<String> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    public static void runDemo() {
        System.out.println("Collections.reverse copy: " + reversedCopy(List.of("a", "b", "c")));
        System.out.println("Collections.frequency: " + frequency(List.of("java", "api", "java"), "java"));
        System.out.println("Collections.unmodifiableList: " + unmodifiableCopy(List.of("safe", "copy")));
    }
}
