package com.studycs;

import com.studycs.algorithms.PatternExamples;
import com.studycs.algorithms.PatternExamples.Activity;
import com.studycs.algorithms.PatternExamples.DijkstraResult;
import com.studycs.algorithms.PatternExamples.IntTraceResult;
import com.studycs.algorithms.PatternExamples.PairResult;
import com.studycs.algorithms.PatternExamples.SearchResult;
import com.studycs.datastructures.HashStructureLab;
import com.studycs.datastructures.MapOperationLab;
import com.studycs.datastructures.ToyHashMap;

import java.util.List;
import java.util.Map;

public final class JavaStudyAssertions {
    private JavaStudyAssertions() {
    }

    public static void main(String[] args) {
        assertEquals(
                "computeIfAbsent calls loader only for absent or null-valued keys",
                "calls=[load:new-key, load:null-key], map={existing=ready, null-key=created-for-null-key, new-key=created-for-new-key}",
                MapOperationLab.computeIfAbsentTrace()
        );

        assertEquals(
                "putIfAbsent writes only when key is absent or mapped to null",
                "returns=[null, ready, null], map={existing=ready, null-key=now-filled, new-key=created}",
                MapOperationLab.putIfAbsentTrace()
        );

        assertEquals(
                "compute and merge expose different update rules",
                "calls=[compute:count=1, compute:remove-me=9, merge-present:2+3], map={count=5, missing=7}",
                MapOperationLab.computeAndMergeTrace()
        );

        ToyHashMap<HashStructureLab.BadHashKey, String> toyMap = new ToyHashMap<>(2);
        toyMap.put(new HashStructureLab.BadHashKey("A"), "alpha");
        toyMap.put(new HashStructureLab.BadHashKey("B"), "bravo");
        toyMap.put(new HashStructureLab.BadHashKey("C"), "charlie");
        assertEquals("toy map keeps all colliding keys", 3, toyMap.size());
        assertEquals("toy map retrieves a colliding key by equals", "bravo", toyMap.get(new HashStructureLab.BadHashKey("B")));
        assertTrue("toy map resized after load factor threshold", toyMap.capacity() >= 4);

        assertEquals(
                "HashSet uses hashCode bucket lookup then equals for duplicates",
                "added=[true, false, true], size=2, containsSameId=true",
                HashStructureLab.hashSetDuplicateTrace()
        );

        PairResult pair = PatternExamples.twoPointersSortedPair(new int[]{1, 2, 4, 7, 11}, 9);
        assertEquals("two pointers finds sorted pair", List.of(2, 7), pair.values());

        IntTraceResult window = PatternExamples.slidingWindowMaxSum(new int[]{2, 1, 5, 1, 3, 2}, 3);
        assertEquals("sliding window max sum", 9, window.value());

        IntTraceResult prefix = PatternExamples.prefixSumRangeQuery(new int[]{3, 1, 4, 1, 5}, 1, 3);
        assertEquals("prefix sum range query is inclusive", 6, prefix.value());

        SearchResult lowerBound = PatternExamples.lowerBound(new int[]{1, 3, 3, 5, 8}, 3);
        assertEquals("lower bound returns first index >= target", 1, lowerBound.index());

        IntTraceResult bfs = PatternExamples.bfsShortestPath(
                List.of(List.of(1, 2), List.of(0, 3), List.of(0, 3), List.of(1, 2, 4), List.of(3)),
                0,
                4
        );
        assertEquals("bfs shortest path distance", 3, bfs.value());

        assertEquals(
                "dfs connected components",
                List.of(List.of(0, 1, 2), List.of(3, 4), List.of(5)),
                PatternExamples.dfsConnectedComponents(6, new int[][]{{0, 1}, {1, 2}, {3, 4}})
        );

        assertEquals(
                "backtracking subsets",
                List.of(List.of(), List.of(1), List.of(1, 2), List.of(2)),
                PatternExamples.subsets(new int[]{1, 2})
        );

        IntTraceResult coinChange = PatternExamples.coinChangeMinCoins(new int[]{1, 3, 4}, 6);
        assertEquals("dp coin change min coins", 2, coinChange.value());

        assertEquals(
                "greedy activity selection",
                List.of(new Activity("A", 1, 3), new Activity("C", 3, 5), new Activity("D", 6, 9)),
                PatternExamples.selectActivities(List.of(
                        new Activity("A", 1, 3),
                        new Activity("B", 2, 4),
                        new Activity("C", 3, 5),
                        new Activity("D", 6, 9)
                ))
        );

        assertEquals("heap top k largest", List.of(9, 7, 5), PatternExamples.topKLargest(new int[]{5, 1, 9, 3, 7}, 3));

        assertTrue(
                "union-find detects connectivity after unions",
                PatternExamples.unionFindConnected(5, new int[][]{{0, 1}, {1, 2}, {3, 4}}, 0, 2).connected()
        );

        DijkstraResult dijkstra = PatternExamples.dijkstraShortestPath(
                Map.of(
                        "A", List.of(new PatternExamples.Edge("B", 4), new PatternExamples.Edge("C", 2)),
                        "B", List.of(new PatternExamples.Edge("D", 5)),
                        "C", List.of(new PatternExamples.Edge("B", 1), new PatternExamples.Edge("D", 8)),
                        "D", List.of()
                ),
                "A",
                "D"
        );
        assertEquals("dijkstra shortest distance", 8, dijkstra.distance());
        assertEquals("dijkstra shortest path", List.of("A", "C", "B", "D"), dijkstra.path());

        System.out.println("All java-study assertions passed.");
    }

    private static void assertEquals(String label, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + "\nexpected: " + expected + "\nactual:   " + actual);
        }
    }

    private static void assertTrue(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }
}
