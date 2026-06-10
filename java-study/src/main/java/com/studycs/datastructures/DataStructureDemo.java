package com.studycs.datastructures;

import java.util.ArrayDeque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

public final class DataStructureDemo {
    private DataStructureDemo() {
    }

    public static void main(String[] args) {
        System.out.println("[Map operations]");
        System.out.println("computeIfAbsent: " + MapOperationLab.computeIfAbsentTrace());
        System.out.println("putIfAbsent:     " + MapOperationLab.putIfAbsentTrace());
        System.out.println("compute/merge:   " + MapOperationLab.computeAndMergeTrace());

        System.out.println();
        System.out.println("[Hash buckets]");
        System.out.println("HashSet duplicate: " + HashStructureLab.hashSetDuplicateTrace());
        System.out.println("ToyHashMap collision/resize: " + HashStructureLab.toyHashMapCollisionTrace());

        System.out.println();
        System.out.println("[Ordering and queue structures]");
        System.out.println("TreeSet sorted unique values: " + treeSetTrace(List.of(4, 1, 4, 2, 3)));
        System.out.println("PriorityQueue min-heap poll order: " + priorityQueueTrace(List.of(7, 2, 9, 1)));
        System.out.println("ArrayDeque stack/queue behavior: " + arrayDequeTrace());
    }

    private static String treeSetTrace(List<Integer> values) {
        TreeSet<Integer> set = new TreeSet<>(values);
        return set.toString();
    }

    private static String priorityQueueTrace(List<Integer> values) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(values);
        List<Integer> polled = new java.util.ArrayList<>();
        while (!queue.isEmpty()) {
            polled.add(queue.poll());
        }
        return polled.toString();
    }

    private static String arrayDequeTrace() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("queue-first");
        deque.addLast("queue-second");
        String queuePoll = deque.removeFirst();

        deque.addFirst("stack-first");
        deque.addFirst("stack-second");
        String stackPop = deque.removeFirst();

        return "queuePoll=" + queuePoll + ", stackPop=" + stackPop + ", remaining=" + deque;
    }
}
