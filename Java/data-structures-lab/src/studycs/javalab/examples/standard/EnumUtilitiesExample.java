package studycs.javalab.examples.standard;

import java.util.EnumMap;
import java.util.EnumSet;

public final class EnumUtilitiesExample {
    private EnumUtilitiesExample() {
    }

    public enum Permission {
        READ,
        WRITE,
        DELETE
    }

    public enum Status {
        READY,
        ACTIVE,
        DONE
    }

    public static boolean hasReadPermission() {
        EnumSet<Permission> permissions = EnumSet.of(Permission.READ, Permission.WRITE);
        return permissions.contains(Permission.READ);
    }

    public static String statusMessage(Status status) {
        EnumMap<Status, String> messages = new EnumMap<>(Status.class);
        messages.put(Status.READY, "waiting");
        messages.put(Status.ACTIVE, "running");
        messages.put(Status.DONE, "finished");
        return messages.get(status);
    }

    public static void runDemo() {
        System.out.println("EnumSet permissions contains READ: " + hasReadPermission());
        System.out.println("EnumMap status ACTIVE: " + statusMessage(Status.ACTIVE));
    }
}
