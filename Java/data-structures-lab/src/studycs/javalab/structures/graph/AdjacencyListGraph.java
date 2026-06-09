package studycs.javalab.structures.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class AdjacencyListGraph {
    private final List<List<Integer>> graph;

    public AdjacencyListGraph(int n) {
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
    }

    public void addUndirectedEdge(int a, int b) {
        graph.get(a).add(b);
        graph.get(b).add(a);
    }

    public List<Integer> bfsOrder(int start) {
        boolean[] visited = new boolean[graph.size()];
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);

            for (int next : graph.get(current)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.offer(next);
                }
            }
        }

        return order;
    }

    public List<Integer> dfsOrder(int start) {
        boolean[] visited = new boolean[graph.size()];
        List<Integer> order = new ArrayList<>();
        dfs(start, visited, order);
        return order;
    }

    public static void runDemo() {
        AdjacencyListGraph graph = new AdjacencyListGraph(5);
        graph.addUndirectedEdge(0, 1);
        graph.addUndirectedEdge(0, 2);
        graph.addUndirectedEdge(1, 3);
        graph.addUndirectedEdge(2, 4);
        System.out.println("AdjacencyListGraph BFS from 0: " + graph.bfsOrder(0));
        System.out.println("AdjacencyListGraph DFS from 0: " + graph.dfsOrder(0));
    }

    private void dfs(int current, boolean[] visited, List<Integer> order) {
        visited[current] = true;
        order.add(current);

        for (int next : graph.get(current)) {
            if (!visited[next]) {
                dfs(next, visited, order);
            }
        }
    }
}
