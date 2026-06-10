# Java Data Structures Lab Folder Structure

이 랩은 "주제별로 한 파일씩 열어보기"를 기준으로 구성합니다.

```text
Java/data-structures-lab
├── README.md
├── FOLDER_STRUCTURE.md
├── DATA_STRUCTURES_GUIDE.md
├── ALGORITHM_EXAM_GUIDE.md
├── JAVA_STANDARD_UTILITIES_GUIDE.md
├── run.sh
├── src/studycs/javalab
│   ├── Main.java
│   ├── CollectionExamples.java
│   ├── ExamAlgorithms.java
│   ├── InterviewAlgorithms.java
│   ├── StructureExamples.java
│   ├── collections
│   │   ├── ArrayListExample.java
│   │   ├── DequeExample.java
│   │   ├── PriorityQueueExample.java
│   │   └── SetExample.java
│   ├── examples
│   │   ├── map
│   │   │   ├── ComputeExample.java
│   │   │   ├── ComputeIfAbsentExample.java
│   │   │   ├── LinkedHashMapLruExample.java
│   │   │   ├── MergeExample.java
│   │   │   ├── MutableKeyExample.java
│   │   │   ├── PutIfAbsentExample.java
│   │   │   └── TreeMapExample.java
│   │   ├── standard
│   │   │   ├── ArraysUtilityExample.java
│   │   │   ├── Base64UuidExample.java
│   │   │   ├── BigDecimalExample.java
│   │   │   ├── CollectionsUtilityExample.java
│   │   │   ├── ComparatorExample.java
│   │   │   ├── DateTimeExample.java
│   │   │   ├── EnumUtilitiesExample.java
│   │   │   ├── ObjectsUtilityExample.java
│   │   │   ├── OptionalExample.java
│   │   │   ├── PathUtilityExample.java
│   │   │   ├── RegexExample.java
│   │   │   └── StringUtilitiesExample.java
│   │   └── stream
│   │       ├── FilterMapExample.java
│   │       ├── GroupingExample.java
│   │       ├── LazyExecutionExample.java
│   │       ├── PrimitiveStreamExample.java
│   │       ├── SortedLimitExample.java
│   │       ├── ToListExample.java
│   │       ├── ToMapMergeExample.java
│   │       └── User.java
│   ├── algorithms
│   │   ├── arrayhash
│   │   │   ├── PrefixSum.java
│   │   │   ├── LongestSubstringWithoutRepeating.java
│   │   │   └── TwoSum.java
│   │   ├── backtracking
│   │   │   ├── CombinationSum.java
│   │   │   └── Permutations.java
│   │   ├── dp
│   │   │   ├── CoinChange.java
│   │   │   └── LongestIncreasingSubsequence.java
│   │   ├── graph
│   │   │   ├── ConnectedComponentsDfs.java
│   │   │   ├── DijkstraShortestPath.java
│   │   │   ├── FloydWarshall.java
│   │   │   ├── GridShortestPathBfs.java
│   │   │   ├── KruskalMinimumSpanningTree.java
│   │   │   └── TopologicalSort.java
│   │   ├── greedy
│   │   │   └── ActivitySelection.java
│   │   ├── heap
│   │   │   ├── MeetingRooms.java
│   │   │   └── TopKElements.java
│   │   ├── math
│   │   │   ├── GcdLcm.java
│   │   │   └── SieveOfEratosthenes.java
│   │   ├── search
│   │   │   └── BinarySearch.java
│   │   ├── sorting
│   │   │   ├── CountingSort.java
│   │   │   ├── MergeSort.java
│   │   │   └── QuickSort.java
│   │   ├── stackqueue
│   │   │   ├── NextGreaterElement.java
│   │   │   └── SlidingWindowMaximum.java
│   │   └── string
│   │       └── KmpSearch.java
│   └── structures
│       ├── bit
│       │   └── BitSetExample.java
│       ├── cache
│       │   └── LruCache.java
│       ├── graph
│       │   └── AdjacencyListGraph.java
│       ├── heap
│       │   └── BinaryMinHeap.java
│       ├── linear
│       │   └── SinglyLinkedList.java
│       ├── range
│       │   ├── FenwickTree.java
│       │   └── SegmentTree.java
│       ├── tree
│       │   └── BinarySearchTree.java
│       ├── trie
│       │   └── Trie.java
│       └── unionfind
│           └── DisjointSet.java
└── test/studycs/javalab
    └── StudyCsLabTest.java
```

## 역할 구분

| 위치 | 역할 |
|---|---|
| `collections` | Java 표준 컬렉션이 어떻게 보이는지 실행으로 확인 |
| `examples/map` | `Map`의 비슷해 보이는 API 차이를 코드로 확인 |
| `examples/stream` | Stream pipeline, collector, primitive stream, `toList` 차이 확인 |
| `examples/standard` | Java 표준 유틸 API를 코드로 확인 |
| `algorithms/*` | 시험/이직 문제 풀이 패턴별 알고리즘 |
| `structures/*` | 자료구조 자체를 직접 구현해서 내부 동작 확인 |
| `StudyCsLabTest` | 모든 예제와 자료구조가 기대대로 동작하는지 검증 |

## 읽는 순서

1. `collections`: Java 기본 컬렉션 감각 잡기.
2. `examples/map`: 실무에서 헷갈리는 `Map` API 차이 보기.
3. `examples/stream`: Stream의 지연 실행과 collector 익히기.
4. `structures`: 자료구조 내부 구현 보기.
5. `algorithms`: 시험 문제에서 패턴을 선택하는 방식 보기.
6. `StudyCsLabTest`: 각 클래스의 기대 동작을 테스트 관점으로 확인하기.
