package model;

/**
 * Encapsulates all outputs produced by executing an algorithm, data structure operation,
 * or empirical benchmark:
 * - Final result summary
 * - Step-by-step trace array (AlgorithmStep[])
 * - Explicit comparisons count, swaps/moves count, and total basic operations count
 * - Pure algorithmic execution time in nanoseconds (System.nanoTime())
 */
public class ExecutionResult {
    private final AlgorithmType algorithmType;
    private final String finalResult;
    private final AlgorithmStep[] steps;
    private final long totalOperations;
    private final long comparisonsCount;
    private final long swapsOrMovesCount;
    private final long executionTimeNanos;
    private final ComplexityInfo complexityInfo;
    private final VisualizationType visualizationType;
    private final String[] tableHeaders;
    private final String[][] tableRows;

    public ExecutionResult(
            AlgorithmType algorithmType,
            String finalResult,
            AlgorithmStep[] steps,
            long totalOperations,
            long executionTimeNanos) {
        this(algorithmType, finalResult, steps, totalOperations, totalOperations, 0, executionTimeNanos, null, null);
    }

    public ExecutionResult(
            AlgorithmType algorithmType,
            String finalResult,
            AlgorithmStep[] steps,
            long totalOperations,
            long comparisonsCount,
            long swapsOrMovesCount,
            long executionTimeNanos) {
        this(algorithmType, finalResult, steps, totalOperations, comparisonsCount, swapsOrMovesCount, executionTimeNanos, null, null);
    }

    public ExecutionResult(
            AlgorithmType algorithmType,
            String finalResult,
            AlgorithmStep[] steps,
            long totalOperations,
            long comparisonsCount,
            long swapsOrMovesCount,
            long executionTimeNanos,
            String[] tableHeaders,
            String[][] tableRows) {
        this.algorithmType = algorithmType;
        this.finalResult = finalResult;
        this.steps = steps != null ? steps : new AlgorithmStep[0];
        this.totalOperations = totalOperations;
        this.comparisonsCount = comparisonsCount;
        this.swapsOrMovesCount = swapsOrMovesCount;
        this.executionTimeNanos = executionTimeNanos;
        this.complexityInfo = algorithmType.getComplexityInfo();
        this.visualizationType = algorithmType.getVisualizationType();
        this.tableHeaders = tableHeaders;
        this.tableRows = tableRows;
    }
public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public AlgorithmStep[] getSteps() {
        return steps;
    }

    public long getTotalOperations() {
        return totalOperations;
    }

    public long getComparisonsCount() {
        return comparisonsCount;
    }

    public long getSwapsOrMovesCount() {
        return swapsOrMovesCount;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public double getExecutionTimeMicros() {
        return executionTimeNanos / 1_000.0;
    }

    public double getExecutionTimeMillis() {
        return executionTimeNanos / 1_000_000.0;
    }

    public ComplexityInfo getComplexityInfo() {
        return complexityInfo;
    }

    public VisualizationType getVisualizationType() {
        return visualizationType;
    }

    public String[] getTableHeaders() {
        return tableHeaders;
    }

    public String[][] getTableRows() {
        return tableRows;
    }
}

