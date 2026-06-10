package studycs.javalab.algorithms.graph;

public final class FloydWarshall {
    public static final int INF = 1_000_000_000;

    private FloydWarshall() {
    }

    public static int[][] shortestPaths(int[][] graph) {
        int n = graph.length;
        int[][] distance = new int[n][n];
        for (int row = 0; row < n; row++) {
            distance[row] = graph[row].clone();
        }

        for (int mid = 0; mid < n; mid++) {
            for (int from = 0; from < n; from++) {
                for (int to = 0; to < n; to++) {
                    if (distance[from][mid] == INF || distance[mid][to] == INF) {
                        continue;
                    }
                    distance[from][to] = Math.min(distance[from][to], distance[from][mid] + distance[mid][to]);
                }
            }
        }

        return distance;
    }

    public static void runDemo() {
        int[][] shortest = shortestPaths(new int[][] {
            {0, 3, INF},
            {INF, 0, 4},
            {2, INF, 0}
        });
        System.out.println("FloydWarshall distance 0->2: " + shortest[0][2]);
        System.out.println("FloydWarshall distance 2->1: " + shortest[2][1]);
    }
}
