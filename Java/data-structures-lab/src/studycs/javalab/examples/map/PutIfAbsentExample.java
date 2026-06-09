package studycs.javalab.examples.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class PutIfAbsentExample {
    private PutIfAbsentExample() {
    }

    public static int eagerCreationCount() {
        Map<String, List<Integer>> map = new HashMap<>();
        AtomicInteger created = new AtomicInteger();

        map.putIfAbsent("numbers", createList(created));
        map.putIfAbsent("numbers", createList(created));

        return created.get();
    }

    public static void runDemo() {
        System.out.println("putIfAbsent creation count: " + eagerCreationCount());
    }

    private static List<Integer> createList(AtomicInteger created) {
        created.incrementAndGet();
        return new ArrayList<>();
    }
}
