# 이직/면접 자료구조 정리

## 1. 면접에서 자료구조를 보는 관점

자료구조 문제는 이름을 맞히는 문제가 아니라 "필요한 연산을 제한 시간 안에 처리하는 구조를 고르는 문제"다.

문제를 받으면 아래 순서로 본다.

1. 입력 크기 `n`을 본다.
2. 반복문을 몇 번까지 돌릴 수 있는지 계산한다.
3. 필요한 연산을 분류한다.
4. 빠르게 해야 하는 연산을 기준으로 자료구조를 고른다.
5. 그 자료구조가 유지해야 하는 불변식을 정한다.
6. 삽입, 삭제, 조회 시 불변식이 유지되는지 확인한다.

입력 크기 감각:

| n | 가능한 접근 |
|---:|---|
| 10 이하 | 완전탐색, backtracking |
| 20 이하 | bitmask, meet-in-the-middle 일부 |
| 1,000 이하 | O(n^2) 가능 |
| 100,000 이하 | O(n log n), O(n) 필요 |
| 1,000,000 이상 | 대체로 O(n), 메모리도 주의 |

## 2. 자료구조 선택 신호

| 문제 신호 | 떠올릴 구조 |
|---|---|
| 포함 여부, 중복 제거, 빈도수 | `HashSet`, `HashMap` |
| 가장 최근/가장 먼저 처리 | `Stack`, `Queue`, `Deque` |
| 양끝에서 넣고 빼기 | `Deque` |
| 최솟값/최댓값을 반복 추출 | `PriorityQueue` |
| 정렬된 key에서 바로 이전/다음 | `TreeMap`, `TreeSet` |
| prefix 검색 | Trie |
| 연결 여부, 그룹 합치기 | Union-Find |
| 구간 합, 점 업데이트 | Fenwick Tree |
| 구간 최소/최대/합, 구간 업데이트 | Segment Tree |
| 그래프 최단거리 | BFS, Dijkstra |
| 선후 관계, 빌드 순서 | Topological Sort |
| 최근 사용 제거 | HashMap + Doubly Linked List, `LinkedHashMap` |
| sliding window 최댓값 | Monotonic Deque |
| 다음 큰 수 | Monotonic Stack |

## 3. 배열과 문자열

### 왜 나왔나

배열은 같은 타입 데이터를 연속 메모리에 저장해서 인덱스 접근을 O(1)로 하기 위해 나온 구조다. 문자열 문제도 대부분 문자 배열 위에서 동작한다고 보면 된다.

### 동작과 비용

| 연산 | 비용 |
|---|---:|
| `arr[i]` | O(1) |
| 끝 삽입 | 고정 배열은 불가, 동적 배열은 amortized O(1) |
| 중간 삽입/삭제 | O(n) |
| 전체 순회 | O(n) |
| 정렬 | 보통 O(n log n) |

### 문제 풀이 패턴

#### Two Pointers

정렬되어 있거나 양끝에서 좁혀갈 수 있을 때 쓴다.

예: 정렬 배열에서 두 수의 합.

```java
static boolean hasTwoSumSorted(int[] arr, int target) {
    int left = 0;
    int right = arr.length - 1;

    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) {
            return true;
        }
        if (sum < target) {
            left++;
        } else {
            right--;
        }
    }

    return false;
}
```

불변식:

- `left`보다 왼쪽, `right`보다 오른쪽은 더 이상 답이 될 수 없게 제거한다.

#### Sliding Window

연속 부분 배열/부분 문자열에서 조건을 만족하는 구간을 찾을 때 쓴다.

예: 중복 없는 가장 긴 부분 문자열.

```java
static int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastIndex = new HashMap<>();
    int left = 0;
    int best = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        if (lastIndex.containsKey(c)) {
            left = Math.max(left, lastIndex.get(c) + 1);
        }

        lastIndex.put(c, right);
        best = Math.max(best, right - left + 1);
    }

    return best;
}
```

동작 과정:

1. `right`가 새 문자를 포함한다.
2. 중복이 생기면 `left`를 중복 문자의 다음 위치로 이동한다.
3. 항상 `[left, right]` 구간은 중복 없는 상태를 유지한다.

#### Prefix Sum

구간 합을 빠르게 구할 때 쓴다.

```java
static int rangeSum(int[] prefix, int left, int right) {
    return prefix[right + 1] - prefix[left];
}

static int[] buildPrefixSum(int[] arr) {
    int[] prefix = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++) {
        prefix[i + 1] = prefix[i] + arr[i];
    }
    return prefix;
}
```

