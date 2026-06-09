# Java Study Examples Design

## Goal

Create a self-contained `java-study` folder that helps the user learn Java collection internals and algorithm-test patterns by running concrete Java examples with visible trace output.

## Scope

The folder covers two areas:

1. Java data structures and method semantics:
   - `computeIfAbsent`, `putIfAbsent`, `compute`, and `merge`
   - Hash bucket collision behavior through a simplified `ToyHashMap`
   - `HashSet`, `TreeSet`, `PriorityQueue`, and `ArrayDeque` behavior
2. Algorithm patterns for coding-test practice:
   - two pointers, sliding window, prefix sum, binary search
   - BFS, DFS, backtracking
   - dynamic programming, greedy, heap
   - union-find and dijkstra

## Architecture

The code is intentionally independent from the existing performance-test Gradle project. It uses plain Java source files and a shell runner so the user can run the examples without importing a build tool or adding dependencies.

`java-study/src/main/java` contains study examples and demo entrypoints. `java-study/src/test/java` contains a small assertion runner that validates representative behavior before the demos are printed.

## Validation

`java-study/scripts/run-all.sh` compiles every Java source file, runs `JavaStudyAssertions`, then runs both console demos. This validates that examples compile and that the documented behavior remains stable.
