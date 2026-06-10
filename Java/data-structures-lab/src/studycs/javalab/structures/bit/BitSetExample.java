package studycs.javalab.structures.bit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class BitSetExample {
    private BitSetExample() {
    }

    public static List<Integer> selectedIndexes(int... indexes) {
        BitSet bitSet = new BitSet();
        for (int index : indexes) {
            bitSet.set(index);
        }

        List<Integer> selected = new ArrayList<>();
        for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
            selected.add(index);
        }
        return selected;
    }

    public static void runDemo() {
        System.out.println("BitSet selected indexes: " + selectedIndexes(1, 3, 10));
    }
}
