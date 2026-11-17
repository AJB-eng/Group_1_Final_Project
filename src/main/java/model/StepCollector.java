package model;

/**
 * Custom dynamic array container for recording AlgorithmStep snapshots from scratch.
 * Uses a manually resized raw AlgorithmStep[] array.
 */
public class StepCollector {
    private AlgorithmStep[] buffer;
    private int size;
    private int capacity;

    public StepCollector() {
        this(64);
    }

    public StepCollector(int initialCapacity) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 16;
        this.buffer = new AlgorithmStep[this.capacity];
        this.size = 0;
    }

    /**
     * Appends an AlgorithmStep to the internal buffer, manually doubling capacity if full.
     */
    public void addStep(AlgorithmStep step) {
        if (size >= capacity) {
            resize(capacity * 2);
        }
        buffer[size] = step;
        size++;
    }

    /**
     * Convenience method to record an array/pointer-based step.
     */
    public void recordArrayStep(
            int[] arraySnapshot,
            int[] activeIndices,
            int[] swapIndices,
            int[] sortedIndices,
            int pointerA, String pointerALabel,
            int pointerB, String pointerBLabel,
            int pointerC, String pointerCLabel,
            int structureCapacity,
            long operationsCount,
            String description) {
        AlgorithmStep step = AlgorithmStep.createArrayStep(
                size + 1,
                arraySnapshot,
                activeIndices,
                swapIndices,
                sortedIndices,
                pointerA, pointerALabel,
                pointerB, pointerBLabel,
                pointerC, pointerCLabel,
                structureCapacity,
                operationsCount,
                description
        );
        addStep(step);
    }

    /**
     * Convenience method to record a Binary Search Tree step.
     */
    public void recordTreeStep(
            TreeNodeSnapshot rootSnapshot,
            int[] traversalSoFar,
            long operationsCount,
            String description) {
        AlgorithmStep step = AlgorithmStep.createTreeStep(
                size + 1,
                rootSnapshot,
                traversalSoFar,
                operationsCount,
                description
        );
        addStep(step);
    }

    /**
     * Convenience method to record a Tower of Hanoi step.
     */
    public void recordHanoiStep(
            int[][] pegsSnapshot,
            int movingDisk,
            int fromPeg,
            int toPeg,
            long operationsCount,
            String description) {
        AlgorithmStep step = AlgorithmStep.createHanoiStep(
                size + 1,
                pegsSnapshot,
                movingDisk,
                fromPeg,
                toPeg,
                operationsCount,
                description
        );
        addStep(step);
    }

    /**
     * Manually allocates a larger array and copies every element via a raw loop.
     */
    private void resize(int newCapacity) {
        AlgorithmStep[] nextBuffer = new AlgorithmStep[newCapacity];
        for (int i = 0; i < size; i++) {
            nextBuffer[i] = buffer[i];
        }
        buffer = nextBuffer;
        capacity = newCapacity;
    }

    public int size() {
        return size;
    }

    public AlgorithmStep get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return buffer[index];
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            buffer[i] = null;
        }
        size = 0;
    }

    /**
     * Returns an exact-length copy of the recorded steps array.
     */
    public AlgorithmStep[] toArray() {
        AlgorithmStep[] result = new AlgorithmStep[size];
        for (int i = 0; i < size; i++) {
            result[i] = buffer[i];
        }
        return result;
    }
}
