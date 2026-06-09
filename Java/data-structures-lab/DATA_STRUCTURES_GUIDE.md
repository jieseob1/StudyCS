# Data Structures Guide

이 문서는 랩에 들어간 자료구조를 빠짐없이 찾기 위한 색인입니다.

## Java 표준 컬렉션 예제

| 주제 | 파일 | 왜 나왔나 | 핵심 성능 |
|---|---|---|---|
| `ArrayList` | `collections/ArrayListExample.java` | 연속 배열로 인덱스 조회와 순회를 빠르게 하기 위해 | `get` O(1), 끝 삽입 amortized O(1), 중간 삽입/삭제 O(n) |
| `Set` | `collections/SetExample.java` | 중복 제거와 포함 여부 확인을 위해 | `HashSet` 평균 O(1), `TreeSet` O(log n) |
| `Deque` | `collections/DequeExample.java` | stack/queue를 양끝 연산으로 표현하기 위해 | 양끝 삽입/삭제 amortized O(1) |
| `PriorityQueue` | `collections/PriorityQueueExample.java` | 최솟값/최댓값을 반복해서 뽑기 위해 | `peek` O(1), `offer/poll` O(log n) |

## Map API 예제

| 주제 | 파일 | 확인할 차이 |
|---|---|---|
| `putIfAbsent` | `examples/map/PutIfAbsentExample.java` | value 인자는 호출 전에 이미 만들어진다. lazy하지 않다. |
| `computeIfAbsent` | `examples/map/ComputeIfAbsentExample.java` | key가 없을 때만 mapping function이 실행된다. grouping에 좋다. |
| `merge` | `examples/map/MergeExample.java` | 없으면 초기값, 있으면 병합한다. 빈도수 세기에 좋다. |
| `compute` | `examples/map/ComputeExample.java` | 항상 remapping function을 실행한다. null 반환 시 mapping이 제거된다. |
| `TreeMap` | `examples/map/TreeMapExample.java` | 정렬된 key에서 `floorKey`, `ceilingKey`를 O(log n)에 찾는다. |
| `LinkedHashMap` LRU | `examples/map/LinkedHashMapLruExample.java` | access-order와 `removeEldestEntry`로 LRU를 만든다. |
| mutable key 문제 | `examples/map/MutableKeyExample.java` | key의 `hashCode/equals` 기준 필드를 바꾸면 조회가 깨질 수 있다. |

## Stream 예제

| 주제 | 파일 | 확인할 동작 |
|---|---|---|
| lazy execution | `examples/stream/LazyExecutionExample.java` | 중간 연산은 최종 연산이 호출될 때 실행된다. |
| `filter/map` | `examples/stream/FilterMapExample.java` | 조건 필터링 후 변환한다. |
| `groupingBy` | `examples/stream/GroupingExample.java` | key별 그룹/집계를 만든다. |
| primitive stream | `examples/stream/PrimitiveStreamExample.java` | `mapToInt`로 boxing을 줄이고 숫자 연산을 쓴다. |
| `toMap` merge | `examples/stream/ToMapMergeExample.java` | 중복 key 처리에는 merge function이 필요하다. |
| `sorted/limit` | `examples/stream/SortedLimitExample.java` | 정렬 후 상위 n개를 뽑는다. 전체 정렬 비용은 O(n log n)이다. |
| `toList` | `examples/stream/ToListExample.java` | `Stream.toList()` 결과는 수정 불가능하다. |

## 직접 구현 자료구조

| 자료구조 | 파일 | 왜 나왔나 | 핵심 불변식 | 성능 |
|---|---|---|---|---|
| 단일 연결 리스트 | `structures/linear/SinglyLinkedList.java` | 노드 연결만 바꿔 앞쪽 삽입/삭제를 빠르게 하기 위해 | 각 노드는 다음 노드만 가리킨다 | 앞 삽입/삭제 O(1), 검색 O(n) |
| Binary Min Heap | `structures/heap/BinaryMinHeap.java` | 최솟값을 빠르게 반복 추출하기 위해 | 부모 값 <= 자식 값 | `peek` O(1), `offer/poll` O(log n) |
| Binary Search Tree | `structures/tree/BinarySearchTree.java` | 정렬 기준으로 탐색 범위를 반씩 줄이기 위해 | 왼쪽 < 현재 < 오른쪽 | 평균 O(log n), 편향 시 O(n) |
| 인접 리스트 그래프 | `structures/graph/AdjacencyListGraph.java` | sparse graph를 메모리 효율적으로 표현하기 위해 | 각 정점은 연결된 이웃 목록을 가진다 | 메모리 O(V+E), BFS/DFS O(V+E) |
| Trie | `structures/trie/Trie.java` | prefix 검색을 문자열 길이만큼에 처리하기 위해 | 문자 경로가 prefix를 표현한다 | 삽입/검색 O(L) |
| Union-Find | `structures/unionfind/DisjointSet.java` | 그룹 병합과 같은 그룹 여부 확인을 빠르게 하기 위해 | 각 집합은 대표 root를 가진다 | 경로 압축 + size 기준 병합 시 거의 O(1) |
| Fenwick Tree | `structures/range/FenwickTree.java` | 점 업데이트와 구간 합을 빠르게 처리하기 위해 | `i & -i` 크기 구간 합을 저장한다 | update/query O(log n) |
| Segment Tree | `structures/range/SegmentTree.java` | 다양한 구간 query/update를 처리하기 위해 | 부모는 자식 구간의 병합값이다 | build O(n), update/query O(log n) |
| LRU Cache | `structures/cache/LruCache.java` | 용량 초과 시 가장 오래 안 쓴 값을 제거하기 위해 | 접근 순서가 최신순으로 갱신된다 | `get/put` 평균 O(1) |
| BitSet | `structures/bit/BitSetExample.java` | boolean 상태를 bit 단위로 압축 저장하기 위해 | bit index가 상태를 표현한다 | set/get O(1), 메모리 compact |

