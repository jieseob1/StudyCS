package studycs.javalab.algorithms.arrayhash;

public class PrefixSum {
    private final int[] prefix;

    private PrefixSum(int[] arr) {
        prefix = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            prefix[i + 1] = prefix[i] + arr[i];
        }
    }

    public static PrefixSum from(int[] arr) {
        return new PrefixSum(arr);
    }

    public int rangeSum(int left, int right) {
        return prefix[right + 1] - prefix[left];
    }

    public static void runDemo() {
        PrefixSum prefixSum = PrefixSum.from(new int[] {2, 4, 6, 8});
        System.out.println("PrefixSum rangeSum(1,3): " + prefixSum.rangeSum(1, 3));
    }
}
