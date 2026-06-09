package studycs.javalab.examples;

import studycs.javalab.examples.stream.FilterMapExample;
import studycs.javalab.examples.stream.GroupingExample;
import studycs.javalab.examples.stream.LazyExecutionExample;
import studycs.javalab.examples.stream.PrimitiveStreamExample;
import studycs.javalab.examples.stream.SortedLimitExample;
import studycs.javalab.examples.stream.ToListExample;
import studycs.javalab.examples.stream.ToMapMergeExample;
import studycs.javalab.examples.stream.User;

import java.util.List;
import java.util.Map;

public final class StreamExamples {
    private StreamExamples() {
    }

    public static List<String> activeUppercaseNames(List<User> users) {
        return FilterMapExample.activeUppercaseNames(users);
    }

    public static Map<String, Long> countByTeam(List<User> users) {
        return GroupingExample.countByTeam(users);
    }

    public static int totalScore(List<User> users) {
        return PrimitiveStreamExample.totalScore(users);
    }

    public static Map<String, User> bestUserByTeam(List<User> users) {
        return ToMapMergeExample.bestUserByTeam(users);
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Stream demo ==");

        List<User> users = List.of(
            new User(1, "kim", "backend", true, 80),
            new User(2, "lee", "backend", false, 90),
            new User(3, "park", "frontend", true, 70),
            new User(4, "choi", "frontend", true, 95)
        );

        LazyExecutionExample.runDemo(users);
        FilterMapExample.runDemo(users);
        GroupingExample.runDemo(users);
        PrimitiveStreamExample.runDemo(users);
        ToMapMergeExample.runDemo(users);
        SortedLimitExample.runDemo(users);
        ToListExample.runDemo(users);
    }
}
