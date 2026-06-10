# Java Study Notes

이 디렉터리는 Java로 자료구조와 컬렉션 API를 공부하기 위한 정리 문서입니다.

## 읽는 순서

1. [Java Data Structures Lab](./data-structures-lab)
   - 실제로 컴파일하고 실행하면서 보는 Java 예제 코드
   - 컬렉션, Map API, Stream, 면접 알고리즘, 직접 구현 자료구조
   - 주제별로 클래스를 나눠서 개별 파일 단위로 확인 가능
   - [폴더 구조](./data-structures-lab/FOLDER_STRUCTURE.md), [자료구조 전체 가이드](./data-structures-lab/DATA_STRUCTURES_GUIDE.md), [시험 대비 알고리즘 가이드](./data-structures-lab/ALGORITHM_EXAM_GUIDE.md), [빅테크 빈출 알고리즘 가이드](./data-structures-lab/BIG_TECH_ALGORITHM_FREQUENCY.md), [Java 표준 유틸 가이드](./data-structures-lab/JAVA_STANDARD_UTILITIES_GUIDE.md)

2. [Java 컬렉션과 Stream 동작 정리](./java-collections-and-streams.md)
   - `List`, `Set`, `Map`, `Queue`, `Deque`, `PriorityQueue`
   - `HashMap`, `TreeMap`, `LinkedHashMap`, `ConcurrentHashMap`
   - `computeIfAbsent`, `putIfAbsent`, `compute`, `merge` 차이
   - Stream의 지연 실행, 중간 연산, 최종 연산, 병렬 Stream 주의점

3. [이직/면접 자료구조 정리](./interview-data-structures.md)
   - 배열, 문자열, 연결 리스트, 스택, 큐, 해시, 힙, 트리, 그래프
   - Trie, Union-Find, Fenwick Tree, Segment Tree, LRU Cache
   - 문제를 보고 어떤 자료구조를 떠올릴지, 왜 그 자료구조가 필요한지
   - Java 구현 템플릿과 자주 틀리는 포인트

## 학습 기준

자료구조를 외울 때는 이름보다 아래 질문에 답할 수 있어야 합니다.

- 어떤 연산을 빠르게 하려고 나온 구조인가?
- 내부적으로 어떤 불변식을 유지하는가?
- 삽입, 삭제, 조회, 순회, 정렬 비용은 얼마인가?
- Java에서 어떤 클래스로 쓰는가?
- 면접 문제에서는 어떤 신호가 나오면 이 자료구조를 떠올려야 하는가?
