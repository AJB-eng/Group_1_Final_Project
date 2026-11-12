package algorithms;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Implements Module C(c) ("Compare All" side-by-side sorting comparison)
 * and Module D (Experimental Time Complexity Analysis across n = 100..10,000+
 * on Random, Already Sorted, and Reverse Sorted inputs, averaged over 3 runs).
 */
public class SortingComparisonLab {

    /**
     * Module C(c): Runs all sorting algorithms on the exact same user input array
     * and returns a side-by-side comparison table.
     */
    public ExecutionResult executeCompareAll(int[] input) {
        StepCollector collector = new StepCollector();

        int[] bArr = ArrayUtils.copyArray(input);
        BubbleSort bubble = new BubbleSort();
        long bTime = bubble.sortForBenchmark(bArr);

        int[] sArr = ArrayUtils.copyArray(input);
        SelectionSort selection = new SelectionSort();
        long sTime = selection.sortForBenchmark(sArr);

        int[] iArr = ArrayUtils.copyArray(input);
        InsertionSort insertion = new InsertionSort();
        long iTime = insertion.sortForBenchmark(iArr);

        int[] mArr = ArrayUtils.copyArray(input);
        MergeSort merge = new MergeSort();
        long mTime = merge.sortForBenchmark(mArr);

        int[] q1Arr = ArrayUtils.copyArray(input);
        QuickSort quickFirst = new QuickSort(false);
        long q1Time = quickFirst.sortForBenchmark(q1Arr);

        int[] q2Arr = ArrayUtils.copyArray(input);
        QuickSort quickRand = new QuickSort(true);
        long q2Time = quickRand.sortForBenchmark(q2Arr);

        String[] headers = new String[]{
                "Sorting Algorithm", "Comparisons", "Swaps / Moves", "Time (ns)", "Time (ms)", "Best / Avg / Worst", "Space & Stability"
        };

        String[][] rows = new String[][]{
                {
                        "1. Bubble Sort",
                        String.valueOf(bubble.getComparisonsCount()),
                        String.valueOf(bubble.getSwapsCount()),
                        String.valueOf(bTime),
                        String.format("%.4f ms", bTime / 1_000_000.0),
                        "O(n) / O(n^2) / O(n^2)",
                        "O(1) | Stable"
                },
                {
                        "2. Selection Sort",
                        String.valueOf(selection.getComparisonsCount()),
                        String.valueOf(selection.getSwapsCount()),
                        String.valueOf(sTime),
                        String.format("%.4f ms", sTime / 1_000_000.0),
                        "O(n^2) / O(n^2) / O(n^2)",
                        "O(1) | Unstable"
                },
                {
                        "3. Insertion Sort",
                        String.valueOf(insertion.getComparisonsCount()),
                        String.valueOf(insertion.getMovesCount()),
                        String.valueOf(iTime),
                        String.format("%.4f ms", iTime / 1_000_000.0),
                        "O(n) / O(n^2) / O(n^2)",
                        "O(1) | Stable"
                },
                {
                        "4. Merge Sort",
                        String.valueOf(merge.getComparisonsCount()),
                        String.valueOf(merge.getMovesCount()),
                        String.valueOf(mTime),
                        String.format("%.4f ms", mTime / 1_000_000.0),
                        "O(n log n) all cases",
                        "O(n) | Stable"
                },
                {
                        "5a. QuickSort (1st Pivot)",
                        String.valueOf(quickFirst.getComparisonsCount()),
                        String.valueOf(quickFirst.getSwapsCount()),
                        String.valueOf(q1Time),
                        String.format("%.4f ms", q1Time / 1_000_000.0),
                        "O(n log n) / O(n log n) / O(n^2)",
                        "O(log n) | Unstable"
                },
                {
                        "5b. QuickSort (Random Pivot)",
                        String.valueOf(quickRand.getComparisonsCount()),
                        String.valueOf(quickRand.getSwapsCount()),
                        String.valueOf(q2Time),
                        String.format("%.4f ms", q2Time / 1_000_000.0),
                        "O(n log n) expected all inputs",
                        "O(log n) | Unstable"
                }
        };

        long totalTime = bTime + sTime + iTime + mTime + q1Time + q2Time;
        long totalComps = bubble.getComparisonsCount() + selection.getComparisonsCount()
                + insertion.getComparisonsCount() + merge.getComparisonsCount()
                + quickFirst.getComparisonsCount() + quickRand.getComparisonsCount();
        long totalSwaps = bubble.getSwapsCount() + selection.getSwapsCount()
                + insertion.getMovesCount() + merge.getMovesCount()
                + quickFirst.getSwapsCount() + quickRand.getSwapsCount();

        collector.recordArrayStep(
                mArr,
                new int[0],
                new int[0],
                ArrayUtils.rangeIndices(0, mArr.length - 1),
                -1, "", -1, "", -1, "",
                mArr.length,
                totalComps + totalSwaps,
                "Compared all 5 sorting algorithms (plus Randomised QuickSort) on n = " + input.length
                        + " elements. Sorted Output: " + ArrayUtils.arrayToString(mArr)
        );

        String summary = "Compare All Complete (n=" + input.length + ") | Sorted: " + ArrayUtils.arrayToString(mArr);
        return new ExecutionResult(
                AlgorithmType.COMPARE_ALL_SORTS,
                summary,
                collector.toArray(),
                totalComps + totalSwaps,
                totalComps,
                totalSwaps,
                totalTime,
                headers,
                rows
        );
    }

