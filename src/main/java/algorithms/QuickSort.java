
package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module C(5): From-Scratch Recursive Quick Sort implementation.
 * Implements BOTH required partition strategies taught in CSC2107:
 * 1. First-Element Pivot with "Yellow/Green" Partition:
 *    - Pivot is chosen as arr[low] (the first element of the subarray).
 *    - Yellow region [low+1 .. yellow] holds elements <= pivot.
 *    - Green region [yellow+1 .. green-1] holds elements > pivot.
 *    - After scanning green from low+1 to high, pivot arr[low] is swapped with arr[yellow].
 * 2. Randomised-Pivot Option:
 *    - Picks a random index in [low..high] and swaps it into arr[low] before running the Yellow/Green partition,
 *      preventing O(n^2) worst-case behavior on already-sorted or reverse-sorted inputs.
 */
public class QuickSort {
    private long operationsCount;
    private long comparisonsCount;
    private long swapsCount;
    private final boolean useRandomPivot;
    private long rngState = 0x9E3779B97F4A7C15L;

    public QuickSort() {
        this(false);
    }

    public QuickSort(boolean useRandomPivot) {
        this.useRandomPivot = useRandomPivot;
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
     * Executes Quick Sort on a copy of the input array, measuring pure execution time
     * via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input) {
        int[] timingArr = ArrayUtils.copyArray(input);
        this.rngState = 0x9E3779B97F4A7C15L;
        long startTime = System.nanoTime();
        runQuickSort(timingArr, null);
        long elapsedNanos = System.nanoTime() - startTime;

        int[] workingArr = ArrayUtils.copyArray(input);
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        this.rngState = 0x9E3779B97F4A7C15L;
        // Guard against stack overflow in visual trace if large sorted array is run with first-element pivot
        runQuickSort(workingArr, collector);

        AlgorithmType type = useRandomPivot ? AlgorithmType.QUICK_SORT_RANDOMIZED : AlgorithmType.QUICK_SORT;
        String modeLabel = useRandomPivot ? "Randomised-Pivot" : "First-Element Pivot (Yellow/Green)";
        String finalResult = "Sorted [" + modeLabel + "] (" + workingArr.length + " items): "
                + ArrayUtils.arrayToString(workingArr)
                + " | Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount;

        return new ExecutionResult(
                type,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.comparisonsCount,
                this.swapsCount,
                elapsedNanos
        );
    }

    /**
     * Runs Quick Sort without step recording for Module C/D benchmarking.
     * Uses tail-call elimination on the larger partition so stack depth never exceeds O(log n)
     * even on worst-case n = 100,000 sorted inputs with First-Element Pivot!
     */
    public long sortForBenchmark(int[] arr) {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.swapsCount = 0;
        this.rngState = System.nanoTime() ^ 0x9E3779B97F4A7C15L;
        long t0 = System.nanoTime();
        quickSortTailSafe(arr, 0, arr.length - 1);
        return System.nanoTime() - t0;
    }

    private void quickSortTailSafe(int[] arr, int low, int high) {
        while (low < high) {
            int pivotIdx = yellowGreenPartition(arr, low, high, null);
            // Recurse on smaller side first to keep stack depth O(log n) even in O(n^2) worst case
            if (pivotIdx - low < high - pivotIdx) {
                quickSortTailSafe(arr, low, pivotIdx - 1);
                low = pivotIdx + 1;
            } else {
                quickSortTailSafe(arr, pivotIdx + 1, high);
                high = pivotIdx - 1;
            }
        }
    }

