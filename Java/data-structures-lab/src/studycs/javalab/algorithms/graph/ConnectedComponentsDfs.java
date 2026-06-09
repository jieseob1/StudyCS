package studycs.javalab.algorithms.graph;

import java.util.ArrayList;
import java.util.List;

public final class ConnectedComponentsDfs {
    private ConnectedComponentsDfs() {
    }

    public static int count(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        boolean[] visited = new boolean[n];
        int components = 0;
        for (int node = 0; node < n; node++) {
            if (!visited[node]) {
                components++;
                dfs(node, graph, visited);
            }
        }

        return components;
    }

    public static void runDemo() {
        System.out.println("ConnectedComponents count -> " + count(5, new int[][] {
            {0, 1}, {1, 2}, {3, 4}
        }));
    }

    private static void dfs(int current, List<List<Integer>> graph, boolean[] visited) {
        visited[current] = true;
        for (int next : graph.get(current)) {
            if (!visited[next]) {
                dfs(next, graph, visited);
            }
        }
    }
}