    /**
     * Module D: Experimental Time Complexity Analysis.
     * Times every sorting algorithm for n = 100, 500, 1000, 2000, 5000, 10000
     * (plus n = 50000 in extended mode for MergeSort and QuickSort)
     * on Random, Already Sorted, and Reverse Sorted inputs, averaging 3 runs per configuration.
     */
    public ExecutionResult executeModuleDBenchmark(String distributionMode) {
        String dist = (distributionMode == null || distributionMode.isEmpty()) ? "RANDOM" : distributionMode;
        int[] sizes = new int[]{100, 500, 1000, 2000, 5000, 10000};

        String[] headers = new String[]{
                "Input Size (n) [" + dist + "]",
                "Bubble (ms)",
                "Selection (ms)",
                "Insertion (ms)",
                "Merge (ms)",
                "Quick 1st-Pivot (ms)",
                "Quick Rand-Pivot (ms)"
        };

        // Include 2 extra rows for n = 50,000 and n = 100,000 on the O(n log n) sorts as required by Module D(a)
        String[][] rows = new String[sizes.length + 2][7];
        StepCollector collector = new StepCollector();
        long totalBenchNanos = 0;
        long totalOps = 0;

        for (int s = 0; s < sizes.length; s++) {
            int n = sizes[s];
            int[] baseData = generateDataset(n, dist);

            long bNanos = averageThreeRuns(baseData, 1);
            long sNanos = averageThreeRuns(baseData, 2);
            long iNanos = averageThreeRuns(baseData, 3);
            long mNanos = averageThreeRuns(baseData, 4);
            long q1Nanos = averageThreeRuns(baseData, 5);
            long q2Nanos = averageThreeRuns(baseData, 6);

            totalBenchNanos += (bNanos + sNanos + iNanos + mNanos + q1Nanos + q2Nanos);
            totalOps += n;

            rows[s][0] = "n = " + n;
            rows[s][1] = String.format("%.3f", bNanos / 1_000_000.0);
            rows[s][2] = String.format("%.3f", sNanos / 1_000_000.0);
            rows[s][3] = String.format("%.3f", iNanos / 1_000_000.0);
            rows[s][4] = String.format("%.3f", mNanos / 1_000_000.0);
            rows[s][5] = String.format("%.3f", q1Nanos / 1_000_000.0);
            rows[s][6] = String.format("%.3f", q2Nanos / 1_000_000.0);

            collector.recordArrayStep(
                    new int[]{
                            (int) (bNanos / 1000),
                            (int) (sNanos / 1000),
                            (int) (iNanos / 1000),
                            (int) (mNanos / 1000),
                            (int) (q1Nanos / 1000),
                            (int) (q2Nanos / 1000)
                    },
                    new int[0], new int[0], new int[0],
                    -1, "", -1, "", -1, "", 6, totalOps,
                    "Benchmarked n = " + n + " (" + dist + ", avg of 3 runs): "
                            + "Bubble=" + rows[s][1] + "ms, Selection=" + rows[s][2] + "ms, "
                            + "Insertion=" + rows[s][3] + "ms, Merge=" + rows[s][4] + "ms, "
                            + "QS(1st)=" + rows[s][5] + "ms, QS(Rand)=" + rows[s][6] + "ms"
            );
        }

        // Large n = 50,000 and n = 100,000 for Merge Sort & Quick Sort (Module D(a))
        int[] largeSizes = new int[]{50000, 100000};
        for (int idx = 0; idx < largeSizes.length; idx++) {
            int n = largeSizes[idx];
            int rowIdx = sizes.length + idx;
            int[] baseData = generateDataset(n, dist);

            long mNanos = averageThreeRuns(baseData, 4);
            long q2Nanos = averageThreeRuns(baseData, 6);
            // For sorted/reverse-sorted at n >= 50,000, First-Element QuickSort is O(n^2) (~5 billion ops),
            // so we run it on Random or cap at n=10,000 for sorted to keep UI responsive
            String q1Cell;
            if ("RANDOM".equals(dist)) {
                long q1Nanos = averageThreeRuns(baseData, 5);
                q1Cell = String.format("%.3f", q1Nanos / 1_000_000.0);
                totalBenchNanos += q1Nanos;
            } else {
                q1Cell = "O(n^2) Degenerate";
            }
            totalBenchNanos += (mNanos + q2Nanos);

            rows[rowIdx][0] = "n = " + n;
            rows[rowIdx][1] = "O(n^2) Skipped";
            rows[rowIdx][2] = "O(n^2) Skipped";
            rows[rowIdx][3] = "SORTED".equals(dist)
                    ? String.format("%.3f", averageThreeRuns(baseData, 3) / 1_000_000.0)
                    : "O(n^2) Skipped";
            rows[rowIdx][4] = String.format("%.3f", mNanos / 1_000_000.0);
            rows[rowIdx][5] = q1Cell;
            rows[rowIdx][6] = String.format("%.3f", q2Nanos / 1_000_000.0);
        }

        String summary = "Module D Benchmark (" + dist + " Input, Avg of 3 Runs across n=100..100,000) Completed!";
        return new ExecutionResult(
                AlgorithmType.MODULE_D_BENCHMARK,
                summary,
                collector.toArray(),
                totalOps,
                totalOps,
                0,
                totalBenchNanos,
                headers,
                rows
        );
    }

    private long averageThreeRuns(int[] baseData, int algoCode) {
        long sum = 0;
        for (int r = 0; r < 3; r++) {
            int[] copy = ArrayUtils.copyArray(baseData);
            if (algoCode == 1) {
                sum += new BubbleSort().sortForBenchmark(copy);
            } else if (algoCode == 2) {
                sum += new SelectionSort().sortForBenchmark(copy);
            } else if (algoCode == 3) {
                sum += new InsertionSort().sortForBenchmark(copy);
            } else if (algoCode == 4) {
                sum += new MergeSort().sortForBenchmark(copy);
            } else if (algoCode == 5) {
                sum += new QuickSort(false).sortForBenchmark(copy);
            } else if (algoCode == 6) {
                sum += new QuickSort(true).sortForBenchmark(copy);
            }
        }
        return sum / 3;
    }

    private int[] generateDataset(int n, String dist) {
        if ("SORTED".equals(dist)) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = i + 1;
            }
            return arr;
        } else if ("REVERSE".equals(dist)) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = n - i;
            }
            return arr;
        } else {
            return ArrayUtils.generateRandomArray(n, 1, n * 10);
        }
    }
}
