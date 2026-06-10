package studycs.javalab.collections;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class SetExample {
    private SetExample() {
    }

    public static void runDemo() {
        Set<String> orderedUnique = new LinkedHashSet<>(List.of("java", "map", "java", "stream"));
        System.out.println("LinkedHashSet keeps first insertion order while removing duplicates: " + orderedUnique);

        Set<Integer> sortedUnique = new TreeSet<>(List.of(3, 1, 2, 3));
        System.out.println("TreeSet keeps sorted unique values: " + sortedUnique);
    }
}
