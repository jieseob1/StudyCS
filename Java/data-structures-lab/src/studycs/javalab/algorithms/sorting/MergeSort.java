package studycs.javalab.algorithms.sorting;

public final class MergeSort {
    private MergeSort() {
    }

    public static int[] sort(int[] input) {
        int[] arr = input.clone();
        int[] temp = new int[arr.length];
        sort(arr, temp, 0, arr.length - 1);
        return arr;
    }

    public static void runDemo() {
        System.out.println("MergeSort [5,1,4,2,3] -> " + java.util.Arrays.toString(sort(new int[] {5, 1, 4, 2, 3})));
    }

    private static void sort(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int mid = (left + right) / 2;
        sort(arr, temp, left, mid);
        sort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        while (i <= mid) {
            temp[k++] = arr[i++];
        }
        while (j <= right) {
            temp[k++] = arr[j++];
        }
        for (int index = left; index <= right; index++) {
            arr[index] = temp[index];
        }
    }
}
