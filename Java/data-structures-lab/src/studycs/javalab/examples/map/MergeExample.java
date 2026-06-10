package studycs.javalab.examples.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MergeExample {
    private MergeExample() {
    }

    public static Map<String, Integer> countWords(List<String> words) {
        Map<String, Integer> count = new LinkedHashMap<>();
        for (String word : words) {
            count.merge(word, 1, Integer::sum);
        }
        return count;
    }

    public static void runDemo() {
        System.out.println("word count using merge: " + countWords(List.of("java", "stream", "java", "java")));
    }
}
