# Java Standard Utilities Guide

이 문서는 `Map`, `Stream` 외에 실무와 코딩 테스트에서 자주 쓰는 Java 표준 유틸 API를 정리합니다.

## 전체 색인

| 주제 | 파일 | 자주 쓰는 API | 언제 쓰나 |
|---|---|---|---|
| String/StringBuilder | `examples/standard/StringUtilitiesExample.java` | `trim`, `replaceAll`, `toLowerCase`, `String.join`, `StringBuilder` | 입력 정규화, 문자열 조립 |
| Objects | `examples/standard/ObjectsUtilityExample.java` | `Objects.equals`, `requireNonNullElse`, `hash` | null-safe 비교, 기본값, `hashCode` 구현 |
| Optional | `examples/standard/OptionalExample.java` | `ofNullable`, `map`, `filter`, `orElse` | null 가능 값을 짧은 변환 파이프라인으로 처리 |
| Arrays | `examples/standard/ArraysUtilityExample.java` | `copyOf`, `sort`, `binarySearch`, `deepToString` | 배열 복사/정렬/검색/출력 |
| Collections | `examples/standard/CollectionsUtilityExample.java` | `reverse`, `frequency`, `unmodifiableList` | 리스트 보조 연산, 불변 view |
| Comparator | `examples/standard/ComparatorExample.java` | `comparingInt`, `reversed`, `thenComparing` | 복합 정렬 조건 |
| java.time | `examples/standard/DateTimeExample.java` | `LocalDate`, `YearMonth`, `ChronoUnit` | 날짜 계산, 날짜 파싱/표현 |
| Regex | `examples/standard/RegexExample.java` | `Pattern`, `Matcher`, `find`, `group` | 반복 패턴 추출 |
| BigDecimal | `examples/standard/BigDecimalExample.java` | `add`, `divide`, `setScale`, `RoundingMode` | 돈/정밀 소수 계산 |
| Enum utilities | `examples/standard/EnumUtilitiesExample.java` | `EnumSet`, `EnumMap` | enum key/flag를 효율적으로 관리 |
| Base64/UUID | `examples/standard/Base64UuidExample.java` | `Base64`, `UUID.nameUUIDFromBytes` | 인코딩/디코딩, 안정 UUID |
| Path | `examples/standard/PathUtilityExample.java` | `Path.of`, `normalize`, `getFileName` | 파일 경로 조립/정규화 |

## 우선순위

| 우선순위 | 먼저 익힐 것 |
|---|---|
| 1 | `String`, `Arrays`, `Collections`, `Comparator` |
| 2 | `Objects`, `Optional`, `java.time`, `Pattern/Matcher` |
| 3 | `BigDecimal`, `EnumSet/EnumMap`, `Base64`, `Path` |

## 자주 틀리는 포인트

- `String.toLowerCase()`는 locale 영향을 받을 수 있으므로 고정 처리에는 `Locale.ROOT`를 사용한다.
- `Arrays.binarySearch`는 정렬된 배열에서만 의미 있다.
- `Collections.unmodifiableList`는 불변 복사본이 아니라 수정 불가능 view다. 여기 예제는 원본 영향 방지를 위해 새 `ArrayList`를 감싼다.
- `Comparator`에서 `(a, b) -> a - b`는 overflow 위험이 있다. `comparingInt`, `Integer.compare`를 쓴다.
- `Optional`은 반환값/중간 변환에 적합하지만 필드나 파라미터에 남발하지 않는다.
- `BigDecimal`은 `new BigDecimal("0.10")`처럼 문자열 생성자를 쓰는 것이 안전하다.
- 날짜는 `Date`, `Calendar`보다 `java.time`을 우선한다.
- 정규식은 반복 사용 시 `Pattern`을 재사용한다.
- 경로 문자열을 직접 `split("/")`하기보다 `Path` API를 쓴다.

## 실행

```bash
cd Java/data-structures-lab
./run.sh
```

출력 중 `== Java standard utility demo ==` 섹션을 보면 각 API의 동작을 확인할 수 있습니다.

