package studycs.javalab;

import studycs.javalab.algorithms.arrayhash.LongestSubstringWithoutRepeating;
import studycs.javalab.algorithms.arrayhash.PrefixSum;
import studycs.javalab.algorithms.arrayhash.TwoSum;
import studycs.javalab.algorithms.backtracking.CombinationSum;
import studycs.javalab.algorithms.backtracking.Permutations;
import studycs.javalab.algorithms.dp.CoinChange;
import studycs.javalab.algorithms.dp.LongestIncreasingSubsequence;
import studycs.javalab.algorithms.graph.ConnectedComponentsDfs;
import studycs.javalab.algorithms.graph.DijkstraShortestPath;
import studycs.javalab.algorithms.graph.FloydWarshall;
import studycs.javalab.algorithms.graph.GridShortestPathBfs;
import studycs.javalab.algorithms.graph.KruskalMinimumSpanningTree;
import studycs.javalab.algorithms.graph.TopologicalSort;
import studycs.javalab.algorithms.greedy.ActivitySelection;
import studycs.javalab.algorithms.heap.MeetingRooms;
import studycs.javalab.algorithms.heap.TopKElements;
import studycs.javalab.algorithms.math.GcdLcm;
import studycs.javalab.algorithms.math.SieveOfEratosthenes;
import studycs.javalab.algorithms.search.BinarySearch;
import studycs.javalab.algorithms.sorting.CountingSort;
import studycs.javalab.algorithms.sorting.MergeSort;
import studycs.javalab.algorithms.sorting.QuickSort;
import studycs.javalab.algorithms.stackqueue.NextGreaterElement;
import studycs.javalab.algorithms.stackqueue.SlidingWindowMaximum;
import studycs.javalab.algorithms.string.KmpSearch;
import studycs.javalab.examples.map.ComputeExample;
import studycs.javalab.examples.map.ComputeIfAbsentExample;
import studycs.javalab.examples.map.MergeExample;
import studycs.javalab.examples.map.PutIfAbsentExample;
import studycs.javalab.examples.standard.ArraysUtilityExample;
import studycs.javalab.examples.standard.Base64UuidExample;
import studycs.javalab.examples.standard.BigDecimalExample;
import studycs.javalab.examples.standard.CollectionsUtilityExample;
import studycs.javalab.examples.standard.ComparatorExample;
import studycs.javalab.examples.standard.DateTimeExample;
import studycs.javalab.examples.standard.EnumUtilitiesExample;
import studycs.javalab.examples.standard.ObjectsUtilityExample;
import studycs.javalab.examples.standard.OptionalExample;
import studycs.javalab.examples.standard.PathUtilityExample;
import studycs.javalab.examples.standard.RegexExample;
import studycs.javalab.examples.standard.StringUtilitiesExample;
import studycs.javalab.examples.stream.FilterMapExample;
import studycs.javalab.examples.stream.GroupingExample;
import studycs.javalab.examples.stream.PrimitiveStreamExample;
import studycs.javalab.examples.stream.User;
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

import java.util.List;
import java.util.Map;

public class StudyCsLabTest {
    public static void main(String[] args) {
        testMapApiBehavior();
        testStreamExamples();
        testStandardUtilityExamples();
        testInterviewAlgorithms();
        testExamAlgorithms();
        testCustomStructures();
        System.out.println("All StudyCS Java lab tests passed.");
    }

    private static void testMapApiBehavior() {
        assertEquals(2, PutIfAbsentExample.eagerCreationCount(),
            "putIfAbsent should evaluate value argument before the call");
        assertEquals(1, ComputeIfAbsentExample.lazyCreationCount(),
            "computeIfAbsent should call mapping function only when value is absent");
        assertEquals(Map.of("java", 3, "stream", 1), MergeExample.countWords(List.of(
            "java", "stream", "java", "java"
        )), "merge should count words");
        assertFalse(ComputeExample.computeReturningNullKeepsKey(),
            "compute returning null should remove the mapping");
    }

    private static void testStreamExamples() {
        List<User> users = List.of(
            new User(1, "kim", "backend", true, 80),
            new User(2, "lee", "backend", false, 90),
            new User(3, "park", "frontend", true, 70)
        );

        assertEquals(List.of("KIM", "PARK"), FilterMapExample.activeUppercaseNames(users),
            "filter + map should transform only active users");
        assertEquals(Map.of("backend", 2L, "frontend", 1L), GroupingExample.countByTeam(users),
            "groupingBy + counting should count users per team");
        assertEquals(240, PrimitiveStreamExample.totalScore(users),
            "primitive stream should sum scores");
    }

