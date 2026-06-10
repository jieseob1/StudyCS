package studycs.javalab.algorithms.stackqueue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class NextGreaterElement {
    private NextGreaterElement() {
    }

    public static int[] find(int[] nums) {
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);

        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }

        return result;
    }

    public static void trace(int[] nums) {
        System.out.println("  monotonic stack trace");
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                int resolvedIndex = stack.pop();
                System.out.println("    pop index=" + resolvedIndex + ", value=" + nums[resolvedIndex]
                    + " -> next greater=" + nums[i]);
            }

            stack.push(i);
            System.out.println("    push index=" + i + ", stack indexes=" + stack);
        }
    }
}
