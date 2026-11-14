
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

public long sortForBenchmark(int[] arr) {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        long t0 = System.nanoTime();
        runSelectionSort(arr, null);
        return System.nanoTime() - t0;
    }

    private void runSelectionSort(int[] arr, StepCollector collector) {
        int n = arr.length;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr, new int[0], new int[0], new int[0],
                    0, "START", n - 1, "END", -1, "", n, operationsCount,
                    "Initial array before Selection Sort: " + ArrayUtils.arrayToString(arr)
            );
        }

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            operationsCount++;

            for (int j = i + 1; j < n; j++) {
                comparisonsCount++;
                operationsCount++;

                if (collector != null) {
                    int[] sortedPrefix = i > 0 ? ArrayUtils.rangeIndices(0, i - 1) : new int[0];
                    collector.recordArrayStep(
                            arr,
                            new int[]{minIndex, j},
                            new int[0],
                            sortedPrefix,
                            i, "i=" + i,
                            j, "SCAN j",
                            minIndex, "MIN=" + arr[minIndex],
                            n,
                            operationsCount,
                            "Pass " + (i + 1) + ": comparing arr[" + j + "]=" + arr[j]
                                    + " with current minimum arr[" + minIndex + "]=" + arr[minIndex]
                    );
                }

                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                    operationsCount++;
                }
            }

            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
                swapsCount++;
                operationsCount += 3;

                if (collector != null) {
                    int[] sortedPrefix = ArrayUtils.rangeIndices(0, i);
                    collector.recordArrayStep(
                            arr,
                            new int[0],
                            new int[]{i, minIndex},
                            sortedPrefix,
                            i, "SWAP",
                            minIndex, "SWAP",
                            i, "LOCKED",
                            n,
                            operationsCount,
                            "Pass " + (i + 1) + ": swapped minimum " + arr[i]
                                    + " from index " + minIndex + " into index " + i
                    );
                }
            } else if (collector != null) {
                int[] sortedPrefix = ArrayUtils.rangeIndices(0, i);
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[0],
                        sortedPrefix,
                        i, "LOCKED",
                        -1, "",
                        -1, "",
                        n,
                        operationsCount,
                        "Pass " + (i + 1) + ": arr[" + i + "]=" + arr[i] + " is already the minimum."
                );
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
                    "Selection Sort complete! Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount
            );
        }
    }
}
