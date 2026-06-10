package studycs.javalab.structures.unionfind;

public class DisjointSet {
    private final int[] parent;
    private final int[] size;

    public DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    public static void runDemo() {
        DisjointSet set = new DisjointSet(5);
        set.union(0, 1);
        set.union(1, 2);
        System.out.println("Union-Find connected(0, 2): " + set.connected(0, 2));
        System.out.println("Union-Find connected(0, 4): " + set.connected(0, 4));
    }
}
