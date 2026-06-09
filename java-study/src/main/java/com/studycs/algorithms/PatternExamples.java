package com.studycs.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public final class PatternExamples {
    private PatternExamples() {
    }

    public record PairResult(List<Integer> indices, List<Integer> values, List<String> trace) {
    }

    public record IntTraceResult(int value, List<String> trace) {
    }

    public record SearchResult(int index, List<String> trace) {
    }

    public record Activity(String name, int start, int end) {
    }

    public record UnionFindResult(boolean connected, List<String> trace) {
    }

    public record Edge(String to, int weight) {
    }

    public record DijkstraResult(int distance, List<String> path, List<String> trace) {
    }

    public static PairResult twoPointersSortedPair(int[] sortedNumbers, int target) {
        int left = 0;
        int right = sortedNumbers.length - 1;
        List<String> trace = new ArrayList<>();

        while (left < right) {
            int sum = sortedNumbers[left] + sortedNumbers[right];
            trace.add("left=" + left + "(" + sortedNumbers[left] + "), right=" + right
                    + "(" + sortedNumbers[right] + "), sum=" + sum);

            if (sum == target) {
                return new PairResult(
                        List.of(left, right),
                        List.of(sortedNumbers[left], sortedNumbers[right]),
                        trace
                );
            }
            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new PairResult(List.of(), List.of(), trace);
    }

    public static IntTraceResult slidingWindowMaxSum(int[] numbers, int windowSize) {
        if (windowSize <= 0 || windowSize > numbers.length) {
            throw new IllegalArgumentException("windowSize must be between 1 and numbers.length");
        }

        int windowSum = 0;
        List<String> trace = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            windowSum += numbers[i];
        }

        int best = windowSum;
        trace.add("window[0.." + (windowSize - 1) + "]=" + windowSum);

        for (int right = windowSize; right < numbers.length; right++) {
            int left = right - windowSize;
            windowSum += numbers[right] - numbers[left];
            best = Math.max(best, windowSum);
            trace.add("drop index " + left + ", add index " + right + ", sum=" + windowSum + ", best=" + best);
        }

        return new IntTraceResult(best, trace);
    }

    public static IntTraceResult prefixSumRangeQuery(int[] numbers, int leftInclusive, int rightInclusive) {
        if (leftInclusive < 0 || rightInclusive >= numbers.length || leftInclusive > rightInclusive) {
            throw new IllegalArgumentException("invalid inclusive range");
        }

        int[] prefix = new int[numbers.length + 1];
        List<String> trace = new ArrayList<>();
        trace.add("prefix[0]=0");
        for (int i = 0; i < numbers.length; i++) {
            prefix[i + 1] = prefix[i] + numbers[i];
            trace.add("prefix[" + (i + 1) + "]=" + prefix[i + 1]);
        }

        int sum = prefix[rightInclusive + 1] - prefix[leftInclusive];
        trace.add("rangeSum=" + prefix[rightInclusive + 1] + "-" + prefix[leftInclusive] + "=" + sum);
        return new IntTraceResult(sum, trace);
    }

    public static SearchResult lowerBound(int[] sortedNumbers, int target) {
        int left = 0;
        int right = sortedNumbers.length;
        List<String> trace = new ArrayList<>();

        while (left < right) {
            int mid = left + (right - left) / 2;
            trace.add("left=" + left + ", right=" + right + ", mid=" + mid + ", value=" + sortedNumbers[mid]);
            if (sortedNumbers[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        trace.add("answer=" + left);
        return new SearchResult(left, trace);
    }

    public static IntTraceResult bfsShortestPath(List<List<Integer>> graph, int start, int target) {
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        List<String> trace = new ArrayList<>();

        distance[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            int node = queue.removeFirst();
            trace.add("visit=" + node + ", distance=" + distance[node]);
            if (node == target) {
                return new IntTraceResult(distance[node], trace);
            }

            for (int next : graph.get(node)) {
                if (distance[next] == -1) {
                    distance[next] = distance[node] + 1;
                    queue.addLast(next);
                    trace.add("  push=" + next + ", distance=" + distance[next]);
                }
            }
        }

        return new IntTraceResult(-1, trace);
    }

    public static List<List<Integer>> dfsConnectedComponents(int nodeCount, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < nodeCount; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
        for (List<Integer> neighbors : graph) {
            Collections.sort(neighbors);
        }

        boolean[] visited = new boolean[nodeCount];
        List<List<Integer>> components = new ArrayList<>();
        for (int node = 0; node < nodeCount; node++) {
            if (!visited[node]) {
                List<Integer> component = new ArrayList<>();
                dfs(node, graph, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    public static List<List<Integer>> subsets(int[] numbers) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSubsets(0, numbers, new ArrayList<>(), result);
        return result;
    }

    public static IntTraceResult coinChangeMinCoins(int[] coins, int amount) {
        int impossible = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, impossible);
        dp[0] = 0;

        List<String> trace = new ArrayList<>();
        for (int value = 1; value <= amount; value++) {
            for (int coin : coins) {
                if (value >= coin && dp[value - coin] != impossible) {
                    dp[value] = Math.min(dp[value], dp[value - coin] + 1);
                }
            }
            trace.add("dp[" + value + "]=" + (dp[value] == impossible ? "INF" : dp[value]));
        }

        return new IntTraceResult(dp[amount] == impossible ? -1 : dp[amount], trace);
    }

    public static List<Activity> selectActivities(List<Activity> activities) {
        List<Activity> sorted = new ArrayList<>(activities);
        sorted.sort(Comparator.comparingInt(Activity::end).thenComparingInt(Activity::start));

        List<Activity> selected = new ArrayList<>();
        int currentEnd = Integer.MIN_VALUE;
        for (Activity activity : sorted) {
            if (activity.start() >= currentEnd) {
                selected.add(activity);
                currentEnd = activity.end();
            }
        }
        return selected;
    }

    public static List<Integer> topKLargest(int[] numbers, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative");
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int number : numbers) {
            minHeap.add(number);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<Integer> result = new ArrayList<>(minHeap);
        result.sort(Comparator.reverseOrder());
        return result;
    }

    public static UnionFindResult unionFindConnected(int nodeCount, int[][] unions, int a, int b) {
        UnionFind unionFind = new UnionFind(nodeCount);
        List<String> trace = new ArrayList<>();
        for (int[] union : unions) {
            unionFind.union(union[0], union[1]);
            trace.add("union(" + union[0] + "," + union[1] + ") parents=" + Arrays.toString(unionFind.parents()));
        }
        boolean connected = unionFind.find(a) == unionFind.find(b);
        trace.add("connected(" + a + "," + b + ")=" + connected);
        return new UnionFindResult(connected, trace);
    }

    public static DijkstraResult dijkstraShortestPath(Map<String, List<Edge>> graph, String start, String target) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> settled = new HashSet<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));
        List<String> trace = new ArrayList<>();

        distance.put(start, 0);
        queue.add(new NodeDistance(start, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (!settled.add(current.node())) {
                continue;
            }

            trace.add("settle=" + current.node() + ", distance=" + current.distance());
            if (current.node().equals(target)) {
                break;
            }

            for (Edge edge : graph.getOrDefault(current.node(), List.of())) {
                int nextDistance = current.distance() + edge.weight();
                int oldDistance = distance.getOrDefault(edge.to(), Integer.MAX_VALUE);
                trace.add("  relax " + current.node() + "->" + edge.to() + " candidate=" + nextDistance
                        + ", old=" + (oldDistance == Integer.MAX_VALUE ? "INF" : oldDistance));
                if (nextDistance < oldDistance) {
                    distance.put(edge.to(), nextDistance);
                    previous.put(edge.to(), current.node());
                    queue.add(new NodeDistance(edge.to(), nextDistance));
                }
            }
        }

        int finalDistance = distance.getOrDefault(target, -1);
        return new DijkstraResult(finalDistance, rebuildPath(previous, start, target, finalDistance), trace);
    }

    private static void dfs(int node, List<List<Integer>> graph, boolean[] visited, List<Integer> component) {
        visited[node] = true;
        component.add(node);
        for (int next : graph.get(node)) {
            if (!visited[next]) {
                dfs(next, graph, visited, component);
            }
        }
    }

    private static void backtrackSubsets(
            int start,
            int[] numbers,
            List<Integer> current,
            List<List<Integer>> result
    ) {
        result.add(new ArrayList<>(current));
        for (int i = start; i < numbers.length; i++) {
            current.add(numbers[i]);
            backtrackSubsets(i + 1, numbers, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static List<String> rebuildPath(
            Map<String, String> previous,
            String start,
            String target,
            int finalDistance
    ) {
        if (finalDistance < 0) {
            return List.of();
        }

        ArrayDeque<String> path = new ArrayDeque<>();
        String current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(start)) {
                return new ArrayList<>(path);
            }
            current = previous.get(current);
        }
        return List.of();
    }

    private record NodeDistance(String node, int distance) {
    }

    private static final class UnionFind {
        private final int[] parent;
        private final int[] rank;

        private UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        private int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]);
            }
            return parent[node];
        }

        private void union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA == rootB) {
                return;
            }

            if (rank[rootA] < rank[rootB]) {
                parent[rootA] = rootB;
            } else if (rank[rootA] > rank[rootB]) {
                parent[rootB] = rootA;
            } else {
                parent[rootB] = rootA;
                rank[rootA]++;
            }
        }

        private int[] parents() {
            int[] snapshot = new int[parent.length];
            for (int i = 0; i < parent.length; i++) {
                snapshot[i] = find(i);
            }
            return snapshot;
        }
    }
}
