package studycs.javalab.algorithms.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CombinationSum {
    private CombinationSum() {
    }

    public static List<List<Integer>> find(int[] candidates, int target) {
        int[] sorted = candidates.clone();
        Arrays.sort(sorted);

        List<List<Integer>> result = new ArrayList<>();
        backtrack(sorted, target, 0, new ArrayList<>(), result);
        return result;
    }

    public static void runDemo() {
        System.out.println("CombinationSum target 7 -> " + find(new int[] {2, 3, 6, 7}, 7));
    }

    private static void backtrack(
        int[] candidates,
        int remaining,
        int start,
        List<Integer> path,
        List<List<Integer>> result
    ) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) {
                return;
            }

            path.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, path, result);
            path.remove(path.size() - 1);
        }
    }
}
