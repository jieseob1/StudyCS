package studycs.javalab.algorithms.graph;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class DijkstraShortestPath {
    private DijkstraShortestPath() {
    }

    public record Edge(int to, int weight) {
    }

    private record State(int node, int distance) {
    }

    public static int[] find(List<List<Edge>> graph, int start) {
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[start] = 0;

        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingInt(State::distance));
        pq.offer(new State(start, 0));

        while (!pq.isEmpty()) {
            State current = pq.poll();
            if (current.distance() != distance[current.node()]) {
                continue;
            }

            for (Edge edge : graph.get(current.node())) {
                int nextDistance = current.distance() + edge.weight();
                if (nextDistance < distance[edge.to()]) {
                    distance[edge.to()] = nextDistance;
                    pq.offer(new State(edge.to(), nextDistance));
                }
            }
        }

        return distance;
    }
}