## 면접 알고리즘 패턴

| 패턴 | 파일 | 언제 쓰나 | 자료구조 |
|---|---|---|---|
| Merge Sort | `algorithms/sorting/MergeSort.java` | 안정 정렬, divide and conquer를 설명해야 할 때 | 배열, 보조 배열 |
| Quick Sort | `algorithms/sorting/QuickSort.java` | pivot partition을 설명해야 할 때 | 배열 |
| Counting Sort | `algorithms/sorting/CountingSort.java` | 값 범위가 작고 정수일 때 | count 배열 |
| Binary Search | `algorithms/search/BinarySearch.java` | 정렬 배열에서 값/경계를 찾을 때 | 배열 |
| Prefix Sum | `algorithms/arrayhash/PrefixSum.java` | 구간 합 질의가 반복될 때 | 배열 |
| Two Sum | `algorithms/arrayhash/TwoSum.java` | 보수 값이 이전에 나왔는지 빠르게 찾을 때 | `HashMap` |
| Sliding Window | `algorithms/arrayhash/LongestSubstringWithoutRepeating.java` | 연속 구간 조건을 유지하며 확장/축소할 때 | `HashMap`, two pointers |
| Backtracking Permutation | `algorithms/backtracking/Permutations.java` | 모든 순서를 만들어야 할 때 | 재귀, used 배열 |
| Backtracking Combination | `algorithms/backtracking/CombinationSum.java` | target을 만드는 조합을 찾아야 할 때 | 재귀, path |
| Coin Change DP | `algorithms/dp/CoinChange.java` | 최소 동전 수처럼 최적 부분 구조가 있을 때 | DP 배열 |
| LIS DP | `algorithms/dp/LongestIncreasingSubsequence.java` | 증가 부분 수열 길이를 구할 때 | tails 배열, 이분 탐색 |
| Greedy Activity Selection | `algorithms/greedy/ActivitySelection.java` | 겹치지 않는 구간을 최대 선택할 때 | 정렬 |
| Monotonic Stack | `algorithms/stackqueue/NextGreaterElement.java` | 다음 큰 값/작은 값을 찾을 때 | `ArrayDeque` |
| Monotonic Deque | `algorithms/stackqueue/SlidingWindowMaximum.java` | window 최댓값/최솟값을 매번 알아야 할 때 | `ArrayDeque` |
| Heap Scheduling | `algorithms/heap/MeetingRooms.java` | 가장 빨리 끝나는 작업을 계속 확인할 때 | `PriorityQueue` |
| Top-K | `algorithms/heap/TopKElements.java` | 전체 정렬 없이 상위 k개만 필요할 때 | `PriorityQueue` |
| Sieve | `algorithms/math/SieveOfEratosthenes.java` | 여러 소수를 한 번에 구할 때 | boolean 배열 |
| GCD/LCM | `algorithms/math/GcdLcm.java` | 최대공약수/최소공배수를 구할 때 | 유클리드 호제법 |
| KMP | `algorithms/string/KmpSearch.java` | 문자열 패턴 검색을 빠르게 해야 할 때 | LPS 배열 |
| BFS 최단거리 | `algorithms/graph/GridShortestPathBfs.java` | 무가중치 최단거리를 구할 때 | `Queue` |
| DFS 연결 요소 | `algorithms/graph/ConnectedComponentsDfs.java` | 연결 요소 개수를 구할 때 | 재귀, 방문 배열 |
| 위상 정렬 | `algorithms/graph/TopologicalSort.java` | 선후 관계 순서를 만들 때 | `Queue`, indegree |
| Dijkstra | `algorithms/graph/DijkstraShortestPath.java` | 양수 가중치 최단거리를 구할 때 | `PriorityQueue` |
| Kruskal MST | `algorithms/graph/KruskalMinimumSpanningTree.java` | 최소 연결 비용을 구할 때 | Union-Find |
| Floyd-Warshall | `algorithms/graph/FloydWarshall.java` | 모든 쌍 최단거리를 구할 때 | 2D DP |

## 빠르게 고르는 기준

| 요구사항 | 먼저 볼 구조 |
|---|---|
| key로 빠르게 찾기 | `HashMap`, `HashSet` |
| 중복 제거 | `HashSet`, `LinkedHashSet`, `TreeSet` |
| 삽입 순서 유지 | `LinkedHashMap`, `LinkedHashSet` |
| 정렬된 key의 이전/다음 | `TreeMap`, `TreeSet` |
| stack/queue | `ArrayDeque` |
| 최솟값/최댓값 반복 추출 | `PriorityQueue`, heap |
| prefix 검색 | Trie |
| 연결 여부/그룹 병합 | Union-Find |
| 구간 합 + 점 업데이트 | Fenwick Tree |
| 구간 query 일반화 | Segment Tree |
| 최근 사용 기준 cache | LRU Cache |
| sparse boolean state | BitSet |
