package studycs.javalab;

import java.util.Arrays;
import java.util.List;

import studycs.javalab.algorithms.arrayhash.LongestSubstringWithoutRepeating;
import studycs.javalab.algorithms.arrayhash.TwoSum;
import studycs.javalab.algorithms.graph.DijkstraShortestPath;
import studycs.javalab.algorithms.graph.GridShortestPathBfs;
import studycs.javalab.algorithms.graph.TopologicalSort;
import studycs.javalab.algorithms.heap.MeetingRooms;
import studycs.javalab.algorithms.heap.TopKElements;
import studycs.javalab.algorithms.stackqueue.NextGreaterElement;
import studycs.javalab.algorithms.stackqueue.SlidingWindowMaximum;

public final class InterviewAlgorithms {
    private InterviewAlgorithms() {
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Interview algorithm demo ==");
        System.out.println("twoSum [2, 7, 11, 15], target 13 -> " + Arrays.toString(TwoSum.findIndices(
            new int[] {2, 7, 11, 15}, 13
        )));
        TwoSum.trace(new int[] {2, 7, 11, 15}, 13);

        System.out.println("longest substring without duplicate in abcabcbb -> " + LongestSubstringWithoutRepeating.lengthOf("abcabcbb"));
        LongestSubstringWithoutRepeating.trace("abcabcbb");

        System.out.println("next greater [4, 5, 2, 25] -> " + Arrays.toString(NextGreaterElement.find(
            new int[] {4, 5, 2, 25}
        )));
        NextGreaterElement.trace(new int[] {4, 5, 2, 25});

        System.out.println("sliding window max -> " + Arrays.toString(SlidingWindowMaximum.find(
            new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3
        )));
        System.out.println("meeting rooms -> " + MeetingRooms.minimumRequired(new int[][] {
            {0, 30}, {5, 10}, {15, 20}
        }));
        System.out.println("top 2 -> " + TopKElements.find(new int[] {3, 1, 5, 2, 4}, 2));
        System.out.println("BFS shortest path -> " + GridShortestPathBfs.shortestPath(new int[][] {
            {0, 0, 0},
            {1, 1, 0},
            {0, 0, 0}
        }));
        GridShortestPathBfs.trace(new int[][] {
            {0, 0, 0},
            {1, 1, 0},
            {0, 0, 0}
        });

        System.out.println("topological sort -> " + TopologicalSort.sort(4, new int[][] {
            {0, 1}, {1, 2}, {2, 3}
        }));

        List<List<DijkstraShortestPath.Edge>> graph = List.of(
            List.of(new DijkstraShortestPath.Edge(1, 4), new DijkstraShortestPath.Edge(2, 1)),
            List.of(new DijkstraShortestPath.Edge(3, 1)),
            List.of(new DijkstraShortestPath.Edge(1, 2), new DijkstraShortestPath.Edge(3, 5)),
            List.of()
        );
        System.out.println("Dijkstra from 0 -> " + Arrays.toString(DijkstraShortestPath.find(graph, 0)));
    }
}
