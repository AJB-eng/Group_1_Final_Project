package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module C(1): From-Scratch Bubble Sort implementation.
 * Time Complexity: Best O(n) [already sorted with early exit], Average O(n^2), Worst O(n^2).
 * Space Complexity: O(1) auxiliary space.
 * Stability: Stable (only swaps when arr[j] > arr[j + 1]).
 */
public class BubbleSort {
    private long operationsCount;
    private long comparisonsCount;
    private long swapsCount;

    public BubbleSort() {
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
     * Executes Bubble Sort on a copy of the input array, measuring pure execution time
     * via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input) {
        int[] timingArr = ArrayUtils.copyArray(input);
        long startTime = System.nanoTime();
        runBubbleSort(timingArr, null);
        long elapsedNanos = System.nanoTime() - startTime;

        int[] workingArr = ArrayUtils.copyArray(input);
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        runBubbleSort(workingArr, collector);

        String finalResult = "Sorted (" + workingArr.length + " items): " + ArrayUtils.arrayToString(workingArr)
                + " | Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount;
        return new ExecutionResult(
                AlgorithmType.BUBBLE_SORT,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.comparisonsCount,
                this.swapsCount,
                elapsedNanos
        );
    }

    /**
     * Runs Bubble Sort without step recording for high-speed benchmarking in Module C/D.
     */
    public long sortForBenchmark(int[] arr) {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        long t0 = System.nanoTime();
        runBubbleSort(arr, null);
        return System.nanoTime() - t0;
    }

    private void runBubbleSort(int[] arr, StepCollector collector) {
        int n = arr.length;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr, new int[0], new int[0], new int[0],
                    0, "START", n - 1, "END", -1, "", n, operationsCount,
                    "Initial array before Bubble Sort: " + ArrayUtils.arrayToString(arr)
            );
        }

        if (n <= 1) {
            return;
        }

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - 1 - i; j++) {
                comparisonsCount++;
                operationsCount++;

                if (collector != null) {
                    int[] sortedTail = i > 0 ? ArrayUtils.rangeIndices(n - i, n - 1) : new int[0];
                    collector.recordArrayStep(
                            arr,
                            new int[]{j, j + 1},
                            new int[0],
                            sortedTail,
                            j, "j",
                            j + 1, "j+1",
                            n - 1 - i, "PASS " + (i + 1),
                            n,
                            operationsCount,
                            "Pass " + (i + 1) + ": comparing arr[" + j + "]=" + arr[j]
                                    + " vs arr[" + (j + 1) + "]=" + arr[j + 1]
                                    + " (Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount + ")"
                    );
                }

                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                    swapsCount++;
                    operationsCount += 3;

                    if (collector != null) {
                        int[] sortedTail = i > 0 ? ArrayUtils.rangeIndices(n - i, n - 1) : new int[0];
                        collector.recordArrayStep(
                                arr,
                                new int[0],
                                new int[]{j, j + 1},
                                sortedTail,
                                j, "SWAP",
                                j + 1, "SWAP",
                                n - 1 - i, "PASS " + (i + 1),
                                n,
                                operationsCount,
                                "Swapped arr[" + j + "] <-> arr[" + (j + 1) + "] (" + arr[j + 1] + " > " + arr[j]
                                        + ") | Swaps=" + swapsCount
                        );
                    }
                }
            }

            if (collector != null) {
                int[] sortedTailAfterPass = ArrayUtils.rangeIndices(n - 1 - i, n - 1);
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[0],
                        sortedTailAfterPass,
                        n - 1 - i, "LOCKED",
                        -1, "",
                        -1, "",
                        n,
                        operationsCount,
                        "After Pass " + (i + 1) + ": " + ArrayUtils.arrayToString(arr)
                );
            }

            if (!swapped) {
                if (collector != null) {
                    collector.recordArrayStep(
                            arr,
                            new int[0],
                            new int[0],
                            ArrayUtils.rangeIndices(0, n - 1),
                            -1, "", -1, "", -1, "",
                            n,
                            operationsCount,
                            "Early exit after Pass " + (i + 1) + " (0 swaps). Array is sorted!"
                    );
                }
                return;
            }
        }

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    new int[0],
                    new int[0],
                    ArrayUtils.rangeIndices(0, n - 1),
                    -1, "", -1, "", -1, "",
                    n,
                    operationsCount,
                    "Bubble Sort complete! Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount
            );
        }
    }
}
