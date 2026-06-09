# Algorithm Exam Guide

이 문서는 시험/코딩테스트 대비용 알고리즘 체크리스트입니다. 목적은 문제를 보고 바로 패턴을 고르는 것입니다.

## 1. 문제를 받으면 먼저 볼 것

1. 입력 크기 `n`, 간선 수 `m`, 질의 수 `q`.
2. 정렬되어 있는지, 정렬해도 되는지.
3. 연속 구간인지, 임의 구간인지.
4. "최소/최대/최단/경우의 수" 중 무엇을 묻는지.
5. 그래프라면 가중치가 있는지, 음수인지, 방향이 있는지.
6. 중복을 허용하는지, 순서가 중요한지.

## 2. 복잡도 감각

| 입력 크기 | 가능한 풀이 |
|---:|---|
| `n <= 10` | 순열, 조합, 백트래킹 |
| `n <= 20` | bitmask, 완전탐색 일부 |
| `n <= 1,000` | O(n^2), Floyd-Warshall |
| `n <= 100,000` | O(n log n), O(n) |
| `n >= 1,000,000` | O(n), 메모리 주의 |

## 3. 시험 대비 패턴별 색인

| 패턴 | 파일 | 문제 신호 | 핵심 |
|---|---|---|---|
| 병합 정렬 | `algorithms/sorting/MergeSort.java` | 안정 정렬, divide and conquer | 반으로 나누고 정렬된 두 배열을 병합 |
| 퀵 정렬 | `algorithms/sorting/QuickSort.java` | pivot partition | pivot 기준 왼쪽/오른쪽 분할 |
| 계수 정렬 | `algorithms/sorting/CountingSort.java` | 값 범위가 작음 | 값 개수를 세서 O(n+k)에 정렬 |
| 이분 탐색 | `algorithms/search/BinarySearch.java` | 정렬 배열, 답을 반씩 줄일 수 있음 | `left`, `right`, `mid` 불변식 |
| 누적합 | `algorithms/arrayhash/PrefixSum.java` | 구간 합 질의 반복 | `prefix[r+1] - prefix[l]` |
| Two Sum | `algorithms/arrayhash/TwoSum.java` | 보수 값 조회 | `HashMap`에 이전 값 저장 |
| Sliding Window | `algorithms/arrayhash/LongestSubstringWithoutRepeating.java` | 연속 부분 배열/문자열 | 조건이 깨지면 왼쪽 이동 |
| 순열 | `algorithms/backtracking/Permutations.java` | 모든 순서 나열 | 선택, 재귀, 원복 |
| 조합 합 | `algorithms/backtracking/CombinationSum.java` | target을 만드는 조합 | start index로 중복 제어 |
| 동전 교환 DP | `algorithms/dp/CoinChange.java` | 최소 개수/최적값 | `dp[x] = min(dp[x-coin] + 1)` |
| LIS | `algorithms/dp/LongestIncreasingSubsequence.java` | 증가 부분 수열 | tails 배열 + lowerBound |
| 활동 선택 | `algorithms/greedy/ActivitySelection.java` | 겹치지 않는 최대 일정 | 가장 빨리 끝나는 것부터 선택 |
| 소수 체 | `algorithms/math/SieveOfEratosthenes.java` | 여러 소수 판별 | 배수를 지워 O(n log log n) |
| GCD/LCM | `algorithms/math/GcdLcm.java` | 최대공약수/최소공배수 | 유클리드 호제법 |
| KMP | `algorithms/string/KmpSearch.java` | 문자열 패턴 검색 | 실패 함수로 비교 되돌림 최소화 |
| BFS 최단거리 | `algorithms/graph/GridShortestPathBfs.java` | 무가중치 최단거리 | queue로 거리 레벨 확장 |
| DFS 컴포넌트 | `algorithms/graph/ConnectedComponentsDfs.java` | 연결 요소 개수 | 방문 배열 + DFS |
| 위상 정렬 | `algorithms/graph/TopologicalSort.java` | 선수 관계, 빌드 순서 | indegree 0부터 queue |
| Dijkstra | `algorithms/graph/DijkstraShortestPath.java` | 양수 가중치 최단거리 | `PriorityQueue`로 가장 짧은 후보 확정 |
| Kruskal MST | `algorithms/graph/KruskalMinimumSpanningTree.java` | 최소 연결 비용 | 간선 정렬 + Union-Find |
| Floyd-Warshall | `algorithms/graph/FloydWarshall.java` | 모든 쌍 최단거리, n 작음 | 중간 노드를 하나씩 허용 |