불변식:

- `prefix[i]`는 `arr[0]`부터 `arr[i - 1]`까지의 합이다.

#### Difference Array

구간에 값을 여러 번 더한 뒤 최종 배열만 필요할 때 쓴다.

```java
static int[] applyRangeAdds(int n, int[][] queries) {
    int[] diff = new int[n + 1];

    for (int[] query : queries) {
        int left = query[0];
        int right = query[1];
        int value = query[2];

        diff[left] += value;
        if (right + 1 < n) {
            diff[right + 1] -= value;
        }
    }

    int[] result = new int[n];
    int current = 0;
    for (int i = 0; i < n; i++) {
        current += diff[i];
        result[i] = current;
    }

    return result;
}
```

## 4. 연결 리스트

### 왜 나왔나

배열은 중간 삽입/삭제 시 원소 이동 비용이 크다. 연결 리스트는 노드 연결만 바꿔 삽입/삭제를 빠르게 하려고 나왔다.

### 면접에서 중요한 포인트

실무 Java에서는 `LinkedList`를 자주 쓰지 않지만, 면접에서는 pointer 조작 능력을 보기 좋다.

자주 나오는 패턴:

- dummy node.
- fast/slow pointer.
- reverse.
- cycle detection.
- merge two sorted lists.
- remove nth from end.

기본 노드:

```java
static class ListNode {
    int val;
    ListNode next;

    ListNode(int val) {
        this.val = val;
    }
}
```

뒤집기:

```java
static ListNode reverse(ListNode head) {
    ListNode prev = null;
    ListNode current = head;

    while (current != null) {
        ListNode next = current.next;
        current.next = prev;
        prev = current;
        current = next;
    }

    return prev;
}
```

동작 과정:

1. `next`를 먼저 저장한다.
2. 현재 노드의 방향을 이전 노드로 돌린다.
3. `prev`와 `current`를 한 칸 전진한다.

cycle detection:

```java
static boolean hasCycle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            return true;
        }
    }

    return false;
}
```

## 5. Stack

### 왜 나왔나

가장 나중에 들어온 것을 먼저 처리하는 LIFO 흐름을 표현하기 위해 나왔다.

대표 문제:

- 괄호 검증.
- 함수 호출/재귀 추적.
- undo.
- expression parsing.
- DFS.
- monotonic stack.

Java에서는 `Stack` 클래스보다 `ArrayDeque`를 사용한다.

```java
Deque<Character> stack = new ArrayDeque<>();

for (char c : s.toCharArray()) {
    if (c == '(') {
        stack.push(c);
    } else if (c == ')') {
        if (stack.isEmpty()) {
            return false;
        }
        stack.pop();
    }
}

return stack.isEmpty();
```

### Monotonic Stack

스택 안의 값이 증가 또는 감소 상태를 유지하도록 만든다. "다음 큰 수", "오른쪽에서 처음으로 작은 값" 같은 문제에 쓴다.

```java
static int[] nextGreaterElement(int[] nums) {
    int[] result = new int[nums.length];
    Arrays.fill(result, -1);

    Deque<Integer> stack = new ArrayDeque<>(); // index 저장

    for (int i = 0; i < nums.length; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }

    return result;
}
```

동작 과정:

1. 스택에는 아직 다음 큰 수를 찾지 못한 index를 둔다.
2. 새 값이 stack top보다 크면, 그 새 값이 top의 next greater다.
3. 해결된 index를 pop한다.
4. 각 index는 한 번 push, 한 번 pop되므로 O(n)이다.

## 6. Queue와 Deque

### Queue

FIFO 처리다. BFS, 작업 대기열, producer-consumer에 사용한다.

```java
Queue<Integer> queue = new ArrayDeque<>();
queue.offer(1);
queue.offer(2);

while (!queue.isEmpty()) {
    int current = queue.poll();
}
```

### BFS

가중치가 없는 그래프/격자에서 최단 거리를 구할 때 쓴다.

```java
static int shortestPath(int[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    int[][] dist = new int[rows][cols];
    for (int[] row : dist) {
        Arrays.fill(row, -1);
    }

    int[] dr = {-1, 1, 0, 0};
    int[] dc = {0, 0, -1, 1};

    Queue<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[] {0, 0});
    dist[0][0] = 0;

    while (!queue.isEmpty()) {
        int[] current = queue.poll();
        int r = current[0];
        int c = current[1];

        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d];
            int nc = c + dc[d];

            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                continue;
            }
            if (grid[nr][nc] == 1 || dist[nr][nc] != -1) {
                continue;
            }

            dist[nr][nc] = dist[r][c] + 1;
            queue.offer(new int[] {nr, nc});
        }
    }

    return dist[rows - 1][cols - 1];
}
```

