package studycs.javalab.algorithms.backtracking;

import java.util.ArrayList;
import java.util.List;

public final class Permutations {
    private Permutations() {
    }

    public static List<List<Integer>> of(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    public static void runDemo() {
        System.out.println("Permutations [1,2,3] -> " + of(new int[] {1, 2, 3}));
    }

    private static void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }

            used[i] = true;
            path.add(nums[i]);
            backtrack(nums, used, path, result);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }
}
