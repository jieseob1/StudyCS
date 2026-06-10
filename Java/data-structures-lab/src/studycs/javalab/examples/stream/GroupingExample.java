package studycs.javalab.examples.stream;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GroupingExample {
    private GroupingExample() {
    }

    public static Map<String, Long> countByTeam(List<User> users) {
        return users.stream()
            .collect(Collectors.groupingBy(User::team, Collectors.counting()));
    }

    public static void runDemo(List<User> users) {
        System.out.println("count by team: " + countByTeam(users));
    }
}
