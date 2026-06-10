package studycs.javalab.examples.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ComputeIfAbsentExample {
    private ComputeIfAbsentExample() {
    }

    public static int lazyCreationCount() {
        Map<String, List<Integer>> map = new HashMap<>();
        AtomicInteger created = new AtomicInteger();

        map.computeIfAbsent("numbers", key -> createList(created));
        map.computeIfAbsent("numbers", key -> createList(created));

        return created.get();
    }

    public static Map<String, List<Integer>> groupNumbers() {
        Map<String, List<Integer>> groups = new LinkedHashMap<>();
        groups.computeIfAbsent("even", key -> new ArrayList<>()).add(2);
        groups.computeIfAbsent("even", key -> new ArrayList<>()).add(4);
        groups.computeIfAbsent("odd", key -> new ArrayList<>()).add(1);
        return groups;
    }

    public static void runDemo() {
        System.out.println("computeIfAbsent creation count: " + lazyCreationCount());
        System.out.println("grouping with computeIfAbsent: " + groupNumbers());
    }

    private static List<Integer> createList(AtomicInteger created) {
        created.incrementAndGet();
        return new ArrayList<>();
    }
}
