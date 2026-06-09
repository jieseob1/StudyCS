package studycs.javalab.examples.stream;

import java.util.List;

public final class ToListExample {
    private ToListExample() {
    }

    public static List<String> names(List<User> users) {
        return users.stream()
            .map(User::name)
            .toList();
    }

    public static void runDemo(List<User> users) {
        List<String> names = names(users);
        System.out.println("Stream.toList result class: " + names.getClass().getName());
        try {
            names.add("new-user");
        } catch (UnsupportedOperationException e) {
            System.out.println("Stream.toList is unmodifiable: " + e.getClass().getSimpleName());
        }
    }
}
