package studycs.javalab.examples.stream;

import java.util.List;

public final class PrimitiveStreamExample {
    private PrimitiveStreamExample() {
    }

    public static int totalScore(List<User> users) {
        return users.stream()
            .mapToInt(User::score)
            .sum();
    }

    public static void runDemo(List<User> users) {
        System.out.println("total score using mapToInt: " + totalScore(users));
    }
}
