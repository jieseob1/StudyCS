package studycs.javalab.structures.trie;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private final Node root = new Node();

    public void insert(String word) {
        Node current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new Node());
            current = current.children.get(c);
        }
        current.word = true;
    }

    public boolean search(String word) {
        Node node = find(word);
        return node != null && node.word;
    }

    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    public static void runDemo() {
        Trie trie = new Trie();
        trie.insert("java");
        trie.insert("javascript");
        System.out.println("Trie search java: " + trie.search("java"));
        System.out.println("Trie search jav: " + trie.search("jav"));
        System.out.println("Trie startsWith jav: " + trie.startsWith("jav"));
    }

    private Node find(String text) {
        Node current = root;
        for (char c : text.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private static class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private boolean word;
    }
}
