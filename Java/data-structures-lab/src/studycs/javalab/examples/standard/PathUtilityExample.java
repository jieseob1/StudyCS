package studycs.javalab.examples.standard;

import java.nio.file.Path;

public final class PathUtilityExample {
    private PathUtilityExample() {
    }

    public static String normalized(String path) {
        return Path.of(path).normalize().toString();
    }

    public static String fileName(String path) {
        return Path.of(path).getFileName().toString();
    }

    public static void runDemo() {
        System.out.println("Path.normalize: " + normalized("logs/../config/app.yml"));
        System.out.println("Path.getFileName: " + fileName("config/app.yml"));
    }
}
