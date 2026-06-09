package studycs.javalab.examples.map;

import java.util.HashMap;
import java.util.Map;

public final class ComputeExample {
    private ComputeExample() {
    }

    public static boolean computeReturningNullKeepsKey() {
        Map<String, Integer> map = new HashMap<>();
        map.put("java", 1);
        map.compute("java", (key, oldValue) -> null);
        return map.containsKey("java");
    }

    public static void runDemo() {
        System.out.println("compute returns null -> key remains? " + computeReturningNullKeepsKey());
    }
}
