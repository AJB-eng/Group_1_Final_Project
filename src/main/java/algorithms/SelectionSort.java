
package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module C(2): From-Scratch Selection Sort implementation.
 * Time Complexity: Best O(n^2), Average O(n^2), Worst O(n^2).
 * Space Complexity: O(1) in-place.
 * Stability: Unstable (long-range swap between i and minIndex can jump over equal elements).
 */
public class SelectionSort {
    private long operationsCount;
    private long comparisonsCount;
    private long swapsCount;

    public SelectionSort() {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public long getSwapsCount() {
        return swapsCount;
    }

    /**
     * Executes Selection Sort on a copy of the input array, measuring pure execution time
     * via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input) {
        int[] timingArr = ArrayUtils.copyArray(input);
        long startTime = System.nanoTime();
        runSelectionSort(timingArr, null);
        long elapsedNanos = System.nanoTime() - startTime;

        int[] workingArr = ArrayUtils.copyArray(input);
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        runSelectionSort(workingArr, collector);

        String finalResult = "Sorted (" + workingArr.length + " items): " + ArrayUtils.arrayToString(workingArr)
                + " | Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount;
        return new ExecutionResult(
                AlgorithmType.SELECTION_SORT,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.comparisonsCount,
                this.swapsCount,
                elapsedNanos
        );
    }
