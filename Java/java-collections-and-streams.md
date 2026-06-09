# Java 컬렉션과 Stream 동작 정리

## 1. 컬렉션을 볼 때의 기준

Java 컬렉션은 단순히 저장소가 아니라 "어떤 연산을 빠르게 할 것인가"에 따라 선택해야 한다.

| 기준 | 확인할 질문 |
|---|---|
| 중복 | 같은 값을 여러 번 허용해야 하는가? |
| 순서 | 삽입 순서, 정렬 순서, 접근 순서가 필요한가? |
| 조회 | 인덱스 조회가 많은가, key 조회가 많은가? |
| 삭제 | 중간 삭제가 많은가, 끝 삭제가 많은가? |
| 정렬 | 매번 정렬된 상태가 필요한가, 마지막에만 정렬하면 되는가? |
| 동시성 | 여러 스레드가 동시에 접근하는가? |
| null | null key/value를 저장해야 하는가? |
| key 안정성 | key 객체의 `equals`, `hashCode` 값이 저장 후 바뀌지 않는가? |

중요한 전제:

- `List`는 순차 컬렉션이다. 중복을 허용하고 인덱스가 있다.
- `Set`은 중복 제거 컬렉션이다. 같은 원소를 한 번만 가진다.
- `Map`은 key로 value를 찾는 구조다. key는 중복될 수 없다.
- `Queue`는 처리 순서를 관리한다.
- `Deque`는 양쪽 끝에서 넣고 뺄 수 있다.
- `Stream`은 자료구조가 아니라 데이터 처리 파이프라인이다.

## 2. 시간 복잡도 요약

평균 복잡도와 최악 복잡도는 다를 수 있다. 특히 해시 기반 컬렉션은 `hashCode` 품질과 충돌에 영향을 받는다.

| 구조 | 조회 | 삽입 | 삭제 | 순서 | 주요 용도 |
|---|---:|---:|---:|---|---|
| `ArrayList` | 인덱스 O(1), 값 검색 O(n) | 끝 O(1) amortized, 중간 O(n) | 중간 O(n) | 삽입 순서 | 일반적인 순차 목록 |
| `LinkedList` | O(n) | 노드 위치를 알면 O(1), 찾으면 O(n) | 노드 위치를 알면 O(1), 찾으면 O(n) | 삽입 순서 | Java에서는 보통 권장 빈도 낮음 |
| `HashSet` | 평균 O(1) | 평균 O(1) | 평균 O(1) | 없음 | 중복 제거, 포함 여부 |
| `LinkedHashSet` | 평균 O(1) | 평균 O(1) | 평균 O(1) | 삽입 순서 | 순서 유지 중복 제거 |
| `TreeSet` | O(log n) | O(log n) | O(log n) | 정렬 순서 | 정렬된 집합, 범위 탐색 |
| `HashMap` | 평균 O(1) | 평균 O(1) | 평균 O(1) | 없음 | key-value 조회 |
| `LinkedHashMap` | 평균 O(1) | 평균 O(1) | 평균 O(1) | 삽입/접근 순서 | LRU, 순서 유지 Map |
| `TreeMap` | O(log n) | O(log n) | O(log n) | key 정렬 | floor/ceiling/range query |
| `ArrayDeque` | 양끝 O(1) amortized | 양끝 O(1) amortized | 양끝 O(1) amortized | 삽입 흐름 | stack, queue 대체 |
| `PriorityQueue` | peek O(1) | O(log n) | poll O(log n) | 우선순위 | 최소/최대값 반복 추출 |
| `ConcurrentHashMap` | 평균 O(1) | 평균 O(1) | 평균 O(1) | 없음 | 동시성 Map |
| `CopyOnWriteArrayList` | O(1) | O(n) 복사 | O(n) 복사 | 삽입 순서 | 읽기 매우 많고 쓰기 적은 경우 |

## 3. `ArrayList`

`ArrayList`는 내부적으로 배열을 사용한다.

동작 과정:

1. 내부 배열에 원소를 연속적으로 저장한다.
2. `get(index)`는 배열 인덱스로 바로 접근하므로 O(1)이다.
3. 끝에 추가할 공간이 있으면 바로 넣는다.
4. 공간이 부족하면 더 큰 배열을 만들고 기존 원소를 복사한다.
5. 중간에 삽입하거나 삭제하면 뒤쪽 원소들을 한 칸씩 밀거나 당긴다.

