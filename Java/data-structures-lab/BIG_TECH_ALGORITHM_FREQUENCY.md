# Big Tech Algorithm Frequency Guide

이 문서는 빅테크 코딩 인터뷰와 온라인 코딩 테스트를 대비하기 위한 빈출 패턴 정리입니다.

정확한 회사별 출제 확률은 대부분 비공개이거나 LeetCode Premium 같은 유료 company tag에 의존합니다. 따라서 여기서는 공개 자료에서 반복적으로 강조되는 주제와 공개 curated list의 구성 패턴을 기준으로 빈출도를 추정합니다.

## 근거로 본 공개 자료

- Tech Interview Handbook 3개월 플랜은 `Array`, `String`, `Sorting and searching`, `Matrix`, `Tree`, `Graph`를 High priority로 둡니다.
  - https://www.techinterviewhandbook.org/coding-interview-study-plan/
- Grind 75 FAQ는 Blind 75, LeetCode Patterns, EPI 등에서 문제 풀을 만들고 LeetCode의 popularity, frequency, companies, likes, topic coverage를 보고 top questions를 추렸다고 설명합니다.
  - https://www.techinterviewhandbook.org/grind75/faq
- LeetCode Top Interview 150 공개 색인은 string/two pointers, graph BFS/DFS/Union-Find, topological sort, heap, DP, greedy, matrix 등 주제가 반복 등장합니다.
  - https://leetcode-top-interview-150.github.io/
- Amazon 공식 interview prep topic은 software development interview에서 data structures, algorithms, coding을 명시합니다.
  - https://www.amazon.jobs/content/en/how-we-hire/interview-prep/software-development-topics
- Google SWE prep guide는 sorting/searching/binary search, divide-and-conquer, DP/memoization, greedy, recursion, Dijkstra, arrays, linked lists, stacks, queues, hash maps, trees, heaps, graphs를 준비 대상으로 언급합니다.
  - https://soft-eng-practicum.github.io/assets/pdfs/Google%20Interview%20Prep%20Guide%20SWE%20.pdf

## 빈출도 등급

| 등급 | 의미 |
|---|---|
| S | 거의 모든 빅테크 코딩 대비에서 최우선. 못하면 합격 가능성이 크게 떨어짐 |
| A | 매우 자주 나옴. onsite/phone screen 모두 대비 필요 |
| B | 빈출이지만 회사/레벨/라운드에 따라 편차 있음 |
| C | 나오면 어렵게 느껴질 수 있으나 상대적으로 출제 빈도는 낮음 |

## S급: 반드시 손에 익혀야 하는 패턴

| 패턴 | 대비 파일 | 대표 문제 유형 | 왜 빈출인가 |
|---|---|---|---|
| Array/String 기본 조작 | `collections/ArrayListExample.java`, `examples/stream/*` | reverse, rotate, parse, normalize, count | 구현력과 edge case를 빠르게 확인하기 좋음 |
| HashMap/HashSet | `examples/map/*`, `algorithms/arrayhash/TwoSum.java` | two-sum, anagram, frequency, grouping | O(n^2)을 O(n)으로 줄이는 대표 도구 |
| Two Pointers | `algorithms/search/BinarySearch.java` 참고 후 추가 연습 | sorted two-sum, palindrome, remove duplicate | 정렬 배열/문자열 문제에 자주 붙음 |
| Sliding Window | `algorithms/arrayhash/LongestSubstringWithoutRepeating.java` | longest substring, min window, subarray condition | 연속 구간 문제의 핵심 패턴 |
| Binary Search | `algorithms/search/BinarySearch.java` | exact search, lower/upper bound, answer search | 정렬/monotonic 조건을 O(log n)으로 처리 |
| BFS/DFS | `algorithms/graph/GridShortestPathBfs.java`, `ConnectedComponentsDfs.java` | island, maze, graph traversal, shortest path | 그래프/트리/격자 문제의 기본 |
| Tree Traversal | `structures/tree/BinarySearchTree.java` | inorder/preorder/postorder, BST, LCA | 재귀와 자료구조 이해를 함께 확인 |
| Sorting + Greedy 기초 | `algorithms/sorting/*`, `algorithms/greedy/ActivitySelection.java` | intervals, meeting rooms, merge intervals | 전처리 정렬 후 단순화되는 문제가 많음 |

## A급: 빅테크 대비에서 반드시 풀어봐야 하는 패턴

| 패턴 | 대비 파일 | 대표 문제 유형 | 비고 |
|---|---|---|---|
| Heap/PriorityQueue | `algorithms/heap/*`, `structures/heap/BinaryMinHeap.java` | top-k, k-way merge, meeting rooms, stream median | Amazon/Google류 최적화 질문에 자주 연결 |
| Topological Sort | `algorithms/graph/TopologicalSort.java` | course schedule, dependency order | BFS/DFS와 함께 그래프 기본 세트 |
| Dijkstra | `algorithms/graph/DijkstraShortestPath.java` | 양수 가중치 최단거리 | Google prep guide도 복잡 알고리즘 예로 언급 |
| Union-Find | `structures/unionfind/DisjointSet.java`, `algorithms/graph/KruskalMinimumSpanningTree.java` | connected components, cycle, MST | graph/grid 문제의 대안 풀이로 자주 등장 |
| Backtracking | `algorithms/backtracking/*` | permutations, combinations, subsets, word search | 완전탐색을 깔끔하게 구현하는지 확인 |
| 1D DP | `algorithms/dp/CoinChange.java`, `LongestIncreasingSubsequence.java` | coin change, house robber, LIS | medium 난이도에서 자주 등장 |
| Stack/Monotonic Stack | `algorithms/stackqueue/NextGreaterElement.java` | next greater, daily temperatures, histogram | 패턴을 모르면 어렵고, 알면 빠름 |
| Prefix Sum | `algorithms/arrayhash/PrefixSum.java` | range sum, subarray sum, matrix sum | HashMap과 결합하면 빈출도가 높음 |

