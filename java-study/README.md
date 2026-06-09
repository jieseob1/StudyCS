# Java Study Examples

자바 자료구조의 동작 차이와 코딩테스트 알고리즘 패턴을 콘솔 trace로 확인하는 학습용 코드입니다.

## 실행

```bash
./java-study/scripts/run-all.sh
```

스크립트는 다음 순서로 실행됩니다.

1. `src/main/java`, `src/test/java` 전체 컴파일
2. `JavaStudyAssertions`로 핵심 동작 검증
3. 자료구조 데모 출력
4. 알고리즘 패턴 데모 출력

외부 라이브러리는 사용하지 않습니다. `record`, `List.of`를 사용하므로 Java 17 이상을 권장합니다.

## 자료구조 예제

위치: `src/main/java/com/studycs/datastructures`

### `MapOperationLab`

`Map` 계열 메서드의 차이를 실제 호출 로그로 보여줍니다.

- `computeIfAbsent`
  - key가 없거나 value가 `null`일 때만 mapping function 실행
  - 이미 값이 있으면 function 자체가 호출되지 않음
- `putIfAbsent`
  - key가 없거나 value가 `null`일 때만 새 값 저장
  - 함수 호출 개념이 없고, 반환값으로 이전 값을 확인
- `compute`
  - key 존재 여부와 상관없이 remapping function 실행
  - remapping 결과가 `null`이면 entry 삭제
- `merge`
  - key가 없거나 value가 `null`이면 전달한 value를 바로 저장
  - 기존 값이 있을 때만 remapping function 실행

### `ToyHashMap`

학습용으로 단순화한 HashMap입니다.

- `hashCode()`로 bucket index 계산
- 같은 bucket 안에서는 linked list로 collision 처리
- key 비교는 `equals()`로 수행
- load factor threshold를 넘으면 table 크기 2배 resize

실제 JDK `HashMap`은 tree bin, fail-fast iterator, view collection 등 더 많은 최적화와 계약을 갖습니다. 이 코드는 내부 흐름을 보기 위한 축소 모델입니다.

### `HashStructureLab`

고의로 같은 `hashCode()`를 반환하는 `BadHashKey`를 사용합니다.

- `HashSet`이 중복을 막을 때 `hashCode()` 후 `equals()`를 사용하는 흐름
- `ToyHashMap`에서 collision bucket이 어떻게 쌓이고 resize 후에도 조회되는지 확인

### 그 외 데모

`DataStructureDemo`에서 아래 구조의 기본 성질도 함께 출력합니다.

- `TreeSet`: 정렬 + 중복 제거
- `PriorityQueue`: 기본 min-heap
- `ArrayDeque`: queue와 stack 양쪽 패턴

## 알고리즘 패턴 예제

위치: `src/main/java/com/studycs/algorithms`

`PatternExamples`에 코딩테스트에서 자주 쓰는 패턴을 작은 함수로 모았습니다.

- Two pointers: 정렬 배열에서 target pair 찾기
- Sliding window: 고정 길이 구간 최대합
- Prefix sum: inclusive range sum
- Binary search lower bound: 첫 `value >= target` 위치
- BFS: unweighted graph 최단 거리
- DFS: connected components
- Backtracking: subsets
- Dynamic programming: coin change minimum coins
- Greedy: activity selection
- Heap: top K largest
- Union-Find: connectivity
- Dijkstra: weighted graph shortest path

각 함수는 결과만 반환하지 않고 `trace`도 같이 반환하도록 구성했습니다. 처음 볼 때는 `AlgorithmPatternDemo` 출력을 보고, 익숙해지면 `PatternExamples` 메서드를 직접 바꿔보면 됩니다.

## 빠르게 볼 파일

- `src/test/java/com/studycs/JavaStudyAssertions.java`: 기대 동작을 가장 압축해서 확인
- `src/main/java/com/studycs/datastructures/MapOperationLab.java`: `computeIfAbsent`, `putIfAbsent`, `compute`, `merge` 비교
- `src/main/java/com/studycs/datastructures/ToyHashMap.java`: HashMap 축소 구현
- `src/main/java/com/studycs/algorithms/PatternExamples.java`: 알고리즘 패턴 모음
