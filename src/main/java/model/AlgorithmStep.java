package model;

/**
 * Snapshot/State object representing one discrete step of an algorithm or data structure execution.
 * Stores raw primitive arrays and custom node snapshots without any java.util collection dependencies.
 */
public class AlgorithmStep {
    private final int stepNumber;
    private final int[] arraySnapshot;
    private final int[] activeIndices;   // Yellow: comparing / inspecting
    private final int[] swapIndices;     // Red: swapping / deleting / overwriting
    private final int[] sortedIndices;   // Green: sorted / found / finalized
    private final int pointerA;          // e.g., head, top, front, low, left, i
    private final int pointerB;          // e.g., tail, rear, high, right, j
    private final int pointerC;          // e.g., mid, pivot, current
    private final String pointerALabel;
    private final String pointerBLabel;
    private final String pointerCLabel;
    private final int structureCapacity; // Used by Stack / Queue to display capacity slots
    private final TreeNodeSnapshot treeRootSnapshot; // Used by BST
    private final int[][] pegsSnapshot;  // Used by Tower of Hanoi (3 pegs)
    private final int movingDisk;        // Disk currently moving in Hanoi (-1 if none)
    private final int fromPeg;           // Source peg index in Hanoi (-1 if none)
    private final int toPeg;             // Destination peg index in Hanoi (-1 if none)
    private final long operationsCount;  // Cumulative operation count at this step
    private final String description;    // Human-readable log message for this step

    public AlgorithmStep(
            int stepNumber,
            int[] arraySnapshot,
            int[] activeIndices,
            int[] swapIndices,
            int[] sortedIndices,
            int pointerA,
            String pointerALabel,
            int pointerB,
            String pointerBLabel,
            int pointerC,
            String pointerCLabel,
            int structureCapacity,
            TreeNodeSnapshot treeRootSnapshot,
            int[][] pegsSnapshot,
            int movingDisk,
            int fromPeg,
            int toPeg,
            long operationsCount,
            String description) {
        this.stepNumber = stepNumber;
        this.arraySnapshot = ArrayUtils.copyArray(arraySnapshot);
        this.activeIndices = ArrayUtils.copyArray(activeIndices);
        this.swapIndices = ArrayUtils.copyArray(swapIndices);
        this.sortedIndices = ArrayUtils.copyArray(sortedIndices);
        this.pointerA = pointerA;
        this.pointerALabel = pointerALabel != null ? pointerALabel : "";
        this.pointerB = pointerB;
        this.pointerBLabel = pointerBLabel != null ? pointerBLabel : "";
        this.pointerC = pointerC;
        this.pointerCLabel = pointerCLabel != null ? pointerCLabel : "";
        this.structureCapacity = structureCapacity;
        this.treeRootSnapshot = treeRootSnapshot;
        this.pegsSnapshot = ArrayUtils.copy2DArray(pegsSnapshot);
        this.movingDisk = movingDisk;
        this.fromPeg = fromPeg;
        this.toPeg = toPeg;
        this.operationsCount = operationsCount;
        this.description = description != null ? description : "";
    }

    /**
     * Convenience constructor for array, linked list, stack, and queue steps.
     */
    public static AlgorithmStep createArrayStep(
            int stepNumber,
            int[] arraySnapshot,
            int[] activeIndices,
            int[] swapIndices,
            int[] sortedIndices,
            int pointerA,
            String pointerALabel,
            int pointerB,
            String pointerBLabel,
            int pointerC,
            String pointerCLabel,
            int capacity,
            long operationsCount,
            String description) {
        return new AlgorithmStep(
                stepNumber,
                arraySnapshot,
                activeIndices,
                swapIndices,
                sortedIndices,
                pointerA,
                pointerALabel,
                pointerB,
                pointerBLabel,
                pointerC,
                pointerCLabel,
                capacity,
                null,
                null,
                -1,
                -1,
                -1,
                operationsCount,
                description
        );
    }

    /**
     * Convenience constructor for Binary Search Tree steps.
     */
    public static AlgorithmStep createTreeStep(
            int stepNumber,
            TreeNodeSnapshot rootSnapshot,
            int[] traversalSoFar,
            long operationsCount,
            String description) {
        return new AlgorithmStep(
                stepNumber,
                traversalSoFar,
                new int[0],
                new int[0],
                new int[0],
                -1, "",
                -1, "",
                -1, "",
                0,
                rootSnapshot,
                null,
                -1, -1, -1,
                operationsCount,
                description
        );
    }

