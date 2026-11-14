package algorithms;

import model.AlgorithmType;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module A(b): Factorial Lab.
 * Computes n! both recursively and iteratively from first principles,
 * handling n = 0 (0! = 1) and rejecting negative inputs with a clear message.
 */
public class FactorialLab {
    private long recursiveCalls;
    private long iterativeLoops;

    /**
     * Computes n! using both Recursive and Iterative implementations.
     * Time Complexity: O(n), Space Complexity: O(n) recursive / O(1) iterative.
     */
    public ExecutionResult execute(int n) {
        StepCollector collector = new StepCollector();

        if (n < 0) {
            collector.recordArrayStep(
                    new int[0], new int[0], new int[0], new int[0],
                    -1, "", -1, "", -1, "", 0, 0,
                    "Validation Error: Negative input n = " + n + " is rejected! Factorial n! requires n >= 0."
            );
            return new ExecutionResult(
                    AlgorithmType.FACTORIAL_TWO_WAYS,
                    "Rejected Negative Input (n = " + n + "). Please enter n >= 0 (Note: 0! = 1).",
                    collector.toArray(),
                    0, 0, 0, 0
            );
        }

        if (n > 20) {
            n = 20; // Cap at 20! = 2,432,902,008,176,640,000 to fit in 64-bit signed long
        }

        // 1. Recursive Factorial timing & call count
        recursiveCalls = 0;
        long t0 = System.nanoTime();
        long recResult = factorialRecursive(n);
        long recTimeNs = System.nanoTime() - t0;

        // 2. Iterative Factorial timing & iteration count
        iterativeLoops = 0;
        long t1 = System.nanoTime();
        long iterResult = factorialIterative(n);
        long iterTimeNs = System.nanoTime() - t1;

        // Build step-by-step progression 0! .. n!
        int[] snap = new int[n + 1];
        long running = 1L;
        for (int k = 0; k <= n; k++) {
            if (k == 0) {
                running = 1L;
            } else {
                running = running * k;
            }
            snap[k] = (int) Math.min(running, 999_999);
            collector.recordArrayStep(
                    snap,
                    k > 0 ? new int[]{k - 1} : new int[0],
                    new int[0],
                    new int[]{k},
                    k > 0 ? k - 1 : -1, (k - 1) + "!",
                    -1, "",
                    k, k + "! = " + running,
                    n + 1,
                    k + 1,
                    k == 0
                            ? "Base case: 0! = 1"
                            : "Step " + k + ": " + k + "! = " + k + " * " + (k - 1) + "! = " + running
            );
        }

        String[] headers = new String[]{
                "Approach", "Result (" + n + "!)", "Calls / Iterations", "Execution Time", "Time Complexity", "Space Complexity"
        };
        String[][] rows = new String[][]{
                {
                        "Recursive n!",
                        String.valueOf(recResult),
                        recursiveCalls + " recursive calls",
                        String.format("%d ns (%.4f ms)", recTimeNs, recTimeNs / 1_000_000.0),
                        "O(n)",
                        "O(n) [n + 1 activation frames on call stack]"
                },
                {
                        "Iterative n!",
                        String.valueOf(iterResult),
                        iterativeLoops + " loop iterations",
                        String.format("%d ns (%.4f ms)", iterTimeNs, iterTimeNs / 1_000_000.0),
                        "O(n)",
                        "O(1) [Single accumulator variable in loop]"
                }
        };

        String summary = n + "! = " + recResult
                + " | Recursive: " + recursiveCalls + " calls (" + recTimeNs + " ns)"
                + " | Iterative: " + iterativeLoops + " iterations (" + iterTimeNs + " ns)";

        return new ExecutionResult(
                AlgorithmType.FACTORIAL_TWO_WAYS,
                summary,
                collector.toArray(),
                recursiveCalls + iterativeLoops,
                recursiveCalls,
                iterativeLoops,
                recTimeNs,
                headers,
                rows
        );
    }

    /**
     * Computes n! recursively: n! = n * (n - 1)! with base case 0! = 1.
     * Time Complexity: O(n), Space Complexity: O(n).
     */
    public long factorialRecursive(int n) {
        recursiveCalls++;
        if (n == 0) {
            return 1L;
        }
        return n * factorialRecursive(n - 1);
    }

    /**
     * Computes n! iteratively using a for-loop accumulator from 1 to n.
     * Time Complexity: O(n), Space Complexity: O(1).
     */
    public long factorialIterative(int n) {
        long result = 1L;
        if (n == 0) {
            return 1L;
        }
        for (int i = 1; i <= n; i++) {
            iterativeLoops++;
            result *= i;
        }
        return result;
    }
}