성능 기대:

- 순차 저장, 인덱스 조회, 순회가 빠르다.
- CPU cache locality가 좋아서 실제 성능도 좋은 편이다.
- 중간 삽입/삭제가 많으면 O(n) 이동 비용이 발생한다.
- 크기를 미리 알면 `new ArrayList<>(expectedSize)`로 리사이즈 비용을 줄일 수 있다.

주의:

```java
List<Integer> list = new ArrayList<>();
list.add(10);
list.add(20);

int first = list.get(0); // O(1)
list.remove(0);          // 뒤 원소를 앞으로 당김, O(n)
```

## 4. `LinkedList`

`LinkedList`는 노드가 이전/다음 노드를 가리키는 doubly linked list다.

동작 과정:

1. 각 원소가 별도 노드 객체로 존재한다.
2. 노드는 `item`, `prev`, `next`를 가진다.
3. 첫 노드와 마지막 노드를 알고 있다.
4. 양끝 삽입/삭제는 빠르다.
5. 인덱스 접근은 처음 또는 끝에서 노드를 따라가야 하므로 O(n)이다.

성능 기대:

- 이론상 노드를 이미 알고 있으면 중간 삽입/삭제 O(1)이다.
- 하지만 Java `LinkedList`에서 보통은 그 노드를 찾는 비용 O(n)이 먼저 든다.
- 노드 객체가 많아 메모리 오버헤드와 cache miss가 크다.
- 대부분의 일반 상황에서는 `ArrayList` 또는 `ArrayDeque`가 더 낫다.

사용해도 되는 경우:

- `Deque` 인터페이스가 필요하고 null 저장이 필요할 때.
- 하지만 stack/queue 용도라면 보통 `ArrayDeque`를 먼저 고려한다.

## 5. `Set`

### `HashSet`

`HashSet`은 내부적으로 `HashMap`을 사용한다. 원소를 `HashMap`의 key로 넣고, value에는 더미 객체를 넣는 방식이다.

동작 과정:

1. 원소의 `hashCode()`를 계산한다.
2. 해시값으로 bucket 위치를 고른다.
3. 같은 bucket에 후보가 있으면 `equals()`로 같은 원소인지 비교한다.
4. 같은 원소가 없으면 추가한다.

성능 기대:

- 포함 여부 확인 `contains` 평균 O(1).
- 중복 제거에 가장 자주 사용한다.
- 순서가 필요하면 `LinkedHashSet`, 정렬이 필요하면 `TreeSet`.

주의:

```java
Set<String> set = new HashSet<>();
set.add("java");
set.add("java");

System.out.println(set.size()); // 1
```

### `LinkedHashSet`

`HashSet`의 조회 성능을 유지하면서 삽입 순서를 보존한다.

적합한 상황:

- 중복 제거 후 원래 순서를 유지해야 할 때.
- 로그, 사용자 입력, CSV row 등 입력 순서가 의미 있을 때.

### `TreeSet`

`TreeSet`은 정렬된 집합이다. 내부적으로 `TreeMap` 기반이며 보통 Red-Black Tree를 사용한다.

성능 기대:

- 삽입, 삭제, 조회 O(log n).
- 항상 정렬된 순서로 순회할 수 있다.
- `first`, `last`, `floor`, `ceiling`, `lower`, `higher` 같은 탐색이 가능하다.

주의:

- 정렬 기준은 `Comparable` 또는 `Comparator`다.
- `TreeSet`에서 중복 여부는 `equals`가 아니라 비교 결과 `compare(a, b) == 0` 기준이다.

```java
Set<String> names = new TreeSet<>(Comparator.comparingInt(String::length));
names.add("a");
names.add("b");

System.out.println(names.size()); // 1, 길이가 같아서 같은 원소로 취급
```

## 6. `Queue`, `Deque`, `PriorityQueue`

### `ArrayDeque`

`ArrayDeque`는 원형 배열 기반의 double-ended queue다.

동작 과정:

1. 내부 배열을 원형으로 사용한다.
2. head와 tail 인덱스를 움직인다.
3. 앞쪽/뒤쪽 삽입과 삭제가 O(1) amortized다.
4. 배열이 가득 차면 확장한다.

