package studycs.javalab.examples.standard;

import java.util.Locale;
import java.util.Optional;

public final class OptionalExample {
    private OptionalExample() {
    }

    public record Profile(String nickname) {
    }

    public static String uppercaseNicknameOrDefault(Profile profile) {
        return Optional.ofNullable(profile)
            .map(Profile::nickname)
            .filter(nickname -> !nickname.isBlank())
            .map(nickname -> nickname.toUpperCase(Locale.ROOT))
            .orElse("ANONYMOUS");
    }

    public static void runDemo() {
        System.out.println("Optional present map/filter/orElse: " + uppercaseNicknameOrDefault(new Profile("kim")));
        System.out.println("Optional null fallback: " + uppercaseNicknameOrDefault(null));
    }
}
