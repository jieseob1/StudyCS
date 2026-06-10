package com.studycs.datastructures;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class HashStructureLab {
    private HashStructureLab() {
    }

    public static String hashSetDuplicateTrace() {
        Set<BadHashKey> set = new HashSet<>();

        boolean first = set.add(new BadHashKey("A"));
        boolean duplicate = set.add(new BadHashKey("A"));
        boolean second = set.add(new BadHashKey("B"));

        return "added=[" + first + ", " + duplicate + ", " + second + "], size=" + set.size()
                + ", containsSameId=" + set.contains(new BadHashKey("A"));
    }

    public static String toyHashMapCollisionTrace() {
        ToyHashMap<BadHashKey, String> map = new ToyHashMap<>(2);
        map.put(new BadHashKey("A"), "alpha");
        map.put(new BadHashKey("B"), "bravo");
        map.put(new BadHashKey("C"), "charlie");

        return "size=" + map.size()
                + ", capacity=" + map.capacity()
                + ", get(B)=" + map.get(new BadHashKey("B"))
                + ", buckets=" + map.bucketSnapshot();
    }

    public static final class BadHashKey {
        private final String id;

        public BadHashKey(String id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof BadHashKey)) {
                return false;
            }
            BadHashKey that = (BadHashKey) other;
            return Objects.equals(id, that.id);
        }

        @Override
        public String toString() {
            return "BadHashKey(" + id + ")";
        }
    }
}
