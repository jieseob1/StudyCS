package studycs.javalab.structures.tree;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree {
    private Node root;

    public void add(int value) {
        root = add(root, value);
    }

    public boolean contains(int value) {
        Node current = root;
        while (current != null) {
            if (value == current.value) {
                return true;
            }
            current = value < current.value ? current.left : current.right;
        }
        return false;
    }

    public List<Integer> inorder() {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    public int height() {
        return height(root);
    }

    public static void runDemo() {
        BinarySearchTree tree = new BinarySearchTree();
        for (int value : new int[] {5, 2, 8, 1, 3}) {
            tree.add(value);
        }
        System.out.println("BinarySearchTree inorder sorted values: " + tree.inorder());
        System.out.println("BinarySearchTree contains 3: " + tree.contains(3));
        System.out.println("BinarySearchTree height: " + tree.height());
    }

    private Node add(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }
        if (value < node.value) {
            node.left = add(node.left, value);
        } else if (value > node.value) {
            node.right = add(node.right, value);
        }
        return node;
    }

    private void inorder(Node node, List<Integer> result) {
        if (node == null) {
            return;
        }
        inorder(node.left, result);
        result.add(node.value);
        inorder(node.right, result);
    }

    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    private static class Node {
        private final int value;
        private Node left;
        private Node right;

        private Node(int value) {
            this.value = value;
        }
    }
}
