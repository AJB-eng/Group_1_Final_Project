package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module C(3): From-Scratch Insertion Sort implementation.
 * Time Complexity: Best O(n) [already sorted], Average O(n^2), Worst O(n^2) [reverse sorted].
 * Space Complexity: O(1) in-place.
 * Stability: Stable (shifts elements only when strictly greater: arr[j] > key).
 */
public class InsertionSort {
    private long operationsCount;
    private long comparisonsCount;
    private long movesCount;

    public InsertionSort() {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.movesCount = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public long getMovesCount() {
        return movesCount;
    }

    /**
     * Executes Insertion Sort on a copy of the input array, measuring pure execution time
     * via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input) {
        int[] timingArr = ArrayUtils.copyArray(input);
        long startTime = System.nanoTime();
        runInsertionSort(timingArr, null);
        long elapsedNanos = System.nanoTime() - startTime;

        int[] workingArr = ArrayUtils.copyArray(input);
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.movesCount = 0;
        runInsertionSort(workingArr, collector);

        String finalResult = "Sorted (" + workingArr.length + " items): " + ArrayUtils.arrayToString(workingArr)
                + " | Comparisons=" + comparisonsCount + ", Shifts/Moves=" + movesCount;
        return new ExecutionResult(
                AlgorithmType.INSERTION_SORT,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.comparisonsCount,
                this.movesCount,
                elapsedNanos
        );
    }

    /**
     * Runs Insertion Sort without step recording for Module C/D benchmarking.
     */
    public long sortForBenchmark(int[] arr) {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.movesCount = 0;
        long t0 = System.nanoTime();
        runInsertionSort(arr, null);
        return System.nanoTime() - t0;
    }

    private void runInsertionSort(int[] arr, StepCollector collector) {
        int n = arr.length;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr, new int[0], new int[0], n > 0 ? new int[]{0} : new int[0],
                    0, "SORTED[0]", n - 1, "END", -1, "", n, operationsCount,
                    "Initial array before Insertion Sort: " + ArrayUtils.arrayToString(arr)
            );
        }

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            operationsCount += 2;

            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[]{i},
                        new int[0],
                        ArrayUtils.rangeIndices(0, i - 1),
                        j, "j",
                        i, "KEY=" + key,
                        -1, "",
                        n,
                        operationsCount,
                        "Pass " + i + ": picked key = " + key + " at index " + i + " to insert into sorted prefix [0.." + (i - 1) + "]."
                );
            }

            while (j >= 0) {
                comparisonsCount++;
                operationsCount++;

                if (collector != null) {
                    collector.recordArrayStep(
                            arr,
                            new int[]{j, j + 1},
                            new int[0],
                            ArrayUtils.rangeIndices(0, i),
                            j, "COMPARE",
                            i, "PASS " + i,
                            j + 1, "KEY=" + key,
                            n,
                            operationsCount,
                            "Comparing arr[" + j + "]=" + arr[j] + " > key (" + key + ")?"
                    );
                }

                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    movesCount++;
                    operationsCount++;

                    if (collector != null) {
                        collector.recordArrayStep(
                                arr,
                                new int[0],
                                new int[]{j, j + 1},
                                ArrayUtils.rangeIndices(0, i),
                                j, "SHIFT->",
                                j + 1, "SHIFTED",
                                -1, "",
                                n,
                                operationsCount,
                                "Shifted arr[" + j + "]=" + arr[j] + " right to index " + (j + 1) + "."
                        );
                    }
                    j--;
                } else {
                    break;
                }
            }

            arr[j + 1] = key;
            movesCount++;
            operationsCount++;

            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[0],
                        ArrayUtils.rangeIndices(0, i),
                        0, "SORTED",
                        i, "END PREFIX",
                        j + 1, "INSERTED " + key,
                        n,
                        operationsCount,
                        "After Pass " + i + ": inserted key " + key + " at index " + (j + 1)
                                + " -> " + ArrayUtils.arrayToString(arr)
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
                    "Insertion Sort complete! Comparisons=" + comparisonsCount + ", Moves=" + movesCount
            );
        }
    }
}
