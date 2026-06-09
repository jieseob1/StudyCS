package com.studycs.datastructures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MapOperationLab {
    private MapOperationLab() {
    }

    public static String computeIfAbsentTrace() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("existing", "ready");
        map.put("null-key", null);

        List<String> calls = new ArrayList<>();

        map.computeIfAbsent("existing", key -> {
            calls.add("load:" + key);
            return "created-for-" + key;
        });

        map.computeIfAbsent("new-key", key -> {
            calls.add("load:" + key);
            return "created-for-" + key;
        });

        map.computeIfAbsent("null-key", key -> {
            calls.add("load:" + key);
            return "created-for-" + key;
        });

        return "calls=" + calls + ", map=" + map;
    }

    public static String putIfAbsentTrace() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("existing", "ready");
        map.put("null-key", null);

        List<String> returns = new ArrayList<>();
        returns.add(String.valueOf(map.putIfAbsent("new-key", "created")));
        returns.add(String.valueOf(map.putIfAbsent("existing", "ignored")));
        returns.add(String.valueOf(map.putIfAbsent("null-key", "now-filled")));

        return "returns=" + returns + ", map=" + map;
    }

    public static String computeAndMergeTrace() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("count", 1);
        map.put("remove-me", 9);

        List<String> calls = new ArrayList<>();

        map.compute("count", (key, value) -> {
            calls.add("compute:" + key + "=" + value);
            return value + 1;
        });

        map.compute("remove-me", (key, value) -> {
            calls.add("compute:" + key + "=" + value);
            return null;
        });

        map.merge("count", 3, (oldValue, newValue) -> {
            calls.add("merge-present:" + oldValue + "+" + newValue);
            return oldValue + newValue;
        });

        map.merge("missing", 7, (oldValue, newValue) -> {
            calls.add("merge-missing-should-not-run");
            return oldValue + newValue;
        });

        return "calls=" + calls + ", map=" + map;
    }
}
