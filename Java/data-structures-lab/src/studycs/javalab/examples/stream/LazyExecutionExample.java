package studycs.javalab.examples.stream;

import java.util.List;
import java.util.stream.Stream;

public final class LazyExecutionExample {
    private LazyExecutionExample() {
    }

    public static void runDemo(List<User> users) {
        Stream<String> lazyStream = users.stream()
            .filter(user -> {
                System.out.println("filter runs only after terminal operation: " + user.name());
                return user.active();
            })
            .map(User::name);

        System.out.println("stream pipeline created");
        System.out.println("terminal count: " + lazyStream.count());
    }
}
