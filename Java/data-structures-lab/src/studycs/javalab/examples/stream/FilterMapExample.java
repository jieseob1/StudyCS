package studycs.javalab.examples.stream;

import java.util.List;

public final class FilterMapExample {
    private FilterMapExample() {
    }

    public static List<String> activeUppercaseNames(List<User> users) {
        return users.stream()
            .filter(User::active)
            .map(User::name)
            .map(String::toUpperCase)
            .toList();
    }

    public static void runDemo(List<User> users) {
        System.out.println("active uppercase names: " + activeUppercaseNames(users));
    }
}
