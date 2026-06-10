package studycs.javalab.structures.range;

public class FenwickTree {
    private final long[] tree;

    public FenwickTree(int n) {
        tree = new long[n + 1];
    }

    public void add(int index, long delta) {
        for (int i = index + 1; i < tree.length; i += i & -i) {
            tree[i] += delta;
        }
    }

    public long prefixSum(int index) {
        long sum = 0;
        for (int i = index + 1; i > 0; i -= i & -i) {
            sum += tree[i];
        }
        return sum;
    }

    public long rangeSum(int left, int right) {
        if (left == 0) {
            return prefixSum(right);
        }
        return prefixSum(right) - prefixSum(left - 1);
    }

    public static void runDemo() {
        FenwickTree fenwick = new FenwickTree(5);
        fenwick.add(0, 1);
        fenwick.add(1, 2);
        fenwick.add(2, 3);
        System.out.println("Fenwick rangeSum(0, 2): " + fenwick.rangeSum(0, 2));
        fenwick.add(1, 5);
        System.out.println("Fenwick after add index 1 by 5, rangeSum(0, 2): " + fenwick.rangeSum(0, 2));
    }
}
