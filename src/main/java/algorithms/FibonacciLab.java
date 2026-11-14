package algorithms;

import model.AlgorithmType;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module A(a): Fibonacci Lab.
 * Computes the n-th Fibonacci number in three ways from first principles:
 * (i) Naive Recursion: O(2^n) time, O(n) call stack space (capped at n <= 30)
 * (ii) Memoized Recursion (Top-Down DP): O(n) time, O(n) space
 * (iii) Bottom-Up Dynamic Programming: O(n) time, O(n) table space
 */
public class FibonacciLab {
    public static final int SAFE_NAIVE_LIMIT = 30;

    private long naiveCalls;
    private long memoCalls;
    private long dpIterations;

    /**
     * Executes all three Fibonacci approaches for input n, measuring time with System.nanoTime()
     * and counting function calls / loop iterations.
     * Time Complexity: O(2^n) for Naive (when n <= 30), O(n) for Memoized and Bottom-Up DP.
     */
    public ExecutionResult execute(int n) {
        StepCollector collector = new StepCollector();

        if (n < 0) {
            collector.recordArrayStep(
                    new int[0], new int[0], new int[0], new int[0],
                    -1, "", -1, "", -1, "", 0, 0,
                    "Invalid Input: n = " + n + ". Fibonacci is only defined for non-negative integers (n >= 0)."
            );
            return new ExecutionResult(
                    AlgorithmType.FIBONACCI_THREE_WAYS,
                    "Validation Error: n must be >= 0 (received " + n + ").",
                    collector.toArray(),
                    0, 0, 0, 0
            );
        }

        if (n > 92) {
            n = 92; // Prevent 64-bit signed long overflow
        }

        // (i) Naive Recursion (limited to n <= SAFE_NAIVE_LIMIT)
        long naiveResult = -1;
        long naiveTimeNs = 0;
        naiveCalls = 0;
        boolean naiveSkipped = (n > SAFE_NAIVE_LIMIT);

        if (!naiveSkipped) {
            long t0 = System.nanoTime();
            naiveResult = fibNaive(n);
            naiveTimeNs = System.nanoTime() - t0;
        }

        // (ii) Memoized Recursion (Top-Down)
        memoCalls = 0;
        long[] memoTiming = createEmptyMemo(n);
        long t1 = System.nanoTime();
        long memoResult = fibMemoized(n, memoTiming);
        long memoTimeNs = System.nanoTime() - t1;

        // (iii) Bottom-Up Dynamic Programming
        dpIterations = 0;
        long t2 = System.nanoTime();
        long dpResult = fibBottomUpPure(n);
        long dpTimeNs = System.nanoTime() - t2;

        // Record step-by-step DP / Memo progression for visualization
        int displayLen = n + 1;
        if (displayLen > 35) {
            displayLen = 35; // Keep bar visualization readable
        }
        int[] dpSnap = new int[displayLen];
        long prev2 = 0;
        long prev1 = 1;
        for (int i = 0; i <= n; i++) {
            long curr;
            if (i == 0) {
                curr = 0;
            } else if (i == 1) {
                curr = 1;
            } else {
                curr = prev1 + prev2;
                prev2 = prev1;
                prev1 = curr;
            }
            if (i < displayLen) {
                dpSnap[i] = (int) Math.min(curr, 999_999);
                int[] active = i >= 2 ? new int[]{i - 2, i - 1} : new int[0];
                int[] done = new int[]{i};
                collector.recordArrayStep(
                        dpSnap,
                        active,
                        new int[0],
                        done,
                        i >= 2 ? i - 2 : -1, "F(" + (i - 2) + ")",
                        i >= 1 ? i - 1 : -1, "F(" + (i - 1) + ")",
                        i, "F(" + i + ")=" + curr,
                        displayLen,
                        i + 1,
                        "Bottom-Up DP Step " + i + ": F(" + i + ") = " + curr
                                + (i >= 2 ? " (computed as F(" + (i - 1) + ") + F(" + (i - 2) + "))" : " (base case)")
                );
            }
        }

        String naiveNote = naiveSkipped
                ? "SKIPPED (n=" + n + " > " + SAFE_NAIVE_LIMIT + ": Naive O(2^n) would make >2.69M redundant calls and freeze!)"
                : String.valueOf(naiveResult);
        String naiveCallsStr = naiveSkipped
                ? "Skipped (> 2,692,537 calls)"
                : String.valueOf(naiveCalls) + " calls";
        String naiveTimeStr = naiveSkipped
                ? "Capped (n > " + SAFE_NAIVE_LIMIT + ")"
                : String.format("%d ns (%.3f ms)", naiveTimeNs, naiveTimeNs / 1_000_000.0);

        collector.recordArrayStep(
                dpSnap,
                new int[0],
                new int[0],
                new int[]{displayLen - 1},
                -1, "", -1, "", displayLen - 1, "F(" + n + ")",
                displayLen,
                (naiveSkipped ? 0 : naiveCalls) + memoCalls + dpIterations,
                "Comparison for F(" + n + ") = " + dpResult
                        + " | Naive Calls: " + naiveCallsStr
                        + " | Memoized Calls: " + memoCalls
                        + " | Bottom-Up DP Iterations: " + dpIterations
        );

        String[] headers = new String[]{
                "Method", "Result F(" + n + ")", "Calls / Iterations", "Execution Time", "Time Complexity", "Space & Why"
        };
        String[][] rows = new String[][]{
                {
                        "(i) Naive Recursion",
                        naiveNote,
                        naiveCallsStr,
                        naiveTimeStr,
                        "O(2^n)",
                        "O(n) stack. Recomputes overlapping subproblems F(k) exponentially many times!"
                },
                {
                        "(ii) Memoized Recursion",
                        String.valueOf(memoResult),
                        memoCalls + " calls",
                        String.format("%d ns (%.4f ms)", memoTimeNs, memoTimeNs / 1_000_000.0),
                        "O(n)",
                        "O(n) memo array + stack. Caches each F(k) on first visit so subproblems run once."
                },
                {
                        "(iii) Bottom-Up DP",
                        String.valueOf(dpResult),
                        dpIterations + " iterations",
                        String.format("%d ns (%.4f ms)", dpTimeNs, dpTimeNs / 1_000_000.0),
                        "O(n)",
                        "O(n) table (or O(1) rolling vars). Iterates 2..n with zero recursion overhead."
                }
        };

        String summary = "F(" + n + ") = " + dpResult
                + " | Naive: " + naiveCallsStr + " (" + naiveTimeStr + ")"
                + " | Memoized: " + memoCalls + " calls (" + memoTimeNs + " ns)"
                + " | Bottom-Up DP: " + dpIterations + " iterations (" + dpTimeNs + " ns)";

        long totalOps = (naiveSkipped ? 0 : naiveCalls) + memoCalls + dpIterations;
        return new ExecutionResult(
                AlgorithmType.FIBONACCI_THREE_WAYS,
                summary,
                collector.toArray(),
                totalOps,
                memoCalls,
                dpIterations,
                dpTimeNs,
                headers,
                rows
        );
    }