    private static void testStandardUtilityExamples() {
        assertEquals("java utility api", StringUtilitiesExample.normalizeWords("  Java   Utility API  "),
            "String utilities should trim, normalize spaces, and lower case");
        assertEquals("java,stream,map", StringUtilitiesExample.joinWithComma(List.of("java", "stream", "map")),
            "String.join should combine values with delimiter");

        assertEquals("unknown", ObjectsUtilityExample.defaultName(null),
            "Objects.requireNonNullElse should supply default value");
        assertTrue(ObjectsUtilityExample.safeEquals("java", new String("java")),
            "Objects.equals should handle equality safely");

        assertEquals("KIM", OptionalExample.uppercaseNicknameOrDefault(new OptionalExample.Profile("kim")),
            "Optional should transform present values");
        assertEquals("ANONYMOUS", OptionalExample.uppercaseNicknameOrDefault(null),
            "Optional should provide default for missing profile");

        assertArrayEquals(new int[] {1, 2, 3, 5}, ArraysUtilityExample.sortedCopy(new int[] {5, 1, 3, 2}),
            "Arrays utility should copy and sort without mutating original");
        assertEquals(2, ArraysUtilityExample.binarySearch(new int[] {1, 3, 5, 7}, 5),
            "Arrays.binarySearch should find sorted index");

        assertEquals(List.of("c", "b", "a"), CollectionsUtilityExample.reversedCopy(List.of("a", "b", "c")),
            "Collections.reverse should reverse a mutable copy");
        assertEquals(2, CollectionsUtilityExample.frequency(List.of("java", "api", "java"), "java"),
            "Collections.frequency should count equal values");

        assertEquals(List.of("lee:90", "kim:80", "park:80"), ComparatorExample.sortByScoreDescThenName(List.of(
            new ComparatorExample.Score("park", 80),
            new ComparatorExample.Score("lee", 90),
            new ComparatorExample.Score("kim", 80)
        )), "Comparator chaining should sort by score descending then name ascending");

        assertEquals(9L, DateTimeExample.daysBetween("2026-06-01", "2026-06-10"),
            "java.time should calculate date distance");
        assertEquals("2026-06", DateTimeExample.yearMonth("2026-06-09"),
            "java.time should format year-month");

        assertEquals(List.of("dev@test.com", "admin@test.com"), RegexExample.extractEmails(
            "mail dev@test.com and admin@test.com"
        ), "Pattern and Matcher should extract repeated matches");

        assertEquals("0.30", BigDecimalExample.addMoney("0.10", "0.20"),
            "BigDecimal should avoid floating point money errors");
        assertEquals("3.33", BigDecimalExample.divideHalfUp("10", "3"),
            "BigDecimal should divide with explicit scale and rounding");

        assertTrue(EnumUtilitiesExample.hasReadPermission(), "EnumSet should represent permission flags");
        assertEquals("running", EnumUtilitiesExample.statusMessage(EnumUtilitiesExample.Status.ACTIVE),
            "EnumMap should map enum keys efficiently");

        assertEquals("amF2YQ==", Base64UuidExample.encodeBase64("java"),
            "Base64 should encode UTF-8 text");
        assertEquals("java", Base64UuidExample.decodeBase64("amF2YQ=="),
            "Base64 should decode UTF-8 text");

        assertEquals("config/app.yml", PathUtilityExample.normalized("logs/../config/app.yml"),
            "Path.normalize should remove redundant path segments");
    }