권장 사용:

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);
stack.push(2);
System.out.println(stack.pop()); // 2

Deque<Integer> queue = new ArrayDeque<>();
queue.offer(1);
queue.offer(2);
System.out.println(queue.poll()); // 1
```

주의:

- `ArrayDeque`는 null을 허용하지 않는다.
- 예전 `Stack` 클래스보다 `ArrayDeque`를 쓰는 것이 일반적으로 낫다.

### `PriorityQueue`

`PriorityQueue`는 binary heap 기반이다. 기본은 min-heap이다.

동작 과정:

1. 내부 배열로 완전 이진 트리를 표현한다.
2. 부모 인덱스는 `(i - 1) / 2`, 자식은 `2 * i + 1`, `2 * i + 2`.
3. 삽입 시 마지막에 넣고 위로 올린다.
4. `poll` 시 root를 제거하고 마지막 원소를 root로 옮긴 뒤 아래로 내린다.

성능 기대:

- `peek`: O(1)
- `offer`: O(log n)
- `poll`: O(log n)
- 전체 정렬용이면 `Arrays.sort`가 낫고, "계속 최솟값/최댓값을 뽑는 문제"에서 강하다.

```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

PriorityQueue<Integer> maxHeap =
    new PriorityQueue<>(Comparator.reverseOrder());
```

Comparator 주의:

```java
// 위험: overflow 가능
// new PriorityQueue<>((a, b) -> a - b);

PriorityQueue<Integer> pq = new PriorityQueue<>(Integer::compare);
```

## 7. `Map` 계열

### `HashMap`

`HashMap`은 평균 O(1) key-value 조회를 목표로 한다.

동작 과정:

1. key의 `hashCode()`를 얻는다.
2. 해시값을 보정해서 bucket index를 계산한다.
3. 같은 bucket 안에서 key를 비교한다.
4. key가 같으면 value를 갱신하거나 반환한다.
5. key가 없으면 새 entry를 추가한다.
6. 원소 수가 capacity * load factor를 넘으면 resize한다.

JDK 8 이후의 대표적인 구현 특징:

- bucket 충돌이 적으면 linked list로 관리한다.
- 충돌이 많고 table 크기가 충분히 크면 bucket을 tree 형태로 바꿀 수 있다.
- 이 tree화는 최악의 충돌 상황을 완화하기 위한 구현 세부사항이다.

성능 기대:

- 좋은 해시 분포: `get`, `put`, `remove` 평균 O(1).
- 충돌이 심하면 느려질 수 있다.
- key 객체의 `hashCode`, `equals`가 정확해야 한다.
- key로 넣은 뒤 key의 동등성에 영향을 주는 필드를 바꾸면 찾을 수 없게 된다.

나쁜 예:

```java
class UserKey {
    String email;

    UserKey(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserKey other && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}

Map<UserKey, String> map = new HashMap<>();
UserKey key = new UserKey("a@test.com");
map.put(key, "A");

key.email = "b@test.com";

System.out.println(map.get(key)); // null일 수 있음
```

### `LinkedHashMap`

`LinkedHashMap`은 `HashMap`에 doubly linked list를 붙여 순서를 유지한다.

순서 모드:

- insertion-order: 넣은 순서 유지.
- access-order: 접근한 순서 기준으로 이동.

LRU cache 예:

```java
Map<Integer, String> lru = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
        return size() > 100;
    }
};
```

### `TreeMap`

`TreeMap`은 key를 정렬된 상태로 유지한다. 보통 Red-Black Tree 기반이다.

성능 기대:

- `get`, `put`, `remove`: O(log n)
- `firstKey`, `lastKey`, `floorKey`, `ceilingKey`: O(log n)
- 범위 탐색이 필요하면 `HashMap`보다 적합하다.

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "A");
map.put(20, "B");
map.put(30, "C");

System.out.println(map.floorKey(25));   // 20
System.out.println(map.ceilingKey(25)); // 30
```

### `EnumMap`

key가 enum이면 `EnumMap`이 매우 효율적이다.

- 내부적으로 enum ordinal 기반 배열을 쓴다.
- 메모리와 속도 면에서 `HashMap<Enum, V>`보다 유리하다.
- key로 null을 허용하지 않는다.

### `IdentityHashMap`

