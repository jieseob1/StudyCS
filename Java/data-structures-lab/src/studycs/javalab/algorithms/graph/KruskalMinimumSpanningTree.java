package studycs.javalab.algorithms.graph;

import java.util.Arrays;
import java.util.Comparator;

import studycs.javalab.structures.unionfind.DisjointSet;

public final class KruskalMinimumSpanningTree {
    private KruskalMinimumSpanningTree() {
    }

    public static int weight(int n, int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, Comparator.comparingInt(edge -> edge[2]));

        DisjointSet disjointSet = new DisjointSet(n);
        int total = 0;
        int used = 0;

        for (int[] edge : sorted) {
            if (disjointSet.union(edge[0], edge[1])) {
                total += edge[2];
                used++;
                if (used == n - 1) {
                    return total;
                }
            }
        }

        return used == n - 1 ? total : -1;
    }

    public static void runDemo() {
        System.out.println("Kruskal MST weight -> " + weight(4, new int[][] {
            {0, 1, 1}, {1, 2, 2}, {2, 3, 3}, {0, 3, 10}
        }));
    }
}