    private static void testInterviewAlgorithms() {
        assertArrayEquals(new int[] {0, 2}, TwoSum.findIndices(
            new int[] {2, 7, 11, 15}, 13
        ), "twoSum should find two indices");

        assertEquals(3, LongestSubstringWithoutRepeating.lengthOf("abcabcbb"),
            "sliding window should find longest substring without duplicates");

        assertArrayEquals(new int[] {5, 25, 25, -1}, NextGreaterElement.find(
            new int[] {4, 5, 2, 25}
        ), "monotonic stack should find next greater values");

        assertArrayEquals(new int[] {3, 3, 5, 5, 6, 7}, SlidingWindowMaximum.find(
            new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3
        ), "monotonic deque should find sliding window maximums");

        assertEquals(2, MeetingRooms.minimumRequired(new int[][] {
            {0, 30}, {5, 10}, {15, 20}
        }), "priority queue should count meeting rooms");

        assertEquals(List.of(5, 4), TopKElements.find(new int[] {3, 1, 5, 2, 4}, 2),
            "heap should return top k values descending");

        assertEquals(4, GridShortestPathBfs.shortestPath(new int[][] {
            {0, 0, 0},
            {1, 1, 0},
            {0, 0, 0}
        }), "BFS should find unweighted shortest path");

        assertEquals(List.of(0, 1, 2, 3), TopologicalSort.sort(4, new int[][] {
            {0, 1}, {1, 2}, {2, 3}
        }), "topological sort should respect dependencies");

        List<List<DijkstraShortestPath.Edge>> graph = List.of(
            List.of(new DijkstraShortestPath.Edge(1, 4), new DijkstraShortestPath.Edge(2, 1)),
            List.of(new DijkstraShortestPath.Edge(3, 1)),
            List.of(new DijkstraShortestPath.Edge(1, 2), new DijkstraShortestPath.Edge(3, 5)),
            List.of()
        );
        assertArrayEquals(new int[] {0, 3, 1, 4}, DijkstraShortestPath.find(graph, 0),
            "Dijkstra should find shortest positive weighted paths");
    }

    private static void testExamAlgorithms() {
        assertArrayEquals(new int[] {1, 2, 3, 4, 5}, MergeSort.sort(new int[] {5, 1, 4, 2, 3}),
            "Merge sort should sort by divide and conquer");
        assertArrayEquals(new int[] {1, 2, 3, 4, 5}, QuickSort.sort(new int[] {5, 1, 4, 2, 3}),
            "Quick sort should sort in-place around pivot partitions");
        assertArrayEquals(new int[] {0, 1, 2, 2, 3, 3}, CountingSort.sort(new int[] {3, 1, 2, 3, 0, 2}, 3),
            "Counting sort should sort small integer ranges");

        assertEquals(2, BinarySearch.indexOf(new int[] {1, 3, 5, 7}, 5),
            "Binary search should find exact index");
        assertEquals(1, BinarySearch.lowerBound(new int[] {1, 3, 3, 7}, 3),
            "Lower bound should find first index greater than or equal to target");
        assertEquals(3, BinarySearch.upperBound(new int[] {1, 3, 3, 7}, 3),
            "Upper bound should find first index greater than target");

        PrefixSum prefixSum = PrefixSum.from(new int[] {2, 4, 6, 8});
        assertEquals(18, prefixSum.rangeSum(1, 3), "Prefix sum should answer range sum in O(1)");

        assertEquals(List.of(
            List.of(1, 2, 3),
            List.of(1, 3, 2),
            List.of(2, 1, 3),
            List.of(2, 3, 1),
            List.of(3, 1, 2),
            List.of(3, 2, 1)
        ), Permutations.of(new int[] {1, 2, 3}), "Backtracking should generate permutations");

        assertEquals(List.of(List.of(2, 2, 3), List.of(7)), CombinationSum.find(new int[] {2, 3, 6, 7}, 7),
            "Combination sum should reuse candidates and reach target");

        assertEquals(3, CoinChange.minimumCoins(new int[] {1, 2, 5}, 11),
            "Coin change DP should find minimum coin count");
        assertEquals(4, LongestIncreasingSubsequence.lengthOf(new int[] {10, 9, 2, 5, 3, 7, 101, 18}),
            "LIS should use binary-search DP tails");

        assertEquals(2, ActivitySelection.maximumNonOverlapping(new int[][] {{1, 3}, {2, 4}, {3, 5}}),
            "Greedy activity selection should pick earliest ending intervals");

        assertEquals(List.of(2, 3, 5, 7, 11, 13), SieveOfEratosthenes.primesUpTo(13),
            "Sieve should list primes up to n");
        assertEquals(6, GcdLcm.gcd(54, 24), "Euclidean algorithm should compute gcd");
        assertEquals(216, GcdLcm.lcm(54, 24), "LCM should use gcd");

        assertEquals(List.of(0, 5), KmpSearch.search("ababcababc", "ababc"),
            "KMP should find all pattern matches");

        assertEquals(2, ConnectedComponentsDfs.count(5, new int[][] {{0, 1}, {1, 2}, {3, 4}}),
            "DFS should count connected components");
        assertEquals(6, KruskalMinimumSpanningTree.weight(4, new int[][] {
            {0, 1, 1}, {1, 2, 2}, {2, 3, 3}, {0, 3, 10}
        }), "Kruskal should build minimum spanning tree");

        int inf = FloydWarshall.INF;
        int[][] shortest = FloydWarshall.shortestPaths(new int[][] {
            {0, 3, inf},
            {inf, 0, 4},
            {2, inf, 0}
        });
        assertEquals(7, shortest[0][2], "Floyd-Warshall should relax through intermediate nodes");
        assertEquals(5, shortest[2][1], "Floyd-Warshall should find all-pairs shortest paths");
    }

