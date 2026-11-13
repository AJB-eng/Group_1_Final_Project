package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module B(c)(i): Custom Singly Linked List implemented from first principles using Node pointers.
 * Supports all required CSC2107 operations:
 * - Insertion: at beginning, at end, before a given element, after a given element
 * - Deletion: at beginning, at end, before a given element, after a given element, of a given element
 * - Search for an element, size, and forward display.
 */
public class CustomSinglyLinkedList {
    private Node head;
    private int size;
    private long operationsCount;

    public CustomSinglyLinkedList() {
        this.head = null;
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

    public boolean isEmpty() {
        return head == null;
    }

    public void clear() {
        head = null;
        size = 0;
        operationsCount = 0;
    }

    /**
     * Inserts a new Node at the beginning (head). Time Complexity: O(1).
     */
    public void insertAtHead(int value, StepCollector collector) {
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
        size++;
        operationsCount += 3;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(), new int[0], new int[0], new int[]{0},
                    0, "HEAD", size - 1, "TAIL", 0, "INSERTED " + value, size, operationsCount,
                    "Inserted " + value + " at beginning (HEAD). Size = " + size
            );
        }
    }

    /**
     * Inserts a new Node at the end (tail). Time Complexity: O(n).
     */
    public void insertAtTail(int value, StepCollector collector) {
        Node newNode = new Node(value);
        operationsCount++;

        if (head == null) {
            head = newNode;
            size++;
            operationsCount += 2;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayRaw(), new int[0], new int[0], new int[]{0},
                        0, "HEAD", 0, "TAIL", 0, "NEW " + value, size, operationsCount,
                        "List was empty. Inserted " + value + " as HEAD/TAIL."
                );
            }
            return;
        }

        Node curr = head;
        int idx = 0;
        while (curr.next != null) {
            operationsCount++;
            curr = curr.next;
            idx++;
        }
        curr.next = newNode;
        size++;
        operationsCount += 2;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(), new int[0], new int[0], new int[]{size - 1},
                    0, "HEAD", size - 1, "TAIL", size - 1, "INSERTED " + value, size, operationsCount,
                    "Inserted " + value + " at end (TAIL, index " + (size - 1) + "). Size = " + size
            );
        }
    }

    /**
     * Inserts newValue immediately BEFORE the first occurrence of targetElement. Time Complexity: O(n).
     */
    public boolean insertBefore(int targetElement, int newValue, StepCollector collector) {
        operationsCount++;
        if (head == null) {
            recordMessageStep(collector, "Cannot insert before " + targetElement + ": list is empty.");
            return false;
        }
        if (head.data == targetElement) {
            insertAtHead(newValue, collector);
            return true;
        }

        Node prev = head;
        Node curr = head.next;
        int idx = 1;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                Node newNode = new Node(newValue);
                newNode.next = curr;
                prev.next = newNode;
                size++;
                operationsCount += 3;
                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayRaw(), new int[]{idx + 1}, new int[0], new int[]{idx},
                            0, "HEAD", size - 1, "TAIL", idx, "BEFORE " + targetElement, size, operationsCount,
                            "Inserted " + newValue + " before " + targetElement + " at index " + idx + "."
                    );
                }
                return true;
            }
            prev = curr;
            curr = curr.next;
            idx++;
        }
        recordMessageStep(collector, "Element " + targetElement + " not found; could not insert " + newValue + " before it.");
        return false;
    }

    /**
     * Inserts newValue immediately AFTER the first occurrence of targetElement. Time Complexity: O(n).
     */
    public boolean insertAfter(int targetElement, int newValue, StepCollector collector) {
        Node curr = head;
        int idx = 0;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                Node newNode = new Node(newValue);
                newNode.next = curr.next;
                curr.next = newNode;
                size++;
                operationsCount += 3;
                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayRaw(), new int[]{idx}, new int[0], new int[]{idx + 1},
                            0, "HEAD", size - 1, "TAIL", idx + 1, "AFTER " + targetElement, size, operationsCount,
                            "Inserted " + newValue + " after " + targetElement + " at index " + (idx + 1) + "."
                    );
                }
                return true;
            }
            curr = curr.next;
            idx++;
        }
        recordMessageStep(collector, "Element " + targetElement + " not found; could not insert " + newValue + " after it.");
        return false;
    }

    /**
     * Deletes the first node (at beginning / head). Time Complexity: O(1).
     */
    public int deleteAtHead(StepCollector collector) {
        operationsCount++;
        if (head == null) {
            recordMessageStep(collector, "Cannot delete at beginning: list is empty.");
            return Integer.MIN_VALUE;
        }
        int removed = head.data;
        head = head.next;
        size--;
        operationsCount += 2;
        recordMessageStep(collector, "Deleted " + removed + " from beginning (HEAD). Size = " + size);
        return removed;
    }

    /**
     * Deletes the last node (at end / tail). Time Complexity: O(n).
     */
    public int deleteAtTail(StepCollector collector) {
        operationsCount++;
        if (head == null) {
            recordMessageStep(collector, "Cannot delete at end: list is empty.");
            return Integer.MIN_VALUE;
        }
        if (head.next == null) {
            int removed = head.data;
            head = null;
            size = 0;
            operationsCount += 2;
            recordMessageStep(collector, "Deleted only node " + removed + " from end. List is now empty.");
            return removed;
        }

        Node prev = head;
        Node curr = head.next;
        while (curr.next != null) {
            operationsCount++;
            prev = curr;
            curr = curr.next;
        }
        int removed = curr.data;
        prev.next = null;
        size--;
        operationsCount += 2;
        recordMessageStep(collector, "Deleted " + removed + " from end (TAIL). Size = " + size);
        return removed;
    }

    /**
     * Deletes the node immediately BEFORE targetElement. Time Complexity: O(n).
     */
    public int deleteBefore(int targetElement, StepCollector collector) {
        operationsCount++;
        if (head == null || head.next == null) {
            recordMessageStep(collector, "Cannot delete before " + targetElement + ": list has fewer than 2 nodes.");
            return Integer.MIN_VALUE;
        }
        if (head.data == targetElement) {
            recordMessageStep(collector, "No node exists before HEAD (" + targetElement + ").");
            return Integer.MIN_VALUE;
        }
        if (head.next.data == targetElement) {
            return deleteAtHead(collector);
        }

        Node prevPrev = head;
        Node prev = head.next;
        Node curr = prev.next;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                int removed = prev.data;
                prevPrev.next = curr;
                size--;
                operationsCount += 2;
                recordMessageStep(collector, "Deleted " + removed + " (which was before " + targetElement + "). Size = " + size);
                return removed;
            }
            prevPrev = prev;
            prev = curr;
            curr = curr.next;
        }
        recordMessageStep(collector, "Element " + targetElement + " not found.");
        return Integer.MIN_VALUE;
    }

    /**
     * Deletes the node immediately AFTER targetElement. Time Complexity: O(n).
     */
    public int deleteAfter(int targetElement, StepCollector collector) {
        Node curr = head;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                if (curr.next == null) {
                    recordMessageStep(collector, "Element " + targetElement + " is at TAIL; no node exists after it.");
                    return Integer.MIN_VALUE;
                }
                int removed = curr.next.data;
                curr.next = curr.next.next;
                size--;
                operationsCount += 2;
                recordMessageStep(collector, "Deleted " + removed + " (which was after " + targetElement + "). Size = " + size);
                return removed;
            }
            curr = curr.next;
        }
        recordMessageStep(collector, "Element " + targetElement + " not found.");
        return Integer.MIN_VALUE;
    }

    /**
     * Deletes the first occurrence of the given value. Time Complexity: O(n).
     */
    public boolean deleteByValue(int value, StepCollector collector) {
        operationsCount++;
        if (head == null) {
            recordMessageStep(collector, "Cannot delete " + value + ": list is empty.");
            return false;
        }
        if (head.data == value) {
            head = head.next;
            size--;
            operationsCount += 2;
            recordMessageStep(collector, "Deleted " + value + " from HEAD. Size = " + size);
            return true;
        }

        Node prev = head;
        Node current = head.next;
        while (current != null) {
            operationsCount += 2;
            if (current.data == value) {
                prev.next = current.next;
                size--;
                operationsCount += 2;
                recordMessageStep(collector, "Deleted " + value + " from list. Size = " + size);
                return true;
            }
            prev = current;
            current = current.next;
        }
        recordMessageStep(collector, "Value " + value + " not found in list.");
        return false;
    }

    /**
     * Searches for target and returns its 0-based index, or -1 if not found. Time Complexity: O(n).
     */
    public int search(int target, StepCollector collector) {
        Node current = head;
        int index = 0;
        while (current != null) {
            operationsCount += 2;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayRaw(), new int[]{index}, new int[0], new int[0],
                        0, "HEAD", size - 1, "TAIL", index, "SCAN", size, operationsCount,
                        "Searching for " + target + ": checking node [" + current.data + "] at index " + index
                );
            }
            if (current.data == target) {
                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayRaw(), new int[0], new int[0], new int[]{index},
                            0, "HEAD", size - 1, "TAIL", index, "FOUND " + target, size, operationsCount,
                            "Found " + target + " at index " + index + "!"
                    );
                }
                return index;
            }
            current = current.next;
            index++;
        }
        recordMessageStep(collector, "Value " + target + " not found in list.");
        return -1;
    }

    /**
     * Displays all elements forward from HEAD to NULL. Time Complexity: O(n).
     */
    public int[] traverse(StepCollector collector) {
        int[] result = new int[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            operationsCount++;
            result[index] = current.data;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayRaw(), new int[]{index}, new int[0], ArrayUtils.rangeIndices(0, index),
                        0, "HEAD", size - 1, "TAIL", index, "VISIT", size, operationsCount,
                        "Forward Display step " + (index + 1) + ": visited [" + current.data + "]"
                );
            }
            current = current.next;
            index++;
        }
        return result;
    }

    private void recordMessageStep(StepCollector collector, String message) {
        if (collector != null) {
            int[] snap = toArrayRaw();
            collector.recordArrayStep(
                    snap, new int[0], new int[0], new int[0],
                    size > 0 ? 0 : -1, "HEAD",
                    size > 0 ? size - 1 : -1, "TAIL",
                    -1, "", size, operationsCount, message
            );
        }
    }

    public int[] toArrayRaw() {
        int[] arr = new int[size];
        Node curr = head;
        int i = 0;
        while (curr != null && i < size) {
            arr[i++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, String mode) {
        int auxVal = inputValues != null && inputValues.length > 0 ? inputValues[0] : 99;
        return executeFullWorkflow(inputValues, targetValue, auxVal, mode);
    }

    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, int secondValue, String mode) {
        StepCollector collector = new StepCollector();

        new Node(0);
        CustomSinglyLinkedList timingList = new CustomSinglyLinkedList();
        int[] existing = toArrayRaw();
        for (int i = 0; i < existing.length; i++) {
            timingList.insertAtTail(existing[i], null);
        }
        timingList.resetCounter();

        long startTime = System.nanoTime();
        runModeInternal(timingList, inputValues, targetValue, secondValue, mode, null);
        long elapsedNanos = System.nanoTime() - startTime;

        resetCounter();
        String finalSummary = runModeInternal(this, inputValues, targetValue, secondValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.SINGLY_LINKED_LIST,
                finalSummary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private String runModeInternal(
            CustomSinglyLinkedList list,
            int[] inputValues,
            int targetValue,
            int secondValue,
            String mode,
            StepCollector collector) {
        if ("INSERT_HEAD".equals(mode)) {
            list.insertAtHead(targetValue, collector);
            if (collector == null) return "";
            return "Inserted " + targetValue + " at Beginning | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("INSERT_TAIL".equals(mode)) {
            list.insertAtTail(targetValue, collector);
            if (collector == null) return "";
            return "Inserted " + targetValue + " at End | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("INSERT_BEFORE".equals(mode)) {
            boolean ok = list.insertBefore(secondValue, targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Inserted " + targetValue + " before " + secondValue : "Target " + secondValue + " not found")
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("INSERT_AFTER".equals(mode)) {
            boolean ok = list.insertAfter(secondValue, targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Inserted " + targetValue + " after " + secondValue : "Target " + secondValue + " not found")
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("DELETE_HEAD".equals(mode)) {
            int rem = list.deleteAtHead(collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "List is empty" : "Deleted " + rem + " at Beginning")
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("DELETE_TAIL".equals(mode)) {
            int rem = list.deleteAtTail(collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "List is empty" : "Deleted " + rem + " at End")
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("DELETE_BEFORE".equals(mode)) {
            int rem = list.deleteBefore(targetValue, collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "Could not delete before " + targetValue : "Deleted " + rem + " before " + targetValue)
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("DELETE_AFTER".equals(mode)) {
            int rem = list.deleteAfter(targetValue, collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "Could not delete after " + targetValue : "Deleted " + rem + " after " + targetValue)
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("DELETE_VALUE".equals(mode)) {
            boolean removed = list.deleteByValue(targetValue, collector);
            if (collector == null) return "";
            return (removed ? "Deleted " + targetValue : "Value " + targetValue + " Not Found")
                    + " | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("SEARCH".equals(mode)) {
            int idx = list.search(targetValue, collector);
            if (collector == null) return "";
            return (idx >= 0 ? "Found " + targetValue + " at Index " + idx : "Target " + targetValue + " Not Found")
                    + " (size=" + list.size() + ") | " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else if ("TRAVERSE".equals(mode)) {
            list.traverse(collector);
            if (collector == null) return "";
            return "Forward Display (size=" + list.size() + "): " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        } else {
            list.clear();
            for (int i = 0; i < inputValues.length; i++) {
                list.insertAtTail(inputValues[i], collector);
            }
            list.traverse(collector);
            if (collector == null) return "";
            return "Singly Linked List (size=" + list.size() + "): " + ArrayUtils.arrayToLinkedChain(list.toArrayRaw());
        }
    }
}
