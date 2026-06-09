package studycs.javalab.algorithms.heap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class TopKElements {
    private TopKElements() {
    }

    public static List<Integer> find(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<Integer> result = new ArrayList<>(minHeap);
        result.sort(Comparator.reverseOrder());
        return result;
    }
}
