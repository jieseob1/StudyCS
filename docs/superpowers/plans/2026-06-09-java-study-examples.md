# Java Study Examples Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a self-contained Java study folder for collection internals and algorithm-test patterns.

**Architecture:** Keep this independent from the existing Gradle performance-test app. Use plain Java classes, a Bash runner, visible trace output, and a small assertion main class for verification.

**Tech Stack:** Java 17+, Bash, no external dependencies.

---

### Task 1: Add RED verification harness

**Files:**
- Create: `java-study/scripts/run-all.sh`
- Create: `java-study/src/test/java/com/studycs/JavaStudyAssertions.java`

- [x] **Step 1: Add compile/run script**

Create a script that compiles all Java files under `java-study/src/main/java` and `java-study/src/test/java`, then runs assertions and demos.

- [x] **Step 2: Add assertion runner first**

Write assertions that reference the intended public API:

- `MapOperationLab`
- `HashStructureLab`
- `ToyHashMap`
- `PatternExamples`

- [x] **Step 3: Run RED verification**

Run:

```bash
./java-study/scripts/run-all.sh
```

Expected result: compilation fails because the implementation classes do not exist yet.

### Task 2: Implement data structure examples

**Files:**
- Create: `java-study/src/main/java/com/studycs/datastructures/MapOperationLab.java`
- Create: `java-study/src/main/java/com/studycs/datastructures/ToyHashMap.java`
- Create: `java-study/src/main/java/com/studycs/datastructures/HashStructureLab.java`
- Create: `java-study/src/main/java/com/studycs/datastructures/DataStructureDemo.java`

- [x] **Step 1: Implement Map operation traces**

Add examples for `computeIfAbsent`, `putIfAbsent`, `compute`, and `merge`.

- [x] **Step 2: Implement simplified HashMap**

Add bucket indexing, linked-list collision handling, key lookup by `equals()`, and resize by load factor.

- [x] **Step 3: Add collection demos**

Print `HashSet`, `TreeSet`, `PriorityQueue`, and `ArrayDeque` behavior.

### Task 3: Implement algorithm pattern examples

**Files:**
- Create: `java-study/src/main/java/com/studycs/algorithms/PatternExamples.java`
- Create: `java-study/src/main/java/com/studycs/algorithms/AlgorithmPatternDemo.java`

- [x] **Step 1: Implement array/search patterns**

Add two pointers, sliding window, prefix sum, and lower bound.

- [x] **Step 2: Implement graph/search patterns**

Add BFS, DFS components, backtracking subsets, union-find, and dijkstra.

- [x] **Step 3: Implement optimization patterns**

Add dynamic programming coin change, greedy activity selection, and heap top K.

### Task 4: Document and verify

**Files:**
- Create: `java-study/README.md`
- Create: `docs/superpowers/specs/2026-06-09-java-study-design.md`
- Create: `docs/superpowers/plans/2026-06-09-java-study-examples.md`

- [x] **Step 1: Add user-facing README**

Document run command, data-structure examples, algorithm patterns, and recommended files to inspect.

- [x] **Step 2: Run GREEN verification**

Run:

```bash
./java-study/scripts/run-all.sh
```

Expected result: assertions pass and both demos print trace output.
