package studycs.javalab;

import studycs.javalab.collections.ArrayListExample;
import studycs.javalab.collections.DequeExample;
import studycs.javalab.collections.PriorityQueueExample;
import studycs.javalab.collections.SetExample;

public final class CollectionExamples {
    private CollectionExamples() {
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Collection demo ==");

        ArrayListExample.runDemo();
        SetExample.runDemo();
        DequeExample.runDemo();
        PriorityQueueExample.runDemo();
    }
}
