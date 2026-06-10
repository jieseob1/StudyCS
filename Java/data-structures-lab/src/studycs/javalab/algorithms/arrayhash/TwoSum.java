package studycs.javalab.algorithms.arrayhash;

import java.util.HashMap;
import java.util.Map;

public final class TwoSum {
    private TwoSum() {
    }

    public static int[] findIndices(int[] nums, int target) {
        Map<Integer, Integer> indexByValue = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (indexByValue.containsKey(need)) {
                return new int[] {indexByValue.get(need), i};
            }
            indexByValue.put(nums[i], i);
        }

        return new int[] {-1, -1};
    }

    public static void trace(int[] nums, int target) {
        System.out.println("  twoSum trace");
        Map<Integer, Integer> indexByValue = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            System.out.println("    i=" + i + ", value=" + nums[i] + ", need=" + need + ", map=" + indexByValue);

            if (indexByValue.containsKey(need)) {
                System.out.println("    found: [" + indexByValue.get(need) + ", " + i + "]");
                return;
            }

            indexByValue.put(nums[i], i);
        }
    }
}
