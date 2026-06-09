package studycs.javalab.algorithms.arrayhash;

import java.util.HashMap;
import java.util.Map;

public final class LongestSubstringWithoutRepeating {
    private LongestSubstringWithoutRepeating() {
    }

    public static int lengthOf(String text) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        int left = 0;
        int best = 0;

        for (int right = 0; right < text.length(); right++) {
            char current = text.charAt(right);
            if (lastIndex.containsKey(current)) {
                left = Math.max(left, lastIndex.get(current) + 1);
            }

            lastIndex.put(current, right);
            best = Math.max(best, right - left + 1);
        }

        return best;
    }

    public static void trace(String text) {
        System.out.println("  sliding window trace");
        Map<Character, Integer> lastIndex = new HashMap<>();
        int left = 0;
        int best = 0;

        for (int right = 0; right < text.length(); right++) {
            char current = text.charAt(right);
            if (lastIndex.containsKey(current)) {
                int nextLeft = Math.max(left, lastIndex.get(current) + 1);
                System.out.println("    duplicate '" + current + "' -> left " + left + " -> " + nextLeft);
                left = nextLeft;
            }

            lastIndex.put(current, right);
            best = Math.max(best, right - left + 1);
            System.out.println("    window=[" + left + "," + right + "] text="
                + text.substring(left, right + 1) + ", best=" + best);
        }
    }
}
