package studycs.javalab.examples.stream;

import java.util.Comparator;
import java.util.List;

public final class SortedLimitExample {
    private SortedLimitExample() {
    }

    public static List<String> topNamesByScore(List<User> users, int limit) {
        return users.stream()
            .sorted(Comparator.comparingInt(User::score).reversed())
            .limit(limit)
            .map(User::name)
            .toList();
    }

    public static void runDemo(List<User> users) {
        System.out.println("top 2 names by score: " + topNamesByScore(users, 2));
    }
}
