package studycs.javalab.examples.standard;

import java.util.Arrays;

public final class ArraysUtilityExample {
    private ArraysUtilityExample() {
    }

    public static int[] sortedCopy(int[] values) {
        int[] copy = Arrays.copyOf(values, values.length);
        Arrays.sort(copy);
        return copy;
    }

    public static int binarySearch(int[] sorted, int target) {
        return Arrays.binarySearch(sorted, target);
    }

    public static String deepMatrixToString(int[][] matrix) {
        return Arrays.deepToString(matrix);
    }

    public static void runDemo() {
        System.out.println("Arrays.copyOf + sort: " + Arrays.toString(sortedCopy(new int[] {5, 1, 3, 2})));
        System.out.println("Arrays.binarySearch: " + binarySearch(new int[] {1, 3, 5, 7}, 5));
        System.out.println("Arrays.deepToString: " + deepMatrixToString(new int[][] {{1, 2}, {3, 4}}));
    }
}
