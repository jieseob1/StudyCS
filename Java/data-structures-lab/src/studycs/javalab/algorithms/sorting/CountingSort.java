package studycs.javalab.algorithms.sorting;

public final class CountingSort {
    private CountingSort() {
    }

    public static int[] sort(int[] input, int maxValue) {
        int[] count = new int[maxValue + 1];
        for (int value : input) {
            count[value]++;
        }

        int[] result = new int[input.length];
        int index = 0;
        for (int value = 0; value <= maxValue; value++) {
            while (count[value] > 0) {
                result[index++] = value;
                count[value]--;
            }
        }
        return result;
    }

    public static void runDemo() {
        System.out.println("CountingSort [3,1,2,3,0,2] -> "
            + java.util.Arrays.toString(sort(new int[] {3, 1, 2, 3, 0, 2}, 3)));
    }
}
