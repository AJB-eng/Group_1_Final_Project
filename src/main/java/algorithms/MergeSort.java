package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module C(4): From-Scratch Recursive Merge Sort (O(n log n)) implementation.
 * Time Complexity: Best O(n log n), Average O(n log n), Worst O(n log n).
 * Space Complexity: O(n) auxiliary merge arrays.
 * Stability: Stable (uses leftArr[i] <= rightArr[j] during merge).
 */
public class MergeSort {
    private long operationsCount;
    private long comparisonsCount;
    private long movesCount;

    public MergeSort() {
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
     * Executes Merge Sort on a copy of the input array, measuring pure execution time
     * via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input) {
        int[] timingArr = ArrayUtils.copyArray(input);
        long startTime = System.nanoTime();
        runMergeSort(timingArr, null);
        long elapsedNanos = System.nanoTime() - startTime;

        int[] workingArr = ArrayUtils.copyArray(input);
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.movesCount = 0;
        runMergeSort(workingArr, collector);

        String finalResult = "Sorted (" + workingArr.length + " items): " + ArrayUtils.arrayToString(workingArr)
                + " | Comparisons=" + comparisonsCount + ", Merge Moves=" + movesCount;
        return new ExecutionResult(
                AlgorithmType.MERGE_SORT,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.comparisonsCount,
                this.movesCount,
                elapsedNanos
        );
    }

    /**
     * Runs Merge Sort without step recording for Module C/D benchmarking.
     */
    public long sortForBenchmark(int[] arr) {
        this.operationsCount = 0;
        this.comparisonsCount = 0;
        this.movesCount = 0;
        long t0 = System.nanoTime();
        runMergeSort(arr, null);
        return System.nanoTime() - t0;
    }

    public static void sortRaw(int[] arr) {
        MergeSort sorter = new MergeSort();
        sorter.runMergeSort(arr, null);
    }

    private void runMergeSort(int[] arr, StepCollector collector) {
        int n = arr.length;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr, new int[0], new int[0], new int[0],
                    0, "LEFT", n - 1, "RIGHT", -1, "", n, operationsCount,
                    "Initial array before Merge Sort: " + ArrayUtils.arrayToString(arr)
            );
        }

        if (n > 1) {
            mergeSortRecursive(arr, 0, n - 1, collector);
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
                    "Merge Sort complete! Comparisons=" + comparisonsCount + ", Moves=" + movesCount
            );
        }
    }

    private void mergeSortRecursive(int[] arr, int left, int right, StepCollector collector) {
        operationsCount++;
        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    ArrayUtils.rangeIndices(left, right),
                    new int[0],
                    new int[0],
                    left, "LEFT",
                    right, "RIGHT",
                    mid, "MID",
                    arr.length,
                    operationsCount,
                    "Divide: splitting [" + left + ".." + right + "] at mid=" + mid
            );
        }

        mergeSortRecursive(arr, left, mid, collector);
        mergeSortRecursive(arr, mid + 1, right, collector);
        merge(arr, left, mid, right, collector);
    }

    private void merge(int[] arr, int left, int mid, int right, StepCollector collector) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];
        operationsCount += 4;

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
            movesCount++;
            operationsCount++;
        }
        for (int j = 0; j < n2; j++) {
            rightArr[j] = arr[mid + 1 + j];
            movesCount++;
            operationsCount++;
        }

        int i = 0;
        int j = 0;
        int k = left;
        operationsCount += 3;

        while (i < n1 && j < n2) {
            comparisonsCount++;
            operationsCount++;

            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[]{left + i, mid + 1 + j},
                        new int[0],
                        new int[0],
                        left, "L",
                        right, "R",
                        k, "WRITE",
                        arr.length,
                        operationsCount,
                        "Merging [" + left + ".." + right + "]: comparing leftArr[" + i + "]=" + leftArr[i]
                                + " <= rightArr[" + j + "]=" + rightArr[j]
                );
            }

            if (leftArr[i] <= rightArr[j]) {
                arr[k] = leftArr[i];
                i++;
            } else {
                arr[k] = rightArr[j];
                j++;
            }
            movesCount++;
            operationsCount += 2;

            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[]{k},
                        ArrayUtils.rangeIndices(left, k),
                        left, "L",
                        right, "R",
                        k, "MERGED",
                        arr.length,
                        operationsCount,
                        "Wrote " + arr[k] + " to arr[" + k + "]"
                );
            }
            k++;
        }

        while (i < n1) {
            arr[k] = leftArr[i];
            i++;
            k++;
            movesCount++;
            operationsCount += 2;
        }

        while (j < n2) {
            arr[k] = rightArr[j];
            j++;
            k++;
            movesCount++;
            operationsCount += 2;
        }

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    new int[0],
                    new int[0],
                    ArrayUtils.rangeIndices(left, right),
                    left, "L",
                    right, "R",
                    mid, "MID",
                    arr.length,
                    operationsCount,
                    "After Merge [" + left + ".." + right + "]: " + ArrayUtils.arrayToString(arr)
            );
        }
    }
}