### Monotonic Deque

sliding window에서 최댓값/최솟값을 O(n)에 구할 때 쓴다.

```java
static int[] maxSlidingWindow(int[] nums, int k) {
    int[] result = new int[nums.length - k + 1];
    Deque<Integer> deque = new ArrayDeque<>(); // index 저장, 값은 내림차순 유지

    for (int i = 0; i < nums.length; i++) {
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }

        while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
            deque.pollLast();
        }

        deque.offerLast(i);

        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }

    return result;
}
```

불변식:

- deque의 index는 window 안에 있다.
- deque의 값은 앞에서 뒤로 갈수록 작아진다.
- 맨 앞은 현재 window의 최댓값이다.

## 7. Hash Table

### 왜 나왔나

배열 인덱스처럼 O(1)에 찾고 싶지만 key가 숫자 범위로 제한되지 않을 때, key를 hash로 바꿔 위치를 찾기 위해 나왔다.

주요 용도:

- 빈도수 세기.
- 중복 검출.
- 캐싱.
- grouping.
- two-sum.
- 방문 처리.

빈도수:

```java
static Map<Character, Integer> frequency(String s) {
    Map<Character, Integer> count = new HashMap<>();
    for (char c : s.toCharArray()) {
        count.merge(c, 1, Integer::sum);
    }
    return count;
}
```

two-sum:

```java
static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> indexByValue = new HashMap<>();

    for (int i = 0; i < nums.length; i++) {
        int need = target - nums[i];
        if (indexByValue.containsKey(need)) {
            return new int[] {indexByValue.get(need), i};
        }
        indexByValue.put(nums[i], i);
    }

    return new int[] {-1, -1};
}
```

## 8. Tree

### 왜 나왔나

계층 구조를 표현하거나, 정렬된 상태에서 탐색/삽입/삭제를 균형 있게 처리하기 위해 나왔다.

### Binary Tree Traversal

```java
static void preorder(TreeNode node, List<Integer> result) {
    if (node == null) {
        return;
    }

    result.add(node.val);
    preorder(node.left, result);
    preorder(node.right, result);
}

static void inorder(TreeNode node, List<Integer> result) {
    if (node == null) {
        return;
    }

    inorder(node.left, result);
    result.add(node.val);
    inorder(node.right, result);
}

static void postorder(TreeNode node, List<Integer> result) {
    if (node == null) {
        return;
    }

    postorder(node.left, result);
    postorder(node.right, result);
    result.add(node.val);
}
```

BST 불변식:

- 왼쪽 subtree의 모든 값 < 현재 값.
- 오른쪽 subtree의 모든 값 > 현재 값.
- inorder traversal 결과가 정렬된다.

TreeMap/TreeSet은 면접에서 직접 구현하지 않아도 ordered map/set 문제에 바로 쓸 수 있다.

```java
TreeMap<Integer, Integer> map = new TreeMap<>();
Integer prev = map.floorKey(10);
Integer next = map.ceilingKey(10);
```

## 9. Heap

### 왜 나왔나

전체를 정렬하지 않고도 최솟값/최댓값을 빠르게 반복해서 꺼내기 위해 나왔다.

대표 문제:

- top k.
- kth largest.
- 작업 스케줄링.
- 회의실 개수.
- Dijkstra.
- median 유지.

회의실 개수:

```java
static int minMeetingRooms(int[][] intervals) {
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

    PriorityQueue<Integer> ends = new PriorityQueue<>();

    for (int[] interval : intervals) {
        int start = interval[0];
        int end = interval[1];

        if (!ends.isEmpty() && ends.peek() <= start) {
            ends.poll();
        }

        ends.offer(end);
    }

    return ends.size();
}
```

동작 과정:

1. 시작 시간 기준으로 정렬한다.
2. heap에는 현재 사용 중인 회의실의 종료 시간을 넣는다.
3. 가장 빨리 끝나는 회의가 새 회의 시작 전에 끝났으면 재사용한다.
4. heap 크기가 필요한 회의실 수다.

Top K:

```java
static List<Integer> topK(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }

    List<Integer> result = new ArrayList<>(minHeap);
    result.sort(Comparator.reverseOrder());
    return result;
}
```

