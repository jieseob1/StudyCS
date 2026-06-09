package studycs.javalab.examples;

import studycs.javalab.examples.map.ComputeExample;
import studycs.javalab.examples.map.ComputeIfAbsentExample;
import studycs.javalab.examples.map.LinkedHashMapLruExample;
import studycs.javalab.examples.map.MergeExample;
import studycs.javalab.examples.map.MutableKeyExample;
import studycs.javalab.examples.map.PutIfAbsentExample;
import studycs.javalab.examples.map.TreeMapExample;

import java.util.List;
import java.util.Map;

public final class MapApiExamples {
    private MapApiExamples() {
    }

    public static int putIfAbsentEagerCreationCount() {
        return PutIfAbsentExample.eagerCreationCount();
    }

    public static int computeIfAbsentLazyCreationCount() {
        return ComputeIfAbsentExample.lazyCreationCount();
    }

    public static Map<String, Integer> countWords(List<String> words) {
        return MergeExample.countWords(words);
    }

    public static boolean computeReturningNullKeepsKey() {
        return ComputeExample.computeReturningNullKeepsKey();
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Map API demo ==");

        PutIfAbsentExample.runDemo();
        ComputeIfAbsentExample.runDemo();
        MergeExample.runDemo();
        ComputeExample.runDemo();
        TreeMapExample.runDemo();
        LinkedHashMapLruExample.runDemo();
        MutableKeyExample.runDemo();
    }
}
