package studycs.javalab;

import studycs.javalab.structures.bit.BitSetExample;
import studycs.javalab.structures.cache.LruCache;
import studycs.javalab.structures.graph.AdjacencyListGraph;
import studycs.javalab.structures.heap.BinaryMinHeap;
import studycs.javalab.structures.linear.SinglyLinkedList;
import studycs.javalab.structures.range.FenwickTree;
import studycs.javalab.structures.range.SegmentTree;
import studycs.javalab.structures.tree.BinarySearchTree;
import studycs.javalab.structures.trie.Trie;
import studycs.javalab.structures.unionfind.DisjointSet;

public final class StructureExamples {
    private StructureExamples() {
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Custom structure demo ==");

        SinglyLinkedList.runDemo();
        BinaryMinHeap.runDemo();
        BinarySearchTree.runDemo();
        AdjacencyListGraph.runDemo();
        Trie.runDemo();
        DisjointSet.runDemo();
        FenwickTree.runDemo();
        SegmentTree.runDemo();
        LruCache.runDemo();
        BitSetExample.runDemo();
    }
}