복잡도:

- 전체 정렬: O(n log n)
- heap top-k: O(n log k)

## 10. Graph

### 왜 나왔나

객체 사이의 관계를 표현하기 위해 나왔다. 트리는 사이클 없는 계층 구조지만, 그래프는 더 일반적인 연결 관계다.

표현 방식:

| 방식 | 메모리 | 적합한 경우 |
|---|---:|---|
| adjacency matrix | O(V^2) | V가 작고 간선 확인이 매우 잦음 |
| adjacency list | O(V + E) | 대부분의 면접 문제 |

인접 리스트:

```java
static List<List<Integer>> buildGraph(int n, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        graph.add(new ArrayList<>());
    }

    for (int[] edge : edges) {
        int from = edge[0];
        int to = edge[1];
        graph.get(from).add(to);
        graph.get(to).add(from);
    }

    return graph;
}
```

DFS:

```java
static void dfs(int node, List<List<Integer>> graph, boolean[] visited) {
    visited[node] = true;

    for (int next : graph.get(node)) {
        if (!visited[next]) {
            dfs(next, graph, visited);
        }
    }
}
```

Topological Sort:

```java
static List<Integer> topologicalSort(int n, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        graph.add(new ArrayList<>());
    }

    int[] indegree = new int[n];
    for (int[] edge : edges) {
        int from = edge[0];
        int to = edge[1];
        graph.get(from).add(to);
        indegree[to]++;
    }

    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        if (indegree[i] == 0) {
            queue.offer(i);
        }
    }

    List<Integer> order = new ArrayList<>();
    while (!queue.isEmpty()) {
        int current = queue.poll();
        order.add(current);

        for (int next : graph.get(current)) {
            indegree[next]--;
            if (indegree[next] == 0) {
                queue.offer(next);
            }
        }
    }

    if (order.size() != n) {
        return List.of(); // cycle
    }

    return order;
}
```

Dijkstra:

```java
record Edge(int to, int weight) {
}

record State(int node, int distance) {
}

static int[] dijkstra(List<List<Edge>> graph, int start) {
    int n = graph.size();
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;

    PriorityQueue<State> pq =
        new PriorityQueue<>(Comparator.comparingInt(State::distance));
    pq.offer(new State(start, 0));

    while (!pq.isEmpty()) {
        State current = pq.poll();
        if (current.distance() != dist[current.node()]) {
            continue;
        }

        for (Edge edge : graph.get(current.node())) {
            int nextDistance = current.distance() + edge.weight();
            if (nextDistance < dist[edge.to()]) {
                dist[edge.to()] = nextDistance;
                pq.offer(new State(edge.to(), nextDistance));
            }
        }
    }

    return dist;
}
```

주의:

- 음수 간선이 있으면 일반 Dijkstra를 쓰면 안 된다.
- 가중치가 모두 1이면 BFS가 더 단순하고 빠르다.

## 11. Trie

### 왜 나왔나

문자열 prefix 검색을 빠르게 하기 위해 나왔다. 해시 테이블은 전체 문자열 조회는 빠르지만 prefix 탐색에는 직접적이지 않다.

기본 구현:

```java
static class Trie {
    private final Node root = new Node();

    void insert(String word) {
        Node current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new Node());
            current = current.children.get(c);
        }
        current.word = true;
    }

    boolean search(String word) {
        Node node = find(word);
        return node != null && node.word;
    }

    boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    private Node find(String text) {
        Node current = root;
        for (char c : text.toCharArray()) {
            current = current.children.get(c);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean word;
    }
}
```

복잡도:

- 삽입: O(L)
- 단어 검색: O(L)
- prefix 검색: O(L)

L은 문자열 길이다.

## 12. Union-Find

### 왜 나왔나

원소들이 같은 그룹에 속하는지 빠르게 확인하고, 두 그룹을 빠르게 합치기 위해 나왔다.

대표 문제:

- 네트워크 연결.
- 무방향 그래프 cycle 검출.
- 섬 개수.
- Kruskal MST.
- 계정 병합.

구현:

```java
static class DisjointSet {
    private final int[] parent;
    private final int[] size;

    DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    boolean union(int a, int b) {
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
}
```

성능:

- path compression + union by size/rank를 쓰면 거의 O(1)에 가깝다.
- 정확히는 inverse Ackermann 수준으로 매우 느리게 증가한다.

동작 과정:

