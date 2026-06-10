package studycs.javalab.algorithms.dp;

import studycs.javalab.algorithms.search.BinarySearch;

public final class LongestIncreasingSubsequence {
    private LongestIncreasingSubsequence() {
    }

    public static int lengthOf(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {
            int index = BinarySearch.lowerBound(java.util.Arrays.copyOf(tails, size), num);
            tails[index] = num;
            if (index == size) {
                size++;
            }
        }

        return size;
    }

    public static void runDemo() {
        System.out.println("LIS length -> " + lengthOf(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
    }
}
