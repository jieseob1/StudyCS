package studycs.javalab;

import studycs.javalab.algorithms.arrayhash.PrefixSum;
import studycs.javalab.algorithms.backtracking.CombinationSum;
import studycs.javalab.algorithms.backtracking.Permutations;
import studycs.javalab.algorithms.dp.CoinChange;
import studycs.javalab.algorithms.dp.LongestIncreasingSubsequence;
import studycs.javalab.algorithms.graph.ConnectedComponentsDfs;
import studycs.javalab.algorithms.graph.FloydWarshall;
import studycs.javalab.algorithms.graph.KruskalMinimumSpanningTree;
import studycs.javalab.algorithms.greedy.ActivitySelection;
import studycs.javalab.algorithms.math.GcdLcm;
import studycs.javalab.algorithms.math.SieveOfEratosthenes;
import studycs.javalab.algorithms.search.BinarySearch;
import studycs.javalab.algorithms.sorting.CountingSort;
import studycs.javalab.algorithms.sorting.MergeSort;
import studycs.javalab.algorithms.sorting.QuickSort;
import studycs.javalab.algorithms.string.KmpSearch;

public final class ExamAlgorithms {
    private ExamAlgorithms() {
    }

    public static void runDemo() {
        System.out.println();
        System.out.println("== Exam algorithm demo ==");

        MergeSort.runDemo();
        QuickSort.runDemo();
        CountingSort.runDemo();
        BinarySearch.runDemo();
        PrefixSum.runDemo();
        Permutations.runDemo();
        CombinationSum.runDemo();
        CoinChange.runDemo();
        LongestIncreasingSubsequence.runDemo();
        ActivitySelection.runDemo();
        SieveOfEratosthenes.runDemo();
        GcdLcm.runDemo();
        KmpSearch.runDemo();
        ConnectedComponentsDfs.runDemo();
        KruskalMinimumSpanningTree.runDemo();
        FloydWarshall.runDemo();
    }
}
