package studycs.javalab.structures.linear;

import java.util.ArrayList;
import java.util.List;

public class SinglyLinkedList {
    private Node head;
    private Node tail;
    private int size;

    public void addFirst(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
    }

    public void addLast(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public int removeFirst() {
        if (head == null) {
            throw new IllegalStateException("list is empty");
        }

        int value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    public boolean contains(int target) {
        for (Node current = head; current != null; current = current.next) {
            if (current.value == target) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public List<Integer> toList() {
        List<Integer> result = new ArrayList<>();
        for (Node current = head; current != null; current = current.next) {
            result.add(current.value);
        }
        return result;
    }

    public static void runDemo() {
        SinglyLinkedList list = new SinglyLinkedList();
        list.addLast(10);
        list.addLast(20);
        list.addFirst(5);
        System.out.println("SinglyLinkedList after addFirst/addLast: " + list.toList());
        System.out.println("SinglyLinkedList removeFirst: " + list.removeFirst());
        System.out.println("SinglyLinkedList contains 20: " + list.contains(20));
    }

    private static class Node {
        private final int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }
}