    /**
     * (i) Naive recursive Fibonacci: F(n) = F(n-1) + F(n-2).
     * Time Complexity: O(2^n), Space Complexity: O(n) call stack.
     */
    public long fibNaive(int n) {
        naiveCalls++;
        if (n <= 1) {
            return n;
        }
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    /**
     * (ii) Memoized recursive Fibonacci using a raw long[] cache array.
     * Time Complexity: O(n), Space Complexity: O(n).
     */
    public long fibMemoized(int n, long[] memo) {
        memoCalls++;
        if (n <= 1) {
            memo[n] = n;
            return n;
        }
        if (memo[n] != -1L) {
            return memo[n];
        }
        memo[n] = fibMemoized(n - 1, memo) + fibMemoized(n - 2, memo);
        return memo[n];
    }

    /**
     * (iii) Bottom-up Dynamic Programming Fibonacci using an iterative table.
     * Time Complexity: O(n), Space Complexity: O(n).
     */
    public long fibBottomUpPure(int n) {
        if (n <= 1) {
            dpIterations = 1;
            return n;
        }
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dpIterations++;
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    private long[] createEmptyMemo(int n) {
        long[] memo = new long[n + 1];
        for (int i = 0; i <= n; i++) {
            memo[i] = -1L;
        }
        return memo;
    }
}
