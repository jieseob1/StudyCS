package studycs.javalab.collections;

import java.util.ArrayDeque;

public final class DequeExample {
    private DequeExample() {
    }

    public static void runDemo() {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        System.out.println("ArrayDeque as stack pop: " + stack.pop());

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.offer(1);
        queue.offer(2);
        System.out.println("ArrayDeque as queue poll: " + queue.poll());
    }
}
