package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module B(b)(i): Custom Linear Queue on a fixed-size array.
 * Implements enqueue, dequeue, front, size, is_empty, is_full, and display all elements.
 * Explicitly demonstrates the "False Full" problem (when rear == capacity - 1 after dequeues
 * leave unused slots at 0 .. front-1) and explains how the Circular Queue solves it.
 */
public class CustomLinearQueue {
    public static final int EMPTY_SLOT = Integer.MIN_VALUE;

    private int[] elements;
    private int front;
    private int rear;
    private int size;
    private int capacity;
    private long operationsCount;

    public CustomLinearQueue() {
        this(6);
    }

    public CustomLinearQueue(int initialCapacity) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 6;
        this.elements = new int[this.capacity];
        clear();
    }

    public void setFixedCapacity(int newCapacity) {
        this.capacity = newCapacity > 0 ? newCapacity : 6;
        this.elements = new int[this.capacity];
        clear();
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            elements[i] = EMPTY_SLOT;
        }
        this.front = 0;
        this.rear = -1;
        this.size = 0;
        this.operationsCount = 0;
    }

    public void resetCounter() {
        this.operationsCount = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getFrontIndex() {
        return front;
    }

    public int getRearIndex() {
        return rear;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * In a Linear Queue without wrap-around, isFull() triggers whenever rear reaches capacity - 1,
     * even if front > 0 (causing the "False Full" condition when size < capacity).
     */
    public boolean isFull() {
        return rear == capacity - 1;
    }

    public boolean isFalseFull() {
        return rear == capacity - 1 && front > 0 && size < capacity;
    }

    /**
     * Enqueues a value at rear + 1 (without modular wrap-around). Time Complexity: O(1).
     */
    public boolean enqueue(int value, StepCollector collector) {
        operationsCount++;
        if (isFull()) {
            String reason = isFalseFull()
                    ? "FALSE FULL! rear == " + rear + " (capacity - 1), even though front == " + front
                    + " leaves " + front + " vacant slot(s) at index 0.." + (front - 1)
                    + "! Circular Queue solves this via rear = (rear + 1) % capacity."
                    : "Linear Queue is Truly Full (size=" + size + ", capacity=" + capacity + ", rear=" + rear + ").";
            if (collector != null) {
                collector.recordArrayStep(
                         getPhysicalArraySnapshot(),
                        front > 0 ? ArrayUtils.rangeIndices(0, front - 1) : new int[0],
                        new int[]{rear},
                        size > 0 ? ArrayUtils.rangeIndices(front, rear) : new int[0],
                        front, "FRONT=" + front,
                        rear, isFalseFull() ? "REAR=" + rear + " (FALSE FULL!)" : "REAR=" + rear + " (FULL)",
                        -1, "",
                        capacity,
                        operationsCount,
                        "Cannot enqueue " + value + ": " + reason
                );
            }
            return false;
        }

        rear++;
        elements[rear] = value;
        size++;
        operationsCount += 3;

        if (collector != null) {
            collector.recordArrayStep(
                    getPhysicalArraySnapshot(),
                    new int[0],
                    new int[0],
                    ArrayUtils.rangeIndices(front, rear),
                    front, "FRONT=" + front,
                    rear, "REAR=" + rear,
                    rear, "ENQUEUED " + value,
                    capacity,
                    operationsCount,
                    "Enqueue(" + value + "): rear incremented to " + rear + " (front=" + front + ", size=" + size + "/" + capacity + ")."
            );
        }
        return true;
    }

    /**
     * Dequeues the element at front and increments front++. Time Complexity: O(1).
     */
    public int dequeue(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        getPhysicalArraySnapshot(),
                        new int[0], new int[0], new int[0],
                        front, "FRONT=" + front + " (EMPTY)",
                        rear, "REAR=" + rear,
                        -1, "UNDERFLOW",
                        capacity,
                        operationsCount,
                        "Queue Underflow! Linear Queue is empty (size == 0)."
                );
            }
            return EMPTY_SLOT;
        }

        int removed = elements[front];
        int oldFront = front;
        elements[front] = EMPTY_SLOT;
        front++;
        size--;
        operationsCount += 4;

        if (collector != null) {
            collector.recordArrayStep(
                    getPhysicalArraySnapshot(),
                    ArrayUtils.rangeIndices(0, oldFront),
                    new int[]{oldFront},
                    size > 0 ? ArrayUtils.rangeIndices(front, rear) : new int[0],
                    front, "FRONT=" + front,
                    rear, "REAR=" + rear,
                    oldFront, "VACATED[" + oldFront + "]",
                    capacity,
                    operationsCount,
                    "Dequeued " + removed + " from index " + oldFront + ". Front advanced to " + front
                            + " (slot " + oldFront + " is now unusable in a Linear Queue!)."
            );
        }
        return removed;
    }

    /**
     * Returns the front element without removing it. Time Complexity: O(1).
     */
    public int front(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        getPhysicalArraySnapshot(), new int[0], new int[0], new int[0],
                        front, "EMPTY", rear, "REAR=" + rear, -1, "", capacity, operationsCount,
                        "Front() failed: Linear Queue is empty."
                );
            }
            return EMPTY_SLOT;
        }
        int val = elements[front];
        operationsCount++;
        if (collector != null) {
            collector.recordArrayStep(
                    getPhysicalArraySnapshot(),
                    new int[]{front},
                    new int[0],
                    ArrayUtils.rangeIndices(front, rear),
                    front, "FRONT=" + front,
                    rear, "REAR=" + rear,
                    front, "FRONT VAL=" + val,
                    capacity,
                    operationsCount,
                    "Front element is " + val + " at index " + front + "."
            );
        }
        return val;
    }

    public int[] getPhysicalArraySnapshot() {
        return ArrayUtils.copyArray(elements);
    }

    public int[] getLogicalElements() {
        if (size <= 0) {
            return new int[0];
        }
        int[] res = new int[size];
        for (int i = 0; i < size; i++) {
            res[i] = elements[front + i];
        }
        return res;
    }

    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, String mode) {
        StepCollector collector = new StepCollector();

        CustomLinearQueue timingQ = new CustomLinearQueue(this.capacity);
        long t0 = System.nanoTime();
        runModeInternal(timingQ, inputValues, targetValue, mode, null);
        long elapsedNanos = System.nanoTime() - t0;

        resetCounter();
        String summary = runModeInternal(this, inputValues, targetValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.LINEAR_QUEUE,
                summary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private String runModeInternal(
            CustomLinearQueue q,
            int[] inputValues,
            int targetValue,
            String mode,
            StepCollector collector) {
        if ("SET_CAPACITY".equals(mode)) {
            int cap = targetValue > 0 ? targetValue : 5;
            q.setFixedCapacity(cap);
            q.operationsCount++;
            if (collector == null) return "";
            collector.recordArrayStep(
                    q.getPhysicalArraySnapshot(), new int[0], new int[0], new int[0],
                    q.front, "FRONT=" + q.front, q.rear, "REAR=" + q.rear, -1, "", q.capacity,
                    q.operationsCount, "Set Linear Queue fixed capacity to " + cap + "."
            );
            return "Linear Queue capacity set to " + cap;
        } else if ("ENQUEUE".equals(mode)) {
            boolean ok = q.enqueue(targetValue, collector);
            if (collector == null) return "";
            if (!ok) {
                return (q.isFalseFull()
                        ? "FALSE FULL! rear=" + q.rear + " reached end while front=" + q.front + " (" + q.front + " wasted slots at start!)"
                        : "Queue is Full! Cannot enqueue " + targetValue)
                        + " | Active: " + ArrayUtils.arrayToString(q.getLogicalElements());
            }
            return "Enqueued " + targetValue + " | Active: " + ArrayUtils.arrayToString(q.getLogicalElements())
                    + " (front=" + q.front + ", rear=" + q.rear + ")";
        } else if ("DEQUEUE".equals(mode)) {
            int dq = q.dequeue(collector);
            if (collector == null) return "";
            return (dq == EMPTY_SLOT ? "Queue is Empty (Underflow)" : "Dequeued " + dq)
                    + " | Active: " + ArrayUtils.arrayToString(q.getLogicalElements())
                    + " (front=" + q.front + ", rear=" + q.rear + ")";
        } else if ("FRONT".equals(mode)) {
            int f = q.front(collector);
            if (collector == null) return "";
            return (f == EMPTY_SLOT ? "Queue is Empty" : "Front = " + f + " (at index " + q.front + ")")
                    + " | Active: " + ArrayUtils.arrayToString(q.getLogicalElements());
        } else if ("STATUS".equals(mode)) {
            q.operationsCount += 3;
            if (collector == null) return "";
            String msg = "size=" + q.size() + ", is_empty=" + q.isEmpty() + ", is_full=" + q.isFull()
                    + (q.isFalseFull() ? " [FALSE FULL!]" : "")
                    + " | Active(front=" + q.front + "..rear=" + q.rear + "): " + ArrayUtils.arrayToString(q.getLogicalElements());
            collector.recordArrayStep(
                    q.getPhysicalArraySnapshot(),
                    q.front > 0 ? ArrayUtils.rangeIndices(0, q.front - 1) : new int[0],
                    new int[0],
                    q.size > 0 ? ArrayUtils.rangeIndices(q.front, q.rear) : new int[0],
                    q.front, "FRONT=" + q.front, q.rear, "REAR=" + q.rear, -1, "", q.capacity, q.operationsCount, msg
            );
            return msg;
        } else {
            // Default / FALSE_FULL_DEMO: Fill queue to capacity, dequeue 2 items, then try to enqueue targetValue
            // to visually demonstrate the "False Full" problem!
            q.clear();
            int cap = q.getCapacity();
            for (int i = 0; i < cap; i++) {
                int val = i < inputValues.length ? inputValues[i] : (i + 1) * 10;
                q.enqueue(val, collector);
            }
            // Dequeue 2 elements from front so slots 0 and 1 become vacant
            q.dequeue(collector);
            if (!q.isEmpty()) {
                q.dequeue(collector);
            }
            // Now try to enqueue targetValue -> triggers FALSE FULL because rear == capacity - 1!
            q.enqueue(targetValue, collector);
            if (collector == null) return "";
            return "FALSE FULL Demonstrated! Active: " + ArrayUtils.arrayToString(q.getLogicalElements())
                    + " | front=" + q.front + ", rear=" + q.rear + ", size=" + q.size + "/" + q.capacity
                    + " (Slots 0.." + (q.front - 1) + " are empty, but rear == capacity-1 blocks enqueue! Circular Queue solves this.)";
        }
    }
}
