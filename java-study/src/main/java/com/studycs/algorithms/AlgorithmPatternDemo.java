package com.studycs.algorithms;

import com.studycs.algorithms.PatternExamples.Activity;
import com.studycs.algorithms.PatternExamples.DijkstraResult;
import com.studycs.algorithms.PatternExamples.IntTraceResult;
import com.studycs.algorithms.PatternExamples.PairResult;
import com.studycs.algorithms.PatternExamples.SearchResult;

import java.util.List;
import java.util.Map;

public final class AlgorithmPatternDemo {
    private AlgorithmPatternDemo() {
    }

    public static void main(String[] args) {
        PairResult pair = PatternExamples.twoPointersSortedPair(new int[]{1, 2, 4, 7, 11}, 9);
        print("Two pointers", "values=" + pair.values() + ", trace=" + pair.trace());

        IntTraceResult window = PatternExamples.slidingWindowMaxSum(new int[]{2, 1, 5, 1, 3, 2}, 3);
        print("Sliding window", "maxSum=" + window.value() + ", trace=" + window.trace());

        IntTraceResult prefix = PatternExamples.prefixSumRangeQuery(new int[]{3, 1, 4, 1, 5}, 1, 3);
        print("Prefix sum", "rangeSum=" + prefix.value() + ", trace=" + prefix.trace());

        SearchResult lowerBound = PatternExamples.lowerBound(new int[]{1, 3, 3, 5, 8}, 3);
        print("Binary search lower bound", "index=" + lowerBound.index() + ", trace=" + lowerBound.trace());

        IntTraceResult bfs = PatternExamples.bfsShortestPath(
                List.of(List.of(1, 2), List.of(0, 3), List.of(0, 3), List.of(1, 2, 4), List.of(3)),
                0,
                4
        );
        print("BFS shortest path", "distance=" + bfs.value() + ", trace=" + bfs.trace());

        print(
                "DFS connected components",
                PatternExamples.dfsConnectedComponents(6, new int[][]{{0, 1}, {1, 2}, {3, 4}}).toString()
        );

        print("Backtracking subsets", PatternExamples.subsets(new int[]{1, 2}).toString());

        IntTraceResult coinChange = PatternExamples.coinChangeMinCoins(new int[]{1, 3, 4}, 6);
        print("Dynamic programming coin change", "minCoins=" + coinChange.value() + ", trace=" + coinChange.trace());

        print(
                "Greedy activity selection",
                PatternExamples.selectActivities(List.of(
                        new Activity("A", 1, 3),
                        new Activity("B", 2, 4),
                        new Activity("C", 3, 5),
                        new Activity("D", 6, 9)
                )).toString()
        );

        print("Heap top K", PatternExamples.topKLargest(new int[]{5, 1, 9, 3, 7}, 3).toString());

        print(
                "Union-Find",
                PatternExamples.unionFindConnected(5, new int[][]{{0, 1}, {1, 2}, {3, 4}}, 0, 2).toString()
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
        print("Dijkstra", "distance=" + dijkstra.distance() + ", path=" + dijkstra.path() + ", trace=" + dijkstra.trace());
    }

    private static void print(String title, String body) {
        System.out.println("[" + title + "]");
        System.out.println(body);
        System.out.println();
    }
}
