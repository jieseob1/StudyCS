package com.studycs.datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ToyHashMap<K, V> {
    private static final double LOAD_FACTOR = 0.75;

    private Node<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public ToyHashMap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be positive");
        }
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity *= 2;
        }
        this.table = (Node<K, V>[]) new Node[capacity];
    }

    public V put(K key, V value) {
        if (size + 1 > threshold()) {
            resize();
        }

        int index = indexFor(key, table.length);
        Node<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }

        table[index] = new Node<>(key, value, table[index]);
        size++;
        return null;
    }

    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    public List<String> bucketSnapshot() {
        List<String> buckets = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            List<String> entries = new ArrayList<>();
            Node<K, V> current = table[i];
            while (current != null) {
                entries.add(current.key + "=" + current.value);
                current = current.next;
            }
            if (!entries.isEmpty()) {
                buckets.add("bucket[" + i + "]" + entries);
            }
        }
        return buckets;
    }

    private Node<K, V> findNode(K key) {
        int index = indexFor(key, table.length);
        Node<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private int threshold() {
        return Math.max(1, (int) (table.length * LOAD_FACTOR));
    }

    private int indexFor(K key, int length) {
        int hash = key == null ? 0 : key.hashCode();
        return (hash & 0x7fffffff) % length;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[oldTable.length * 2];
        size = 0;

        for (Node<K, V> bucket : oldTable) {
            Node<K, V> current = bucket;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    private static final class Node<K, V> {
        private final K key;
        private V value;
        private final Node<K, V> next;

        private Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
