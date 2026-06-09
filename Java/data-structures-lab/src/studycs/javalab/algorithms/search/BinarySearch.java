package studycs.javalab.algorithms.search;

public final class BinarySearch {
    private BinarySearch() {
    }

    public static int indexOf(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                return mid;
            }
            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static int lowerBound(int[] arr, int target) {
        int left = 0;
        int right = arr.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static int upperBound(int[] arr, int target) {
        int left = 0;
        int right = arr.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static void runDemo() {
        int[] arr = {1, 3, 3, 7};
        System.out.println("BinarySearch indexOf 3: " + indexOf(arr, 3));
        System.out.println("BinarySearch lowerBound 3: " + lowerBound(arr, 3));
        System.out.println("BinarySearch upperBound 3: " + upperBound(arr, 3));
    }
}
