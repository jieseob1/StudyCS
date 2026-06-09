package studycs.javalab.examples.map;

import java.util.TreeMap;

public final class TreeMapExample {
    private TreeMapExample() {
    }

    public static void runDemo() {
        TreeMap<Integer, String> scoreBands = new TreeMap<>();
        scoreBands.put(60, "D");
        scoreBands.put(70, "C");
        scoreBands.put(80, "B");
        scoreBands.put(90, "A");
        System.out.println("TreeMap floorKey(85): " + scoreBands.floorKey(85));
        System.out.println("TreeMap ceilingKey(85): " + scoreBands.ceilingKey(85));
    }
}
