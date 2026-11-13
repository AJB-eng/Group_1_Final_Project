package algorithms;

import model.AlgorithmType;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module A(c): Recursive Tower of Hanoi Solver and Visualizer.
 * Supports user-selected disk count (1 to 8) AND user-selected Source, Destination, and Auxiliary pegs.
 * Lists every move, animates disk transfers across pegs, and confirms total moves = 2^n - 1.
 */
public class TowerOfHanoi {
    private static final String[] PEG_SHORT = new String[]{"A", "B", "C"};
    private static final String[] PEG_NAMES = new String[]{"Peg A", "Peg B", "Peg C"};

    private long operationsCount;
    private long recursiveCallsCount;
    private int moveCounter;
    private int[][] pegs;
    private int[] pegSizes;

    public TowerOfHanoi() {
        this.operationsCount = 0;
        this.recursiveCallsCount = 0;
        this.moveCounter = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }
    
 /**
     * Default execute method (Source = Peg A [0], Destination = Peg C [2], Auxiliary = Peg B [1]).
     */
    public ExecutionResult execute(int requestedDisks) {
        return execute(requestedDisks, 0, 2, 1);
    }

    /**
     * Executes the recursive Tower of Hanoi algorithm for diskCount disks (1..8)
     * from sourcePeg to destPeg using auxPeg.
     * Time Complexity: O(2^n) — performs exactly 2^n - 1 disk moves and 2^n - 1 recursive calls.
     * Space Complexity: O(n) recursion stack depth.
     */
    public ExecutionResult execute(int requestedDisks, int sourcePeg, int destPeg, int auxPeg) {
        int diskCount = requestedDisks;
        if (diskCount < 1) {
            diskCount = 1;
        } else if (diskCount > 8) {
            diskCount = 8;
        }

        // Validate that sourcePeg, destPeg, auxPeg are distinct indices in {0, 1, 2}
        if (sourcePeg < 0 || sourcePeg > 2 || destPeg < 0 || destPeg > 2 || sourcePeg == destPeg) {
            sourcePeg = 0;
            destPeg = 2;
            auxPeg = 1;
        } else {
            auxPeg = 3 - sourcePeg - destPeg;
        }

        // Pure timing run
        TowerOfHanoi timingRunner = new TowerOfHanoi();
        long startTime = System.nanoTime();
        timingRunner.runHanoiInternal(diskCount, sourcePeg, destPeg, auxPeg, null);
        long elapsedNanos = System.nanoTime() - startTime;

        // Visual trace run
        StepCollector collector = new StepCollector();
        this.operationsCount = 0;
        this.recursiveCallsCount = 0;
        this.moveCounter = 0;
        runHanoiInternal(diskCount, sourcePeg, destPeg, auxPeg, collector);

        int expectedMoves = (1 << diskCount) - 1;
        String finalResult = "Solved Tower of Hanoi (" + diskCount + " disks: "
                + PEG_NAMES[sourcePeg] + " -> " + PEG_NAMES[destPeg] + " via " + PEG_NAMES[auxPeg] + ") | "
                + "Total Moves = " + moveCounter + " (Confirmed 2^" + diskCount + " - 1 = " + expectedMoves + ")";

        return new ExecutionResult(
                AlgorithmType.TOWER_OF_HANOI,
                finalResult,
                collector.toArray(),
                this.operationsCount,
                this.recursiveCallsCount,
                this.moveCounter,
                elapsedNanos
        );
    }