## 4. 패턴 선택 기준

| 문제 표현 | 선택 |
|---|---|
| "정렬된 배열에서 ..." | 이분 탐색, two pointers |
| "구간 합을 여러 번" | 누적합, Fenwick Tree |
| "연속된 부분 배열/문자열" | Sliding Window |
| "모든 경우를 만들어라" | Backtracking |
| "최소/최대 비용을 누적" | DP 또는 Greedy 검토 |
| "가장 빨리 끝나는 것부터" | Greedy |
| "가장 작은/큰 것을 반복해서 꺼냄" | Heap |
| "무가중치 최단거리" | BFS |
| "양수 가중치 최단거리" | Dijkstra |
| "모든 정점 쌍 최단거리" | Floyd-Warshall |
| "연결 비용 최소" | Kruskal MST |
| "선후 관계" | Topological Sort |
| "부분 문자열 찾기" | KMP |
| "소수를 많이 판별" | Sieve |

## 5. 시험 전 암기 템플릿

### 이분 탐색

```java
int left = 0;
int right = arr.length;
while (left < right) {
    int mid = left + (right - left) / 2;
    if (arr[mid] < target) {
        left = mid + 1;
    } else {
        right = mid;
    }
}
```

### 백트래킹

```java
path.add(choice);
backtrack(nextState);
path.remove(path.size() - 1);
```

### DP

```java
dp[0] = base;
for (state...) {
    for (choice...) {
        dp[state] = best(dp[state], dp[previous] + cost);
    }
}
```

### BFS

```java
queue.offer(start);
visited[start] = true;
while (!queue.isEmpty()) {
    int current = queue.poll();
    for (int next : graph.get(current)) {
        if (!visited[next]) {
            visited[next] = true;
            queue.offer(next);
        }
    }
}
```

## 6. 자주 틀리는 포인트

- `lowerBound`와 `upperBound` 기대값을 헷갈린다.
- 정렬이 필요한 문제인데 원본 순서를 보존해야 하는 조건을 놓친다.
- 백트래킹에서 `path.remove(...)`, `used[i] = false` 원복을 빼먹는다.
- DP의 base case를 잘못 둔다.
- Greedy는 "왜 이 선택이 항상 최적인지" 설명할 수 있어야 한다.
- Dijkstra를 음수 간선에 쓰면 안 된다.
- Floyd-Warshall은 O(n^3)이므로 n이 큰 문제에 쓰면 안 된다.
- KMP의 match 후 `matched = lps[matched - 1]`를 빼먹으면 겹치는 패턴을 놓친다.
- Comparator에서 `a - b`를 쓰면 overflow가 날 수 있다.

## 7. 추천 학습 순서

1. `BinarySearch`, `PrefixSum`, `TwoSum`, `SlidingWindow`.
2. `MergeSort`, `QuickSort`, `CountingSort`.
3. `NextGreaterElement`, `SlidingWindowMaximum`, `TopKElements`.
4. `Permutations`, `CombinationSum`.
5. `CoinChange`, `LongestIncreasingSubsequence`.
6. `GridShortestPathBfs`, `ConnectedComponentsDfs`, `TopologicalSort`.
7. `DijkstraShortestPath`, `KruskalMinimumSpanningTree`, `FloydWarshall`.
8. `KmpSearch`, `SieveOfEratosthenes`, `GcdLcm`.