    private static void testCustomStructures() {
        SinglyLinkedList list = new SinglyLinkedList();
        list.addLast(10);
        list.addLast(20);
        list.addFirst(5);
        assertEquals(List.of(5, 10, 20), list.toList(), "Singly linked list should preserve pointer order");
        assertEquals(5, list.removeFirst(), "Singly linked list should remove head in O(1)");
        assertTrue(list.contains(20), "Singly linked list should scan for existing value");

        BinaryMinHeap heap = new BinaryMinHeap();
        heap.offer(5);
        heap.offer(1);
        heap.offer(3);
        assertEquals(1, heap.poll(), "Binary min heap should poll smallest value first");
        assertEquals(3, heap.poll(), "Binary min heap should restore heap order after poll");

        BinarySearchTree tree = new BinarySearchTree();
        for (int value : new int[] {5, 2, 8, 1, 3}) {
            tree.add(value);
        }
        assertEquals(List.of(1, 2, 3, 5, 8), tree.inorder(), "BST inorder traversal should be sorted");
        assertTrue(tree.contains(3), "BST should find inserted value");

        AdjacencyListGraph graph = new AdjacencyListGraph(5);
        graph.addUndirectedEdge(0, 1);
        graph.addUndirectedEdge(0, 2);
        graph.addUndirectedEdge(1, 3);
        graph.addUndirectedEdge(2, 4);
        assertEquals(List.of(0, 1, 2, 3, 4), graph.bfsOrder(0), "Adjacency graph BFS should visit by layers");
        assertEquals(List.of(0, 1, 3, 2, 4), graph.dfsOrder(0), "Adjacency graph DFS should go deep first");

        Trie trie = new Trie();
        trie.insert("java");
        trie.insert("javascript");
        assertTrue(trie.search("java"), "Trie should find inserted word");
        assertFalse(trie.search("jav"), "Trie should distinguish prefix from word");
        assertTrue(trie.startsWith("java"), "Trie should find prefix");

        DisjointSet set = new DisjointSet(5);
        assertTrue(set.union(0, 1), "Union should merge different groups");
        assertTrue(set.union(1, 2), "Union should merge through root");
        assertFalse(set.union(0, 2), "Union should report already connected nodes");
        assertTrue(set.connected(0, 2), "Connected should use compressed roots");

        FenwickTree fenwick = new FenwickTree(5);
        fenwick.add(0, 1);
        fenwick.add(1, 2);
        fenwick.add(2, 3);
        assertEquals(6L, fenwick.rangeSum(0, 2), "Fenwick tree should answer range sum");
        fenwick.add(1, 5);
        assertEquals(11L, fenwick.rangeSum(0, 2), "Fenwick tree should handle point update");

        SegmentTree segmentTree = new SegmentTree(new int[] {1, 2, 3, 4});
        assertEquals(9L, segmentTree.query(1, 3), "Segment tree should answer range sum");
        segmentTree.update(2, 10);
        assertEquals(16L, segmentTree.query(1, 3), "Segment tree should handle point update");

        LruCache<Integer, String> cache = new LruCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.get(1);
        cache.put(3, "three");
        assertTrue(cache.containsKey(1), "LRU should keep recently accessed key");
        assertFalse(cache.containsKey(2), "LRU should evict least recently used key");
        assertTrue(cache.containsKey(3), "LRU should keep inserted key");

        assertEquals(List.of(1, 3, 10), BitSetExample.selectedIndexes(1, 3, 10),
            "BitSet should store sparse boolean indexes compactly");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual, String message) {
        if (expected.length != actual.length) {
            throw new AssertionError(message + " length expected=" + expected.length + " actual=" + actual.length);
        }

        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                throw new AssertionError(message + " at index=" + i + " expected=" + expected[i] + " actual=" + actual[i]);
            }
        }
    }
}
