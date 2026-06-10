package studycs.javalab.collections;

import java.util.Arrays;
import java.util.PriorityQueue;

public final class PriorityQueueExample {
    private PriorityQueueExample() {
    }

    public static void runDemo() {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(Arrays.asList(5, 1, 3));
        System.out.print("PriorityQueue poll order:");
        while (!minHeap.isEmpty()) {
            System.out.print(" " + minHeap.poll());
        }
        System.out.println();
    }
}
