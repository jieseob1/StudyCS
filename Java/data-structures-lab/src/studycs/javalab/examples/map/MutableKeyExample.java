package studycs.javalab.examples.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MutableKeyExample {
    private MutableKeyExample() {
    }

    public static String lookupAfterMutatingKey() {
        Map<MutableKey, String> mutableKeyMap = new HashMap<>();
        MutableKey key = new MutableKey("before@example.com");
        mutableKeyMap.put(key, "saved");
        key.email = "after@example.com";
        return mutableKeyMap.get(key);
    }

    public static void runDemo() {
        System.out.println("HashMap lookup after mutating key field: " + lookupAfterMutatingKey());
    }

    private static final class MutableKey {
        private String email;

        private MutableKey(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MutableKey that && Objects.equals(email, that.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email);
        }
    }
}