1. 각 원소는 처음에 자기 자신이 대표다.
2. `find`는 대표 root를 찾는다.
3. 찾는 과정에서 parent를 root로 바로 연결해 경로를 압축한다.
4. `union`은 작은 트리를 큰 트리 아래에 붙인다.

## 13. Fenwick Tree

### 왜 나왔나

배열에서 값이 바뀌고, prefix sum이나 range sum query가 반복될 때 O(log n)에 처리하기 위해 나왔다.

적합한 경우:

- point update.
- prefix sum query.
- range sum query.

구현:

```java
static class FenwickTree {
    private final long[] tree;

    FenwickTree(int n) {
        tree = new long[n + 1];
    }

    void add(int index, long delta) {
        for (int i = index + 1; i < tree.length; i += i & -i) {
            tree[i] += delta;
        }
    }

    long prefixSum(int index) {
        long sum = 0;
        for (int i = index + 1; i > 0; i -= i & -i) {
            sum += tree[i];
        }
        return sum;
    }

    long rangeSum(int left, int right) {
        if (left == 0) {
            return prefixSum(right);
        }
        return prefixSum(right) - prefixSum(left - 1);
    }
}
```

핵심:

- `i & -i`는 i가 담당하는 구간 크기를 의미한다.
- 구현은 1-indexed가 편하다.

## 14. Segment Tree

### 왜 나왔나

구간 query와 update를 모두 빠르게 처리하기 위해 나왔다. Fenwick Tree보다 구현은 복잡하지만, 합뿐 아니라 최소/최대/gcd 등 다양한 연산에 쓸 수 있다.

적합한 경우:

- range min/max/sum query.
- point update.
- lazy propagation을 쓰면 range update.

구현:

```java
static class SegmentTree {
    private final long[] tree;
    private final int n;

    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new long[n * 4];
        build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
            return;
        }

        int mid = (start + end) / 2;
        build(arr, node * 2, start, mid);
        build(arr, node * 2 + 1, mid + 1, end);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    void update(int index, int value) {
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            tree[node] = value;
            return;
        }

        int mid = (start + end) / 2;
        if (index <= mid) {
            update(node * 2, start, mid, index, value);
        } else {
            update(node * 2 + 1, mid + 1, end, index, value);
        }

        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    long query(int left, int right) {
        return query(1, 0, n - 1, left, right);
    }

    private long query(int node, int start, int end, int left, int right) {
        if (right < start || end < left) {
            return 0;
        }
        if (left <= start && end <= right) {
            return tree[node];
        }

        int mid = (start + end) / 2;
        return query(node * 2, start, mid, left, right)
            + query(node * 2 + 1, mid + 1, end, left, right);
    }
}
```

복잡도:

- build: O(n)
- update: O(log n)
- query: O(log n)
- memory: O(n)

## 15. LRU Cache

### 왜 나왔나

캐시 용량이 제한되어 있을 때, 가장 오래 사용하지 않은 항목을 제거하기 위해 나왔다.

필요한 연산:

- `get`: O(1)
- `put`: O(1)
- 최근 사용 순서 갱신: O(1)
- 가장 오래된 항목 제거: O(1)

자료구조 조합:

- `HashMap`: key로 node를 O(1)에 찾는다.
- Doubly Linked List: node를 O(1)에 제거하고 맨 앞으로 옮긴다.

Java에서는 `LinkedHashMap`으로 간단히 구현 가능하다.

```java
static class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LruCache(int capacity) {
        super(16, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

## 16. BitSet과 Bitmask

### 왜 나왔나

boolean 상태 여러 개를 매우 적은 메모리로 저장하거나, 부분집합 상태를 정수 하나로 표현하기 위해 나왔다.

`BitSet`:

```java
BitSet visited = new BitSet();
visited.set(10);
System.out.println(visited.get(10)); // true
```

bitmask:

```java
int mask = 0;
mask |= 1 << 3;              // 3번 bit 켜기
boolean has = (mask & (1 << 3)) != 0;
mask &= ~(1 << 3);           // 3번 bit 끄기
```

적합한 문제:

- 원소 수가 작을 때 부분집합 순회.
- 방문 상태 압축.
- DP state 압축.

## 17. 문제 풀이 패턴 요약

| 패턴 | 언제 쓰나 | 핵심 자료구조 |
|---|---|---|
| Two Pointers | 정렬 배열, 양끝 조정 | 배열 |
| Sliding Window | 연속 구간 | 배열, HashMap, Deque |
| Prefix Sum | 구간 합 반복 | 배열 |
| Difference Array | 구간 업데이트 후 최종 상태 | 배열 |
| Monotonic Stack | 다음 큰/작은 값 | Stack |
| Monotonic Deque | window 최댓값/최솟값 | Deque |
| BFS | 무가중치 최단거리 | Queue |
| DFS | 연결 요소, backtracking | Stack/Recursion |
| Topological Sort | 선후 관계 | Queue, Graph |
| Dijkstra | 양수 가중치 최단거리 | PriorityQueue |
| Union-Find | 연결 여부, 그룹 병합 | Disjoint Set |
| Trie | prefix 검색 | Tree + Map/Array |
| Fenwick Tree | point update + range sum | Binary Indexed Tree |
| Segment Tree | range query/update | Tree |

## 18. Java 면접 구현 팁

### `record`로 pair 표현

```java
record Point(int row, int col) {
}

