package studycs.javalab.examples.map;

import studycs.javalab.structures.cache.LruCache;

public final class LinkedHashMapLruExample {
    private LinkedHashMapLruExample() {
    }

    public static void runDemo() {
        LruCache<Integer, String> cache = new LruCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.get(1);
        cache.put(3, "three");
        System.out.println("LRU after access key 1 then put key 3: " + cache);
    }
}
