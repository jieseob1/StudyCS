package studycs.javalab.algorithms.graph;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public final class GridShortestPathBfs {
    private GridShortestPathBfs() {
    }

    public static int shortestPath(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] distance = emptyDistance(rows, cols);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {0, 0});
        distance[0][0] = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            for (int direction = 0; direction < 4; direction++) {
                int nextRow = row + dr[direction];
                int nextCol = col + dc[direction];

                if (cannotVisit(grid, distance, nextRow, nextCol)) {
                    continue;
                }

                distance[nextRow][nextCol] = distance[row][col] + 1;
                queue.offer(new int[] {nextRow, nextCol});
            }
        }

        return distance[rows - 1][cols - 1];
    }

    public static void trace(int[][] grid) {
        System.out.println("  BFS trace");
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] distance = emptyDistance(rows, cols);

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[] {0, 0});
        distance[0][0] = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            System.out.println("    poll=(" + row + "," + col + "), distance=" + distance[row][col]);

            for (int direction = 0; direction < 4; direction++) {
                int nextRow = row + dr[direction];
                int nextCol = col + dc[direction];

                if (cannotVisit(grid, distance, nextRow, nextCol)) {
                    continue;
                }

                distance[nextRow][nextCol] = distance[row][col] + 1;
                queue.offer(new int[] {nextRow, nextCol});
                System.out.println("      offer=(" + nextRow + "," + nextCol + "), distance=" + distance[nextRow][nextCol]);
            }
        }
    }

    private static int[][] emptyDistance(int rows, int cols) {
        int[][] distance = new int[rows][cols];
        for (int[] row : distance) {
            Arrays.fill(row, -1);
        }
        return distance;
    }

    private static boolean cannotVisit(int[][] grid, int[][] distance, int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
            return true;
        }
        return grid[row][col] == 1 || distance[row][col] != -1;
    }
}