`equals`가 아니라 `==`로 key를 비교한다.

적합한 상황:

- 객체 동등성이 아니라 객체 identity 자체를 추적해야 할 때.
- 일반 비즈니스 Map으로는 거의 쓰지 않는다.

### `WeakHashMap`

key를 weak reference로 잡는다. key가 다른 곳에서 강하게 참조되지 않으면 GC 대상이 되고 entry가 제거될 수 있다.

적합한 상황:

- cache, 메타데이터 저장처럼 key 생명주기에 맞춰 entry가 사라져도 되는 경우.

### `ConcurrentHashMap`

동시 접근을 위한 Map이다.

특징:

- null key/value를 허용하지 않는다.
- 대부분의 단일 연산은 thread-safe하다.
- iterator는 fail-fast가 아니라 weakly consistent하다.
- `compute`, `merge` 같은 원자적 갱신 API를 잘 써야 한다.

주의:

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// 비원자적: get과 put 사이에 다른 스레드가 끼어들 수 있음
Integer old = map.get("a");
map.put("a", old == null ? 1 : old + 1);

// 원자적 갱신
map.merge("a", 1, Integer::sum);
```

## 8. `Map` API 차이

### 한눈에 보는 차이

| API | key 없음 | key 있음, value non-null | key 있음, value null | 값/함수 평가 | null 반환 의미 |
|---|---|---|---|---|---|
| `put(k, v)` | 저장 | 덮어씀 | 덮어씀 | `v`는 이미 계산됨 | null 저장 가능 Map이면 value로 저장 |
| `putIfAbsent(k, v)` | 저장 | 유지 | 저장 | `v`는 이미 계산됨 | null 저장 가능 Map이면 value로 저장 가능 |
| `computeIfAbsent(k, f)` | `f` 실행 후 non-null이면 저장 | 유지, `f` 실행 안 함 | `f` 실행 후 non-null이면 저장 | 필요할 때만 함수 실행 | 저장하지 않음 |
| `computeIfPresent(k, f)` | 실행 안 함 | `f` 실행 | 실행 안 함 | present + non-null일 때 실행 | mapping 제거 |
| `compute(k, f)` | `f(k, null)` 실행 | `f(k, old)` 실행 | `f(k, null)` 실행 | 항상 실행 | mapping 제거 또는 미저장 |
| `merge(k, v, f)` | `v` 저장 | `f(old, v)` 저장 | `v` 저장 | `v`는 이미 계산됨 | mapping 제거 |
| `getOrDefault(k, d)` | `d` 반환 | 기존 value 반환 | null 반환 | `d`는 이미 계산됨 | 저장 없음 |

`HashMap`처럼 null value를 허용하는 Map에서는 "key 없음"과 "key는 있는데 value가 null"을 구분해야 한다.

```java
Map<String, String> map = new HashMap<>();
map.put("a", null);

System.out.println(map.containsKey("a"));       // true
System.out.println(map.get("a"));               // null
System.out.println(map.getOrDefault("a", "x")); // null
```

### `put`

무조건 저장하고, 기존 value를 반환한다.

```java
Map<String, Integer> map = new HashMap<>();

Integer old1 = map.put("a", 1); // null
Integer old2 = map.put("a", 2); // 1

System.out.println(map.get("a")); // 2
```

쓰기 의도가 확실할 때 사용한다.

### `putIfAbsent`

key가 없거나 기존 value가 null이면 저장한다. 이미 non-null value가 있으면 유지한다.

```java
Map<String, List<Integer>> map = new HashMap<>();

map.putIfAbsent("a", new ArrayList<>());
map.get("a").add(1);
```

중요한 차이:

```java
map.putIfAbsent("a", expensiveCreate());
```

`expensiveCreate()`는 key가 이미 있어도 메서드 호출 전에 실행된다. 즉 lazy하지 않다.

적합한 경우:

- 새 value 생성 비용이 작다.
- 이미 만들어진 value를 넣고 싶다.
- "없을 때만 넣기" 자체가 목적이다.

### `computeIfAbsent`

key가 없거나 기존 value가 null일 때만 함수를 실행하고, 함수 결과가 non-null이면 저장한다.

```java
Map<String, List<Integer>> groups = new HashMap<>();

