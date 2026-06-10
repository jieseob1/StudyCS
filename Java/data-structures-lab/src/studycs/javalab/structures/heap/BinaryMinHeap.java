package studycs.javalab.structures.heap;

import java.util.ArrayList;
import java.util.List;

public class BinaryMinHeap {
    private final List<Integer> heap = new ArrayList<>();

    public void offer(int value) {
        heap.add(value);
        siftUp(heap.size() - 1);
    }

    public int peek() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("heap is empty");
        }
        return heap.get(0);
    }

    public int poll() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("heap is empty");
        }

        int min = heap.get(0);
        int last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return min;
    }

    public int size() {
        return heap.size();
    }

    public List<Integer> snapshot() {
        return List.copyOf(heap);
    }

    public static void runDemo() {
        BinaryMinHeap heap = new BinaryMinHeap();
        heap.offer(5);
        heap.offer(1);
        heap.offer(3);
        System.out.println("BinaryMinHeap internal array after offers: " + heap.snapshot());
        System.out.println("BinaryMinHeap poll order: " + heap.poll() + ", " + heap.poll() + ", " + heap.poll());
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(parent) <= heap.get(index)) {
                return;
            }
            swap(parent, index);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = index * 2 + 2;
            int smallest = index;

            if (left < heap.size() && heap.get(left) < heap.get(smallest)) {
                smallest = left;
            }
            if (right < heap.size() && heap.get(right) < heap.get(smallest)) {
                smallest = right;
            }
            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int left, int right) {
        int temp = heap.get(left);
        heap.set(left, heap.get(right));
        heap.set(right, temp);
    }
}