## B급: 준비하면 변별력이 생기는 패턴

| 패턴 | 대비 파일 | 대표 문제 유형 | 비고 |
|---|---|---|---|
| Trie | `structures/trie/Trie.java` | autocomplete, word dictionary, prefix search | 검색/문자열 쪽에서 출제 |
| KMP/String Matching | `algorithms/string/KmpSearch.java` | repeated pattern, substring search | 일반 SWE보다는 문자열 집중 라운드에서 유리 |
| Segment Tree/Fenwick Tree | `structures/range/*` | dynamic range query | 일반 빅테크 인터뷰보다는 OA/고난도에서 등장 |
| Floyd-Warshall | `algorithms/graph/FloydWarshall.java` | all-pairs shortest path, small n | n이 작고 모든 쌍이면 선택 |
| BitSet/Bitmask | `structures/bit/BitSetExample.java` | subset state, visited compression | n이 작거나 상태 압축 문제 |
| Counting Sort | `algorithms/sorting/CountingSort.java` | small integer range, bucket/frequency | frequency 문제와 결합 |
| Math/Sieve/GCD | `algorithms/math/*` | prime, gcd/lcm, modular thinking | 회사/라운드별 편차 있음 |

## C급: 시간이 남으면 보는 패턴

| 패턴 | 이유 |
|---|---|
| Geometry | 특정 회사/팀 외에는 빈도가 낮음 |
| Advanced DP, interval DP, tree DP | 고난도 라운드나 competitive-style 문제에서 유리 |
| A* | Google guide에서 언급되지만 일반 인터뷰에서는 Dijkstra보다 훨씬 덜 나옴 |
| Balanced Tree 직접 구현 | 개념은 중요하지만 Java 인터뷰에서는 `TreeMap`, `TreeSet` 활용이 더 현실적 |

## 회사별 대비 감각

| 회사/유형 | 우선순위 |
|---|---|
| Google | 그래프, 트리, DP, 이분 탐색, 정렬/탐색, 해시, 복잡도 설명 |
| Meta | 배열/문자열, 해시, BFS/DFS, 트리, heap/top-k, 빠른 구현과 dry-run |
| Amazon | 자료구조/알고리즘 기본기, 해시, heap, graph, OOD/behavioral 병행 |
| Microsoft | 배열/문자열, 트리, 그래프, DP, 설계 사고와 edge case |
| Apple | 구현 정확도, 자료구조 기본기, 문자열/배열/트리, role-specific 지식 |
| Online Assessment | 문자열/배열, prefix sum, sorting, greedy, heap, DP, graph shortest path |

## 4주 압축 대비 순서

### Week 1: S급 기본기

- `TwoSum`
- `PrefixSum`
- `LongestSubstringWithoutRepeating`
- `BinarySearch`
- `MergeSort`, `QuickSort`
- `GridShortestPathBfs`, `ConnectedComponentsDfs`

### Week 2: 그래프/트리/heap

- `TopologicalSort`
- `DijkstraShortestPath`
- `KruskalMinimumSpanningTree`
- `BinarySearchTree`
- `TopKElements`
- `MeetingRooms`
- `BinaryMinHeap`

### Week 3: DP/Backtracking/String

- `CoinChange`
- `LongestIncreasingSubsequence`
- `Permutations`
- `CombinationSum`
- `KmpSearch`
- `Trie`

### Week 4: 변별력과 복습

- `FenwickTree`
- `SegmentTree`
- `FloydWarshall`
- `BitSetExample`
- `SieveOfEratosthenes`
- `GcdLcm`
- 기존 테스트를 안 보고 다시 구현

## 패턴별 문제를 보고 고르는 법

| 문제 힌트 | 선택 |
|---|---|
| "가장 긴/짧은 연속 부분 문자열" | Sliding Window |
| "정렬되어 있다" | Binary Search 또는 Two Pointers |
| "구간 합 여러 번" | Prefix Sum, Fenwick Tree |
| "가장 작은/큰 값을 계속 꺼낸다" | Heap |
| "의존 관계/선수 과목" | Topological Sort |
| "무가중치 최단거리" | BFS |
| "양수 가중치 최단거리" | Dijkstra |
| "연결 여부를 여러 번 묻는다" | Union-Find |
| "모든 경우/모든 조합" | Backtracking |
| "최소/최대 비용이 누적된다" | DP |
| "겹치지 않게 최대 선택" | Greedy + sort by end |
| "prefix로 단어 검색" | Trie |

## 현재 랩 커버리지 평가

| 영역 | 상태 |
|---|---|
| S급 패턴 | 대부분 코드 있음. Two Pointers 전용 파일은 추가 후보 |
| A급 패턴 | Heap, Topological Sort, Dijkstra, Union-Find, Backtracking, DP 포함 |
| B급 패턴 | Trie, KMP, Fenwick, Segment Tree, Floyd-Warshall, BitSet 포함 |
| C급 패턴 | Geometry, advanced DP, A*는 아직 제외 |