groups.computeIfAbsent("even", key -> new ArrayList<>()).add(2);
groups.computeIfAbsent("even", key -> new ArrayList<>()).add(4);

System.out.println(groups); // {even=[2, 4]}
```

동작 과정:

1. key로 기존 value를 찾는다.
2. 기존 value가 non-null이면 그대로 반환한다.
3. 기존 value가 없거나 null이면 mapping function을 호출한다.
4. 함수 결과가 non-null이면 저장한다.
5. 함수 결과가 null이면 저장하지 않는다.

`putIfAbsent`와의 핵심 차이:

```java
Map<String, List<Integer>> map = new HashMap<>();
map.put("a", new ArrayList<>());

// 새 ArrayList가 항상 만들어진다.
map.putIfAbsent("a", new ArrayList<>());

// key가 이미 있으므로 새 ArrayList가 만들어지지 않는다.
map.computeIfAbsent("a", key -> new ArrayList<>());
```

성능 기대:

- value 생성 비용이 크거나 대부분 key가 이미 있을 때 유리하다.
- grouping, cache 초기화, adjacency list 구성에 특히 좋다.
- lambda 호출 자체의 비용은 보통 작지만, 아주 뜨거운 루프에서는 측정이 필요하다.

주의:

- mapping function 안에서 같은 Map을 구조적으로 수정하지 않는 것이 좋다.
- `ConcurrentHashMap`에서는 전체 메서드 호출이 원자적으로 수행된다. 함수는 짧고 단순해야 한다.
- `ConcurrentHashMap`에서 함수가 오래 걸리면 같은 key 또는 관련 갱신이 지연될 수 있다.

### `computeIfPresent`

key가 있고 기존 value가 non-null일 때만 실행한다. 함수 결과가 null이면 entry를 제거한다.

```java
Map<String, Integer> count = new HashMap<>();
count.put("a", 3);

count.computeIfPresent("a", (key, old) -> old + 1);
count.computeIfPresent("b", (key, old) -> old + 1);

System.out.println(count); // {a=4}
```

### `compute`

key 존재 여부와 관계없이 함수를 실행한다. 가장 일반적이지만 의미가 넓어서 가독성이 떨어질 수 있다.

```java
Map<String, Integer> count = new HashMap<>();

count.compute("a", (key, old) -> old == null ? 1 : old + 1);
count.compute("a", (key, old) -> old == null ? 1 : old + 1);

System.out.println(count); // {a=2}
```

반환값이 null이면 mapping이 제거된다.

```java
count.compute("a", (key, old) -> null);
System.out.println(count.containsKey("a")); // false
```

### `merge`

카운팅처럼 "없으면 초기값, 있으면 합치기"에 좋다.

```java
Map<String, Integer> count = new HashMap<>();

count.merge("java", 1, Integer::sum);
count.merge("java", 1, Integer::sum);

System.out.println(count); // {java=2}
```

동작 과정:

1. key가 없거나 기존 value가 null이면 전달한 value를 저장한다.
2. key가 있고 기존 value가 non-null이면 remapping function을 실행한다.
3. remapping 결과가 null이면 entry를 제거한다.

주의:

- `HashMap`은 null value를 저장할 수 있지만, `merge(k, value, remappingFunction)`의 `value` 인자는 null이면 안 된다.
- 제거를 표현하고 싶다면 remapping function에서 null을 반환한다.

언제 쓰나:

- 빈도수 세기.
- 합계 누적.
- set/list 병합.

```java
Map<String, Set<String>> tags = new HashMap<>();

tags.merge(
    "post-1",
    new HashSet<>(Set.of("java")),
    (oldSet, newSet) -> {
        oldSet.addAll(newSet);
        return oldSet;
    }
);
```

### 선택 기준

| 상황 | 추천 |
|---|---|
| 무조건 덮어쓰기 | `put` |
| 이미 만든 값을 없을 때만 넣기 | `putIfAbsent` |
| 값 생성 비용이 있고 없을 때만 만들기 | `computeIfAbsent` |
| 카운팅/합산 | `merge` |
| key 존재 여부와 관계없이 새 값 계산 | `compute` |
| 기존 값이 있을 때만 변경 | `computeIfPresent` |
| 조회만 하고 기본값 반환 | `getOrDefault` |

## 9. Iteration과 fail-fast

일반 컬렉션의 iterator는 구조 변경을 감지하면 `ConcurrentModificationException`을 던질 수 있다. 이것은 버그를 빨리 드러내기 위한 fail-fast 동작이지, 동시성 안전을 보장하는 장치가 아니다.

나쁜 예:

```java
List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));