Queue<Point> queue = new ArrayDeque<>();
queue.offer(new Point(0, 0));
```

### Comparator overflow 피하기

```java
// 위험
// Arrays.sort(arr, (a, b) -> a[0] - b[0]);

Arrays.sort(arr, Comparator.comparingInt(a -> a[0]));
```

### `ArrayDeque` 우선 사용

```java
Deque<Integer> stack = new ArrayDeque<>();
Queue<Integer> queue = new ArrayDeque<>();
```

### `computeIfAbsent`로 그래프 구성

```java
Map<Integer, List<Integer>> graph = new HashMap<>();

for (int[] edge : edges) {
    graph.computeIfAbsent(edge[0], key -> new ArrayList<>()).add(edge[1]);
    graph.computeIfAbsent(edge[1], key -> new ArrayList<>()).add(edge[0]);
}
```

### 빈도수는 `merge`

```java
Map<String, Integer> count = new HashMap<>();

for (String word : words) {
    count.merge(word, 1, Integer::sum);
}
```

### 방문 배열과 방문 Set

index가 0부터 n-1이면 배열이 빠르다.

```java
boolean[] visited = new boolean[n];
```

key가 문자열/객체면 Set이 편하다.

```java
Set<String> visited = new HashSet<>();
```

## 19. 자주 나오는 복잡도 비교

| 목표 | 단순 접근 | 개선 접근 |
|---|---|---|
| 두 수 합 | 이중 루프 O(n^2) | HashMap O(n) |
| 정렬 배열 두 수 합 | 이중 루프 O(n^2) | Two pointers O(n) |
| 구간 합 여러 번 | 매번 합 O(nq) | Prefix sum O(n + q) |
| sliding window max | 매번 max O(nk) | Monotonic deque O(n) |
| top k | 전체 정렬 O(n log n) | Heap O(n log k) |
| 연결 여부 반복 | DFS/BFS 반복 O(q(V+E)) | Union-Find 거의 O(q) |
| prefix 검색 | 모든 단어 확인 O(nL) | Trie O(L) |
| range update 다수 | 매번 갱신 O(nq) | Difference array O(n + q) |
| point update + range sum | 배열 합 O(nq) | Fenwick O((n+q) log n) |

## 20. 자료구조별 면접 설명 템플릿

면접에서 답할 때는 아래 형식이 좋다.

```text
이 문제는 [필요 연산]이 반복됩니다.
단순히 하면 [복잡도]가 걸립니다.
[자료구조]를 쓰면 [이유] 때문에 [복잡도]로 줄일 수 있습니다.
핵심 불변식은 [불변식]입니다.
삽입/삭제/조회 때 이 불변식을 이렇게 유지합니다.
```

예:

```text
이 문제는 현재 window의 최댓값을 매 위치마다 알아야 합니다.
매번 window를 스캔하면 O(nk)가 걸립니다.
Deque에 index를 저장하고 값이 내림차순이 되도록 유지하면 맨 앞이 항상 최댓값입니다.
각 index는 한 번 들어가고 한 번 나오므로 전체 O(n)에 풀 수 있습니다.
```

## 21. 공부 순서

1. Java 컬렉션 API와 시간 복잡도를 먼저 외운다.
2. `HashMap`, `ArrayDeque`, `PriorityQueue`, `TreeMap`을 손에 익힌다.
3. 배열 패턴: two pointers, sliding window, prefix sum.
4. stack/queue 패턴: monotonic stack, BFS.
5. graph: DFS, BFS, topological sort, Dijkstra.
6. advanced: Union-Find, Trie, Fenwick Tree, Segment Tree.
7. 각 문제마다 "왜 이 구조인가"를 한 문장으로 설명하는 연습을 한다.

