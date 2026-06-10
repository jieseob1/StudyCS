package studycs.javalab.algorithms.heap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public final class MeetingRooms {
    private MeetingRooms() {
    }

    public static int minimumRequired(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(interval -> interval[0]));
        PriorityQueue<Integer> ends = new PriorityQueue<>();

        for (int[] interval : intervals) {
            if (!ends.isEmpty() && ends.peek() <= interval[0]) {
                ends.poll();
            }
            ends.offer(interval[1]);
        }

        return ends.size();
    }
}