for (Integer number : numbers) {
    if (number == 2) {
        numbers.remove(number); // ConcurrentModificationException 가능
    }
}
```

좋은 예:

```java
numbers.removeIf(number -> number == 2);
```

또는:

```java
Iterator<Integer> iterator = numbers.iterator();
while (iterator.hasNext()) {
    if (iterator.next() == 2) {
        iterator.remove();
    }
}
```

## 10. 동시성 컬렉션 선택

| 상황 | 선택 |
|---|---|
| Map을 여러 스레드가 갱신 | `ConcurrentHashMap` |
| 읽기가 압도적으로 많고 쓰기가 드묾 | `CopyOnWriteArrayList` |
| producer-consumer queue | `BlockingQueue` 구현체 |
| 정렬된 동시 Map | `ConcurrentSkipListMap` |
| 단순 동기화 wrapper | `Collections.synchronizedList`, `synchronizedMap` |

주의:

- `Collections.synchronizedMap`은 단일 메서드는 동기화하지만, 반복 중에는 외부에서 직접 동기화해야 한다.
- `ConcurrentHashMap`의 `size()`는 동시 변경 중 정확한 순간값으로 의존하기 어렵다.
- 복합 연산은 `compute`, `merge`, `putIfAbsent` 같은 원자적 API를 사용한다.

## 11. Stream의 핵심 모델

Stream은 데이터를 저장하지 않는다. source에서 데이터를 흘려보내고, 중간 연산을 조합한 뒤, 최종 연산이 호출될 때 처리한다.

파이프라인 구조:

```java
List<String> result = names.stream()          // source
    .filter(name -> name.length() >= 3)       // intermediate
    .map(String::toUpperCase)                 // intermediate
    .sorted()                                 // intermediate
    .toList();                                // terminal
```

핵심 특징:

- 중간 연산은 lazy하다.
- 최종 연산이 있어야 실행된다.
- Stream은 한 번 소비하면 재사용할 수 없다.
- 원본 컬렉션을 자동으로 바꾸지 않는다.
- 함수형 스타일을 권장한다. shared mutable state와 섞으면 위험하다.

한 번만 사용 가능:

```java
Stream<String> stream = List.of("a", "b").stream();

long count = stream.count();
// stream.toList(); // IllegalStateException
```

## 12. 중간 연산

### `filter`

조건을 만족하는 원소만 통과시킨다.

```java
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .toList();
```

### `map`

원소를 다른 값으로 변환한다.

```java
List<String> names = users.stream()
    .map(User::name)
    .toList();
```

### `flatMap`

중첩된 stream을 펼친다.

```java
List<String> words = lines.stream()
    .flatMap(line -> Arrays.stream(line.split(" ")))
    .toList();
```

`map`을 쓰면 `Stream<Stream<String>>`이 되고, `flatMap`을 쓰면 `Stream<String>`이 된다.

### `distinct`

중복을 제거한다. 내부적으로 `equals`/`hashCode` 기준을 사용한다.

성능:

- 일반적으로 seen set이 필요하므로 추가 메모리 O(n)이 든다.
- 순서 있는 stream에서는 기존 순서를 유지해야 해서 병렬 처리 효율이 떨어질 수 있다.

### `sorted`

정렬한다.

성능:

- O(n log n).
- 전체 원소를 봐야 하므로 lazy pipeline 중에서도 stateful 연산이다.

### `limit`, `skip`

앞에서 n개만 가져오거나 n개를 건너뛴다.

```java
List<Integer> top3 = scores.stream()
    .sorted(Comparator.reverseOrder())
    .limit(3)
    .toList();
```

주의:

- `sorted().limit(3)`은 전체 정렬 O(n log n)이다.
- top-k가 크지 않으면 `PriorityQueue`가 더 나을 수 있다.

### `peek`

주로 디버깅용이다. 비즈니스 로직의 side effect를 넣는 용도로 쓰지 않는 편이 좋다.

```java
List<String> result = names.stream()
    .peek(name -> System.out.println("before = " + name))
    .map(String::toUpperCase)
    .peek(name -> System.out.println("after = " + name))
    .toList();
