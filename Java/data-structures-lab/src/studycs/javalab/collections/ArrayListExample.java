package studycs.javalab.collections;

import java.util.ArrayList;
import java.util.List;

public final class ArrayListExample {
    private ArrayListExample() {
    }

    public static void runDemo() {
        List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
        list.add(1, 15);
        System.out.println("ArrayList after middle insert: " + list);
        list.remove(1);
        System.out.println("ArrayList after middle remove: " + list);
    }
}
