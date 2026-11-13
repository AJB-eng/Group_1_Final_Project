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