```

## 13. 최종 연산

### `toList`

Java 16+의 `Stream.toList()`는 unmodifiable list를 반환한다.

```java
List<String> names = users.stream()
    .map(User::name)
    .toList();

// names.add("new"); // UnsupportedOperationException
```

수정 가능한 list가 필요하면:

```java
List<String> names = users.stream()
    .map(User::name)
    .collect(Collectors.toCollection(ArrayList::new));
```

### `collect`

가변 컨테이너로 결과를 모은다.

```java
Map<String, List<User>> byTeam = users.stream()
    .collect(Collectors.groupingBy(User::team));
```

### `reduce`

값을 하나로 접는다. immutable 누적에 적합하다.

```java
int sum = numbers.stream()
    .reduce(0, Integer::sum);
```

복잡한 mutable 결과를 만들 때는 `reduce`보다 `collect`가 맞다.

### `anyMatch`, `allMatch`, `noneMatch`

short-circuit 연산이다.

```java
boolean hasAdmin = users.stream()
    .anyMatch(User::admin);
```

조건이 결정되는 순간 더 이상 처리하지 않을 수 있다.

### `findFirst`, `findAny`

```java
Optional<User> firstAdmin = users.stream()
    .filter(User::admin)
    .findFirst();
```

- `findFirst`: encounter order가 있으면 첫 번째를 보장한다.
- `findAny`: 병렬 stream에서 더 자유롭게 빠른 원소를 반환할 수 있다.

### `forEach`

side effect 수행용이다.

```java
users.forEach(user -> sendEmail(user.email()));
```

주의:

- stream pipeline 내부에서 외부 list에 add하는 방식은 피한다.
- 병렬 stream에서는 순서와 thread-safety 문제가 생긴다.

## 14. Collector 자주 쓰는 패턴

### grouping

```java
Map<String, List<User>> byTeam = users.stream()
    .collect(Collectors.groupingBy(User::team));
```

### counting

```java
Map<String, Long> countByTeam = users.stream()
    .collect(Collectors.groupingBy(User::team, Collectors.counting()));
```

### mapping

```java
Map<String, List<String>> namesByTeam = users.stream()
    .collect(Collectors.groupingBy(
        User::team,
        Collectors.mapping(User::name, Collectors.toList())
    ));
```

### max per group

```java
Map<String, Optional<User>> topByTeam = users.stream()
    .collect(Collectors.groupingBy(
        User::team,
        Collectors.maxBy(Comparator.comparingInt(User::score))
    ));
```

### partitioning

```java
Map<Boolean, List<User>> partitioned = users.stream()
    .collect(Collectors.partitioningBy(User::admin));
```

### toMap

```java
Map<Long, User> byId = users.stream()
    .collect(Collectors.toMap(User::id, Function.identity()));
```

중복 key가 가능하면 merge function이 필요하다.

```java
Map<String, User> bestByEmail = users.stream()
    .collect(Collectors.toMap(
        User::email,
        Function.identity(),
        BinaryOperator.maxBy(Comparator.comparingInt(User::score))
    ));
```

순서를 유지하고 싶으면 map supplier를 넘긴다.

```java
Map<Long, User> ordered = users.stream()
    .collect(Collectors.toMap(
        User::id,
        Function.identity(),
        (a, b) -> a,
        LinkedHashMap::new
    ));
```

## 15. Primitive Stream

boxing 비용을 줄이고 숫자 전용 연산을 쓰려면 `IntStream`, `LongStream`, `DoubleStream`을 사용한다.

```java
int sum = users.stream()
    .mapToInt(User::score)
    .sum();

double average = users.stream()
    .mapToInt(User::score)
    .average()
    .orElse(0.0);
```

`Stream<Integer>`는 객체 stream이라 boxing/unboxing 비용이 있다.

## 16. Stream 성능 감각

Stream은 항상 빠른 도구가 아니다. 목적은 "선언적으로 안전하게 데이터 변환을 표현"하는 것이다.

성능 판단:

- 단순 for-loop가 가장 빠른 경우가 많다.
- `map/filter` 조합은 가독성이 좋고 대부분 충분히 빠르다.
- `sorted`, `distinct`, `groupingBy`는 stateful하거나 메모리를 추가로 쓴다.
- 작은 데이터에서는 stream overhead가 더 클 수 있다.
- 아주 뜨거운 경로에서는 JMH 같은 벤치마크로 측정해야 한다.

좋은 사용:

```java
List<String> activeNames = users.stream()
    .filter(User::active)
    .map(User::name)
    .toList();
