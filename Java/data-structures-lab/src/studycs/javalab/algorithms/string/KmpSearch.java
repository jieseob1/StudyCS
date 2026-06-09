package studycs.javalab.algorithms.string;

import java.util.ArrayList;
import java.util.List;

public final class KmpSearch {
    private KmpSearch() {
    }

    public static List<Integer> search(String text, String pattern) {
        int[] lps = buildLps(pattern);
        List<Integer> result = new ArrayList<>();
        int matched = 0;

        for (int i = 0; i < text.length(); i++) {
            while (matched > 0 && text.charAt(i) != pattern.charAt(matched)) {
                matched = lps[matched - 1];
            }

            if (text.charAt(i) == pattern.charAt(matched)) {
                matched++;
            }

            if (matched == pattern.length()) {
                result.add(i - pattern.length() + 1);
                matched = lps[matched - 1];
            }
        }

        return result;
    }

    public static void runDemo() {
        System.out.println("KMP search ababc in ababcababc -> " + search("ababcababc", "ababc"));
    }

    private static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;

        for (int i = 1; i < pattern.length(); i++) {
            while (length > 0 && pattern.charAt(i) != pattern.charAt(length)) {
                length = lps[length - 1];
            }

            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
            }
        }

        return lps;
    }
}
