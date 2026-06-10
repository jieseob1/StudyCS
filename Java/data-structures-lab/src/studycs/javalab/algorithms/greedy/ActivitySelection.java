package studycs.javalab.algorithms.greedy;

import java.util.Arrays;
import java.util.Comparator;

public final class ActivitySelection {
    private ActivitySelection() {
    }

    public static int maximumNonOverlapping(int[][] intervals) {
        int[][] sorted = intervals.clone();
        Arrays.sort(sorted, Comparator.comparingInt(interval -> interval[1]));

        int count = 0;
        int currentEnd = Integer.MIN_VALUE;
        for (int[] interval : sorted) {
            if (interval[0] >= currentEnd) {
                count++;
                currentEnd = interval[1];
            }
        }

        return count;
    }

    public static void runDemo() {
        System.out.println("ActivitySelection max -> " + maximumNonOverlapping(new int[][] {
            {1, 3}, {2, 4}, {3, 5}
        }));
    }
}
