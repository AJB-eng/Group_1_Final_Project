package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module B(b)(ii): Custom FIFO Circular Queue implemented on a fixed-size array
 * using manual 'front' and 'rear' pointers with modular wrap-around arithmetic:
 * rear = (rear + 1) % capacity, front = (front + 1) % capacity.
 * Solves the Linear Queue "False Full" problem by reusing vacated front slots!
 */
public class CustomQueue {
    public static final int EMPTY_SLOT = Integer.MIN_VALUE;

    private int[] elements;
    private int front;
    private int rear;
    private int size;
    private int capacity;
    private long operationsCount;

    public CustomQueue() {
        this(6);
    }

    public CustomQueue(int initialCapacity) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 6;
        this.elements = new int[this.capacity];
        clear();
    }

    public void setFixedCapacity(int newCapacity) {
        this.capacity = newCapacity > 0 ? newCapacity : 6;
        this.elements = new int[this.capacity];
        clear();
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

    public boolean isFull() {
        return size == capacity;
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

    /**
     * Enqueues a value at rear = (rear + 1) % capacity.
     * Reuses vacated front slots via modular wrap-around! Time Complexity: O(1).
     */
    public boolean enqueue(int value, StepCollector collector) {
        operationsCount++;
        if (isFull()) {
            if (collector != null) {
                collector.recordArrayStep(
                        getPhysicalArraySnapshot(),
                        new int[0],
                        new int[]{rear >= 0 ? rear : 0},
                        getOccupiedPhysicalIndices(),
                        front, "FRONT=" + front,
                        rear, "REAR=" + rear + " (FULL)",
                        -1, "OVERFLOW",
                        capacity,
                        operationsCount,
                        "Queue Overflow! Circular Queue is full (size = " + size + "/" + capacity + "). Cannot enqueue " + value + "."
                );
            }
            return false;
        }

        int oldRear = rear;
        rear = (rear + 1) % capacity;
        boolean wrapped = (oldRear == capacity - 1 && rear == 0);
        elements[rear] = value;
        size++;
        operationsCount += 3;

        if (collector != null) {
            collector.recordArrayStep(
                    getPhysicalArraySnapshot(),
                    wrapped ? new int[]{rear} : new int[0],
                    new int[0],
                    getOccupiedPhysicalIndices(),
                    front, "FRONT=" + front,
                    rear, wrapped ? "REAR=" + rear + " (WRAPPED!)" : "REAR=" + rear,
                    rear, "ENQ " + value,
                    capacity,
                    operationsCount,
                    "Enqueue(" + value + "): rear = (" + oldRear + " + 1) % " + capacity + " -> index " + rear
                            + (wrapped ? " [WRAPPED AROUND to start of array, reusing vacated slot 0!]" : "")
                            + " (size=" + size + "/" + capacity + ")."
            );
        }
        return true;
    }

    /**
     * Dequeues and returns the front element, advancing front = (front + 1) % capacity. Time Complexity: O(1).
     */
    public int dequeue(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        getPhysicalArraySnapshot(),
                        new int[0], new int[0], new int[0],
                        front, "FRONT (EMPTY)", rear, "REAR (EMPTY)", -1, "UNDERFLOW",
                        capacity, operationsCount,
                        "Queue Underflow! Circular Queue is empty (size == 0)."
                );
            }
            return EMPTY_SLOT;
        }

        int removedValue = elements[front];
        int oldFront = front;
        elements[front] = EMPTY_SLOT;
        front = (front + 1) % capacity;
        size--;
        operationsCount += 4;

        if (size == 0) {
            front = 0;
            rear = -1;
        }

        if (collector != null) {
            collector.recordArrayStep(
                    getPhysicalArraySnapshot(),
                    new int[0],
                    new int[]{oldFront},
                    getOccupiedPhysicalIndices(),
                    front, "FRONT=" + front,
                    rear, "REAR=" + rear,
                    oldFront, "DEQ " + removedValue,
                    capacity,
                    operationsCount,
                    "Dequeued " + removedValue + " from slot " + oldFront
                            + ". Advanced front = (" + oldFront + " + 1) % " + capacity + " -> " + front
                            + " (size=" + size + "/" + capacity + ")."
            );
        }
        return removedValue;
    }

    /**
     * Peeks at the front element without removing it. Time Complexity: O(1).
     */
    public int peekFront(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        getPhysicalArraySnapshot(), new int[0], new int[0], new int[0],
                        front, "EMPTY", rear, "EMPTY", -1, "", capacity, operationsCount,
                        "Front() failed: Circular Queue is empty."
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
                    getOccupiedPhysicalIndices(),
                    front, "FRONT=" + front,
                    rear, "REAR=" + rear,
                    front, "FRONT=" + val,
                    capacity,
                    operationsCount,
                    "Front element is " + val + " at physical index " + front + "."
            );
        }
        return val;
    }

    public int[] getPhysicalArraySnapshot() {
        return ArrayUtils.copyArray(elements);
    }

    private int[] getOccupiedPhysicalIndices() {
        if (size <= 0) {
            return new int[0];
        }
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = (front + i) % capacity;
        }
        return indices;
    }

    public int[] toLogicalArrayRaw() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = elements[(front + i) % capacity];
        }
        return result;
    }

    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, String mode) {
        StepCollector collector = new StepCollector();

        CustomQueue timingQueue = new CustomQueue(this.capacity);
        long startTime = System.nanoTime();
        runModeInternal(timingQueue, inputValues, targetValue, mode, null);
        long elapsedNanos = System.nanoTime() - startTime;

        resetCounter();
        String summary = runModeInternal(this, inputValues, targetValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.CUSTOM_QUEUE,
                summary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private String runModeInternal(
            CustomQueue queue,
            int[] inputValues,
            int targetValue,
            String mode,
            StepCollector collector) {
        if ("SET_CAPACITY".equals(mode)) {
            int cap = targetValue > 0 ? targetValue : 6;
            queue.setFixedCapacity(cap);
            queue.operationsCount++;
            if (collector == null) return "";
            collector.recordArrayStep(
                    queue.getPhysicalArraySnapshot(), new int[0], new int[0], new int[0],
                    queue.front, "FRONT=" + queue.front, queue.rear, "REAR=" + queue.rear, -1, "", queue.capacity,
                    queue.operationsCount, "Set Circular Queue fixed capacity to " + cap + "."
            );
            return "Circular Queue capacity set to " + cap;
        } else if ("ENQUEUE".equals(mode)) {
            boolean ok = queue.enqueue(targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Enqueued " + targetValue : "Queue is Full! (size=" + queue.size() + "/" + queue.getCapacity() + ")")
                    + " | Logical Order: " + ArrayUtils.arrayToString(queue.toLogicalArrayRaw())
                    + " [front=" + queue.getFrontIndex() + ", rear=" + queue.getRearIndex() + "]";
        } else if ("DEQUEUE".equals(mode)) {
            int dq = queue.dequeue(collector);
            if (collector == null) return "";
            return (dq == EMPTY_SLOT ? "Queue is Empty (Underflow)" : "Dequeued " + dq)
                    + " | Logical Order: " + ArrayUtils.arrayToString(queue.toLogicalArrayRaw())
                    + " [front=" + queue.getFrontIndex() + ", rear=" + queue.getRearIndex() + "]";
        } else if ("PEEK".equals(mode)) {
            int pk = queue.peekFront(collector);
            if (collector == null) return "";
            return (pk == EMPTY_SLOT ? "Queue is Empty" : "Front = " + pk + " (index " + queue.getFrontIndex() + ")")
                    + " | Logical Order: " + ArrayUtils.arrayToString(queue.toLogicalArrayRaw());
        } else if ("STATUS".equals(mode)) {
            queue.operationsCount += 3;
            if (collector == null) return "";
            String msg = "size=" + queue.size() + ", is_empty=" + queue.isEmpty() + ", is_full=" + queue.isFull()
                    + ", front=" + queue.getFrontIndex() + ", rear=" + queue.getRearIndex()
                    + " | Elements: " + ArrayUtils.arrayToString(queue.toLogicalArrayRaw());
            collector.recordArrayStep(
                    queue.getPhysicalArraySnapshot(), new int[0], new int[0], queue.getOccupiedPhysicalIndices(),
                    queue.front, "FRONT=" + queue.front, queue.rear, "REAR=" + queue.rear, -1, "", queue.capacity,
                    queue.operationsCount, msg
            );
            return msg;
        } else {
            // Default demo: fill capacity, dequeue 2, then enqueue 2 more so rear WRAPS AROUND to indices 0 and 1!
            queue.clear();
            int cap = queue.getCapacity();
            for (int i = 0; i < cap; i++) {
                int val = i < inputValues.length ? inputValues[i] : (i + 1) * 10;
                queue.enqueue(val, collector);
            }
            queue.dequeue(collector);
            queue.dequeue(collector);
            queue.enqueue(targetValue, collector);
            queue.enqueue(targetValue + 11, collector);
            if (collector == null) return "";
            return "Circular Wrap-Around Demonstrated! Logical FIFO: " + ArrayUtils.arrayToString(queue.toLogicalArrayRaw())
                    + " | front=" + queue.getFrontIndex() + ", rear=" + queue.getRearIndex()
                    + " (wrapped to start!), size=" + queue.size() + "/" + queue.getCapacity();
        }
    }
}