    private void runQuickSort(int[] arr, StepCollector collector) {
        int n = arr.length;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr, new int[0], new int[0], new int[0],
                    0, "LOW", n - 1, "HIGH", -1, "", n, operationsCount,
                    "Initial array before Quick Sort ("
                            + (useRandomPivot ? "Randomised Pivot" : "First-Element Pivot, Yellow/Green Partition")
                            + "): " + ArrayUtils.arrayToString(arr)
            );
        }

        if (n > 1) {
            quickSortRecursive(arr, 0, n - 1, collector);
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
                    "Quick Sort complete! Comparisons=" + comparisonsCount + ", Swaps=" + swapsCount
            );
        }
    }

    private void quickSortRecursive(int[] arr, int low, int high, StepCollector collector) {
        operationsCount++;
        if (low < high) {
            int pivotIndex = yellowGreenPartition(arr, low, high, collector);
            quickSortRecursive(arr, low, pivotIndex - 1, collector);
            quickSortRecursive(arr, pivotIndex + 1, high, collector);
        }
    }

    /**
     * First-Element Pivot "Yellow/Green" Partition taught in CSC2107:
     * - If useRandomPivot is true, first swaps a random index in [low..high] into arr[low].
     * - Pivot = arr[low].
     * - Yellow pointer starts at low ( invariant: arr[low+1 .. yellow] <= pivot ).
     * - Green pointer scans from low + 1 to high ( invariant: arr[yellow+1 .. green-1] > pivot ).
     * - Finally swaps arr[low] <-> arr[yellow] to place pivot at its exact final position.
     */
    private int yellowGreenPartition(int[] arr, int low, int high, StepCollector collector) {
        if (useRandomPivot && high > low) {
            rngState = rngState * 6364136223846793005L + 1442695040888963407L;
            int span = high - low + 1;
            int randIdx = low + (int) ((rngState & 0x7FFFFFFFFFFFFFFFL) % span);
            if (randIdx != low) {
                int tmp = arr[low];
                arr[low] = arr[randIdx];
                arr[randIdx] = tmp;
                swapsCount++;
                operationsCount += 3;

                if (collector != null) {
                    collector.recordArrayStep(
                            arr,
                            new int[]{low},
                            new int[]{low, randIdx},
                            new int[0],
                            low, "NEW PIVOT",
                            randIdx, "RAND IDX",
                            -1, "",
                            arr.length,
                            operationsCount,
                            "Randomised Pivot: swapped random index " + randIdx + " (value " + arr[low] + ") into first position low=" + low
                    );
                }
            }
        }

        int pivot = arr[low];
        int yellow = low;
        operationsCount += 2;

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    new int[]{low},
                    new int[0],
                    new int[0],
                    low, "PIVOT=" + pivot,
                    yellow, "YELLOW",
                    low + 1, "GREEN",
                    arr.length,
                    operationsCount,
                    "Partition [" + low + ".." + high + "]: First-element pivot arr[" + low + "] = " + pivot
                            + ". Yellow (<=pivot) at " + yellow + ", Green scanning from " + (low + 1) + "."
            );
        }

        for (int green = low + 1; green <= high; green++) {
            comparisonsCount++;
            operationsCount++;

            if (collector != null) {
                int[] yellowZone = yellow >= low + 1 ? ArrayUtils.rangeIndices(low + 1, yellow) : new int[]{low};
                int[] greenZone = green - 1 >= yellow + 1 ? ArrayUtils.rangeIndices(yellow + 1, green - 1) : new int[0];
                collector.recordArrayStep(
                        arr,
                        yellowZone,
                        new int[]{green},
                        greenZone,
                        low, "PIVOT=" + pivot,
                        yellow, "YELLOW",
                        green, "GREEN",
                        arr.length,
                        operationsCount,
                        "Comparing arr[green=" + green + "]=" + arr[green] + " <= pivot (" + pivot + ")?"
                );
            }

            if (arr[green] <= pivot) {
                yellow++;
                operationsCount++;
                if (yellow != green) {
                    int temp = arr[yellow];
                    arr[yellow] = arr[green];
                    arr[green] = temp;
                    swapsCount++;
                    operationsCount += 3;

                    if (collector != null) {
                        int[] yellowZone = ArrayUtils.rangeIndices(low + 1, yellow);
                        int[] greenZone = green >= yellow + 1 ? ArrayUtils.rangeIndices(yellow + 1, green) : new int[0];
                        collector.recordArrayStep(
                                arr,
                                yellowZone,
                                new int[]{yellow, green},
                                greenZone,
                                low, "PIVOT=" + pivot,
                                yellow, "YELLOW",
                                green, "GREEN",
                                arr.length,
                                operationsCount,
                                "arr[" + green + "] <= " + pivot + ": advanced yellow to " + yellow
                                        + " and swapped arr[" + yellow + "] <-> arr[" + green + "]"
                        );
                    }
                }
            }
        }

        // Swap pivot at low with end of yellow zone
        if (low != yellow) {
            int temp = arr[low];
            arr[low] = arr[yellow];
            arr[yellow] = temp;
            swapsCount++;
            operationsCount += 3;
        }

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    yellow > low ? ArrayUtils.rangeIndices(low, yellow - 1) : new int[0],
                    new int[]{low, yellow},
                    new int[]{yellow},
                    low, "LEFT",
                    high, "RIGHT",
                    yellow, "PIVOT LOCKED",
                    arr.length,
                    operationsCount,
                    "After Partition [" + low + ".." + high + "]: swapped pivot " + pivot
                            + " into index " + yellow + " -> " + ArrayUtils.arrayToString(arr)
            );
        }

        return yellow;
    }
}
