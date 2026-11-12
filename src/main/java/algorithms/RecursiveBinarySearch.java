package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * From-Scratch Recursive Binary Search (O(log n)) implementation.
 * Automatically ensures the input array is sorted (using custom MergeSort if unsorted)
 * and recursively searches [low..high] while recording every midpoint comparison.
 */
public class RecursiveBinarySearch {
    private long operationsCount;

    public RecursiveBinarySearch() {
        this.operationsCount = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }

    /**
     * Executes Recursive Binary Search for target on the input array, measuring pure algorithmic
     * search time via System.nanoTime() and recording step-by-step snapshots.
     */
    public ExecutionResult execute(int[] input, int target) {
        int[] sortedArr = ArrayUtils.copyArray(input);
        boolean wasAlreadySorted = isSortedAscending(sortedArr);
        if (!wasAlreadySorted) {
            MergeSort.sortRaw(sortedArr);
        }

        // Pure timing run on the sorted array
        int[] timingArr = ArrayUtils.copyArray(sortedArr);
        long startTime = System.nanoTime();
        this.operationsCount = 0;
        binarySearchRecursive(timingArr, target, 0, timingArr.length - 1, null);
        long elapsedNanos = System.nanoTime() - startTime;

        // Visual trace run
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;

        if (!wasAlreadySorted) {
            collector.recordArrayStep(
                    sortedArr,
                    new int[0],
                    new int[0],
                    new int[0],
                    0, "LOW",
                    sortedArr.length - 1, "HIGH",
                    -1, "",
                    sortedArr.length,
                    operationsCount,
                    "Pre-condition: Sorted input array via custom MergeSort for Binary Search: "
                            + ArrayUtils.arrayToString(sortedArr)
            );
        } else {
            collector.recordArrayStep(
                    sortedArr,
                    new int[0],
                    new int[0],
                    new int[0],
                    0, "LOW",
                    sortedArr.length - 1, "HIGH",
                    -1, "",
                    sortedArr.length,
                    operationsCount,
                    "Initial sorted array for Recursive Binary Search (Target = " + target + "): "
                            + ArrayUtils.arrayToString(sortedArr)
            );
        }

        int foundIndex = binarySearchRecursive(sortedArr, target, 0, sortedArr.length - 1, collector);

        String finalResult;
        if (foundIndex >= 0) {
            finalResult = "Target " + target + " FOUND at sorted index " + foundIndex
                    + " | Sorted Array: " + ArrayUtils.arrayToString(sortedArr);
        } else {
            finalResult = "Target " + target + " NOT FOUND in array"
                    + " | Sorted Array: " + ArrayUtils.arrayToString(sortedArr);
        }

        return new ExecutionResult(
                AlgorithmType.RECURSIVE_BINARY_SEARCH,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private int binarySearchRecursive(int[] arr, int target, int low, int high, StepCollector collector) {
        operationsCount++; // Base case comparison: low > high
        if (low > high) {
            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[0],
                        new int[0],
                        low, "LOW",
                        high, "HIGH",
                        -1, "NOT FOUND",
                        arr.length,
                        operationsCount,
                        "Base case reached (low=" + low + " > high=" + high + "). Target " + target + " is NOT present."
                );
            }
            return -1;
        }

        int mid = low + (high - low) / 2;
        operationsCount++; // Midpoint computation: mid = low + (high - low) / 2

        if (collector != null) {
            collector.recordArrayStep(
                    arr,
                    new int[]{mid},
                    new int[0],
                    ArrayUtils.rangeIndices(low, high),
                    low, "LOW=" + low,
                    high, "HIGH=" + high,
                    mid, "MID=" + mid,
                    arr.length,
                    operationsCount,
                    "Recursive call on [" + low + ".." + high + "]: mid = " + mid
                            + ", comparing arr[" + mid + "]=" + arr[mid] + " with target " + target + "."
            );
        }

        operationsCount++; // Equality comparison: arr[mid] == target
        if (arr[mid] == target) {
            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[0],
                        new int[0],
                        new int[]{mid},
                        low, "LOW",
                        high, "HIGH",
                        mid, "FOUND=" + target,
                        arr.length,
                        operationsCount,
                        "Match! arr[" + mid + "] == " + target + ". Target found at index " + mid + "."
                );
            }
            return mid;
        }

        operationsCount++; // Directional comparison: arr[mid] > target
        if (arr[mid] > target) {
            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[]{mid},
                        ArrayUtils.rangeIndices(mid, high),
                        new int[0],
                        low, "LOW",
                        mid - 1, "NEW HIGH",
                        mid, "TOO HIGH",
                        arr.length,
                        operationsCount,
                        "arr[" + mid + "]=" + arr[mid] + " > " + target
                                + ": discarding right half [" + mid + ".." + high + "], recursing left [" + low + ".." + (mid - 1) + "]."
                );
            }
            return binarySearchRecursive(arr, target, low, mid - 1, collector);
        } else {
            if (collector != null) {
                collector.recordArrayStep(
                        arr,
                        new int[]{mid},
                        ArrayUtils.rangeIndices(low, mid),
                        new int[0],
                        mid + 1, "NEW LOW",
                        high, "HIGH",
                        mid, "TOO LOW",
                        arr.length,
                        operationsCount,
                        "arr[" + mid + "]=" + arr[mid] + " < " + target
                                + ": discarding left half [" + low + ".." + mid + "], recursing right [" + (mid + 1) + ".." + high + "]."
                );
            }
            return binarySearchRecursive(arr, target, mid + 1, high, collector);
        }
    }

    private boolean isSortedAscending(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
