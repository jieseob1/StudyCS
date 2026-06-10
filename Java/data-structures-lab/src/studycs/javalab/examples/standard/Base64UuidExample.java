package studycs.javalab.examples.standard;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public final class Base64UuidExample {
    private Base64UuidExample() {
    }

    public static String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    public static UUID stableUuidFromName(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public static void runDemo() {
        System.out.println("Base64 encode: " + encodeBase64("java"));
        System.out.println("Base64 decode: " + decodeBase64("amF2YQ=="));
        System.out.println("UUID.nameUUIDFromBytes: " + stableUuidFromName("studycs"));
    }
}
