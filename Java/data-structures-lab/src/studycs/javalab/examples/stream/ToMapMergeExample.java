package studycs.javalab.examples.stream;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ToMapMergeExample {
    private ToMapMergeExample() {
    }

    public static Map<String, User> bestUserByTeam(List<User> users) {
        return users.stream()
            .collect(Collectors.toMap(
                User::team,
                Function.identity(),
                (left, right) -> left.score() >= right.score() ? left : right
            ));
    }

    public static void runDemo(List<User> users) {
        System.out.println("best user by team with toMap merge: " + bestUserByTeam(users));
    }
}
