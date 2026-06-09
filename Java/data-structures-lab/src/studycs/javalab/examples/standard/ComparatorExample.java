package studycs.javalab.examples.standard;

import java.util.Comparator;
import java.util.List;

public final class ComparatorExample {
    private ComparatorExample() {
    }

    public record Score(String name, int score) {
    }

    public static List<String> sortByScoreDescThenName(List<Score> scores) {
        return scores.stream()
            .sorted(
                Comparator.comparingInt(Score::score)
                    .reversed()
                    .thenComparing(Score::name)
            )
            .map(score -> score.name() + ":" + score.score())
            .toList();
    }

    public static void runDemo() {
        System.out.println("Comparator chaining: " + sortByScoreDescThenName(List.of(
            new Score("park", 80),
            new Score("lee", 90),
            new Score("kim", 80)
        )));
    }
}
