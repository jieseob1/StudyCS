# Java Data Structures Lab

실제로 컴파일하고 실행하면서 Java 컬렉션, `Map` API, Stream, 면접 자료구조를 볼 수 있는 콘솔 예제입니다.

## 실행

프로젝트 루트에서:

```bash
cd Java/data-structures-lab
./run.sh
```

직접 명령으로 실행하려면:

```bash
rm -rf out
mkdir -p out
javac -d out $(find src test -name '*.java')
java -cp out studycs.javalab.StudyCsLabTest
java -cp out studycs.javalab.Main
```

## 볼 포인트

- `src/studycs/javalab/collections`: `ArrayList`, `Set`, `Deque`, `PriorityQueue` 예제.
- `src/studycs/javalab/examples/map`: `putIfAbsent`, `computeIfAbsent`, `merge`, `compute`, `TreeMap`, `LinkedHashMap` LRU, mutable key 예제.
- `src/studycs/javalab/examples/stream`: lazy execution, `filter/map`, `groupingBy`, primitive stream, `toMap` merge, `sorted/limit`, `toList` 예제.
- `src/studycs/javalab/examples/standard`: String, Objects, Optional, Arrays, Collections, Comparator, java.time, Regex, BigDecimal, EnumSet/EnumMap, Base64, Path 예제.
- `src/studycs/javalab/algorithms`: sorting, search, array/hash, stack/queue, heap, backtracking, DP, greedy, graph, math, string 패턴별 알고리즘.
- `src/studycs/javalab/structures`: linear, heap, tree, graph, trie, union-find, range, cache, bit 구조 구현.

`CollectionExamples`, `MapApiExamples`, `StreamExamples`, `InterviewAlgorithms`는 실행 순서를 모아주는 목차 역할만 합니다. 실제 공부할 코드는 위 패키지의 개별 클래스를 보면 됩니다.

## 자세한 색인

- [폴더 구조](./FOLDER_STRUCTURE.md)
- [자료구조/알고리즘 전체 가이드](./DATA_STRUCTURES_GUIDE.md)
- [시험 대비 알고리즘 가이드](./ALGORITHM_EXAM_GUIDE.md)
- [빅테크 빈출 알고리즘 가이드](./BIG_TECH_ALGORITHM_FREQUENCY.md)
- [Java 표준 유틸 가이드](./JAVA_STANDARD_UTILITIES_GUIDE.md)
