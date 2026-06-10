package studycs.javalab.algorithms.sorting;

public final class QuickSort {
    private QuickSort() {
    }

    public static int[] sort(int[] input) {
        int[] arr = input.clone();
        quickSort(arr, 0, arr.length - 1);
        return arr;
    }

    public static void runDemo() {
        System.out.println("QuickSort [5,1,4,2,3] -> " + java.util.Arrays.toString(sort(new int[] {5, 1, 4, 2, 3})));
    }

    private static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(arr, left, right);
        quickSort(arr, left, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int smallerEnd = left - 1;

        for (int current = left; current < right; current++) {
            if (arr[current] <= pivot) {
                smallerEnd++;
                swap(arr, smallerEnd, current);
            }
        }

        swap(arr, smallerEnd + 1, right);
        return smallerEnd + 1;
    }

    private static void swap(int[] arr, int left, int right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}