```

나쁜 사용:

```java
List<String> names = new ArrayList<>();

users.parallelStream()
    .filter(User::active)
    .forEach(user -> names.add(user.name())); // thread-safe하지 않음
```

좋은 사용:

```java
List<String> names = users.parallelStream()
    .filter(User::active)
    .map(User::name)
    .toList();
```

## 17. Parallel Stream

parallel stream은 공짜 성능 향상이 아니다.

적합한 경우:

- 데이터가 충분히 크다.
- CPU-bound 작업이다.
- 각 원소 처리가 독립적이다.
- 연산이 stateless하다.
- reduce/collect 연산이 associative하다.
- 순서 보장이 크게 중요하지 않다.

피해야 하는 경우:

- I/O 작업.
- DB/API 호출.
- shared mutable state 갱신.
- 작은 데이터.
- 순서 의존 로직.
- 이미 서버 요청 스레드가 많은 웹 애플리케이션의 무분별한 사용.

예:

```java
long count = numbers.parallelStream()
    .filter(JavaCollectionsNotes::isPrime)
    .count();
```

병렬화 전 질문:

- 병렬화할 만큼 원소 수가 많은가?
- 각 작업 시간이 충분히 큰가?
- 공용 ForkJoinPool을 써도 괜찮은가?
- 순서가 깨져도 되는가?
- shared state가 없는가?

## 18. `Optional`과 Stream

`Optional`은 null 가능성을 명시적으로 표현하는 값 컨테이너다.

```java
String name = users.stream()
    .filter(User::admin)
    .findFirst()
    .map(User::name)
    .orElse("unknown");
```

주의:

- 필드 타입으로 남발하지 않는다.
- 컬렉션 반환은 `Optional<List<T>>`보다 빈 list가 낫다.
- `get()`을 바로 호출하면 null 체크를 `isPresent()`로 바꾼 것에 불과하다.

## 19. 자료구조 선택 빠른 기준

| 요구사항 | 선택 |
|---|---|
| 순서대로 담고 인덱스로 조회 | `ArrayList` |
| 양끝에서 넣고 빼기 | `ArrayDeque` |
| stack | `ArrayDeque` |
| queue | `ArrayDeque` |
| 우선순위 기준으로 계속 꺼내기 | `PriorityQueue` |
| 중복 제거 | `HashSet` |
| 중복 제거 + 삽입 순서 유지 | `LinkedHashSet` |
| 중복 제거 + 정렬 유지 | `TreeSet` |
| key-value 빠른 조회 | `HashMap` |
| key-value + 삽입/접근 순서 | `LinkedHashMap` |
| key 정렬, floor/ceiling | `TreeMap` |
| enum key | `EnumMap` |
| 동시성 key-value | `ConcurrentHashMap` |
| 읽기 많고 쓰기 거의 없음 | `CopyOnWriteArrayList` |
| 빈도수 세기 | `HashMap.merge` |
| grouping | `computeIfAbsent` 또는 `Collectors.groupingBy` |

## 20. 자주 틀리는 포인트

- `HashMap` key로 mutable 객체를 쓰고 저장 후 필드를 바꾼다.
- `TreeSet`에서 comparator가 같다고 판단하면 다른 객체도 중복 제거된다.
- `PriorityQueue` iterator가 우선순위 순회라고 착각한다. 정렬 순서로 꺼내려면 `poll`해야 한다.
- `putIfAbsent`가 lazy하다고 착각한다. value 인자는 먼저 계산된다.
- `computeIfAbsent` 함수 안에서 같은 Map을 수정한다.
- `getOrDefault`가 null mapping도 default로 바꿔준다고 착각한다.
- `Stream` 중간 연산만 쓰고 최종 연산을 호출하지 않는다.
- `Stream`을 재사용한다.
- parallel stream에서 외부 mutable collection을 갱신한다.
- 단순한 루프가 더 명확한데 억지로 stream을 쓴다.
