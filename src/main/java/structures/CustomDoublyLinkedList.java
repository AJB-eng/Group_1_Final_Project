package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module B(c)(ii): Custom Doubly Linked List implemented from first principles using DoublyNode
 * with explicit next and prev pointers plus head and tail references.
 * Supports all required CSC2107 operations:
 * - Insertion: at beginning, at end, before a given element, after a given element
 * - Deletion: at beginning, at end, before a given element, after a given element, of a given element
 * - Search, size, forward display (HEAD -> TAIL), AND backward display (TAIL -> HEAD via prev pointers).
 */
public class CustomDoublyLinkedList {
    private DoublyNode head;
    private DoublyNode tail;
    private int size;
    private long operationsCount;

    public CustomDoublyLinkedList() {
        this.head = null;
        this.tail = null;
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
        tail = null;
        size = 0;
        operationsCount = 0;
    }

    /**
     * Inserts a new DoublyNode at the beginning (head). Time Complexity: O(1).
     */
    public void insertAtHead(int value, StepCollector collector) {
        DoublyNode newNode = new DoublyNode(value);
        operationsCount++;

        if (head == null) {
            head = newNode;
            tail = newNode;
            operationsCount += 2;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            operationsCount += 3;
        }
        size++;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayForwardRaw(), new int[0], new int[0], new int[]{0},
                    0, "HEAD", size - 1, "TAIL", 0, "INS HEAD " + value, size, operationsCount,
                    "Inserted " + value + " at beginning (HEAD). Updated head.prev and newNode.next."
            );
        }
    }

    /**
     * Inserts a new DoublyNode at the end (tail) using the tail pointer. Time Complexity: O(1).
     */
    public void insertAtTail(int value, StepCollector collector) {
        DoublyNode newNode = new DoublyNode(value);
        operationsCount++;

        if (tail == null) {
            head = newNode;
            tail = newNode;
            operationsCount += 2;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            operationsCount += 3;
        }
        size++;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayForwardRaw(), new int[0], new int[0], new int[]{size - 1},
                    0, "HEAD", size - 1, "TAIL", size - 1, "INS TAIL " + value, size, operationsCount,
                    "Inserted " + value + " at end (TAIL). Linked tail.next <-> newNode.prev."
            );
        }
    }

    /**
     * Inserts newValue immediately BEFORE the first occurrence of targetElement. Time Complexity: O(n).
     */
    public boolean insertBefore(int targetElement, int newValue, StepCollector collector) {
        DoublyNode curr = head;
        int idx = 0;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                if (curr == head) {
                    insertAtHead(newValue, collector);
                    return true;
                }
                DoublyNode newNode = new DoublyNode(newValue);
                DoublyNode prevNode = curr.prev;
                newNode.prev = prevNode;
                newNode.next = curr;
                prevNode.next = newNode;
                curr.prev = newNode;
                size++;
                operationsCount += 5;

                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayForwardRaw(), new int[]{idx + 1}, new int[0], new int[]{idx},
                            0, "HEAD", size - 1, "TAIL", idx, "BEFORE " + targetElement, size, operationsCount,
                            "Inserted " + newValue + " before " + targetElement + " (updated 4 next/prev pointers)."
                    );
                }
                return true;
            }
            curr = curr.next;
            idx++;
        }
        recordMessageStep(collector, "Target " + targetElement + " not found; cannot insert " + newValue + " before it.");
        return false;
    }

    /**
     * Inserts newValue immediately AFTER the first occurrence of targetElement. Time Complexity: O(n).
     */
    public boolean insertAfter(int targetElement, int newValue, StepCollector collector) {
        DoublyNode curr = head;
        int idx = 0;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                if (curr == tail) {
                    insertAtTail(newValue, collector);
                    return true;
                }
                DoublyNode newNode = new DoublyNode(newValue);
                DoublyNode nextNode = curr.next;
                newNode.prev = curr;
                newNode.next = nextNode;
                curr.next = newNode;
                nextNode.prev = newNode;
                size++;
                operationsCount += 5;

                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayForwardRaw(), new int[]{idx}, new int[0], new int[]{idx + 1},
                            0, "HEAD", size - 1, "TAIL", idx + 1, "AFTER " + targetElement, size, operationsCount,
                            "Inserted " + newValue + " after " + targetElement + " (updated 4 next/prev pointers)."
                    );
                }
                return true;
            }
            curr = curr.next;
            idx++;
        }
        recordMessageStep(collector, "Target " + targetElement + " not found; cannot insert " + newValue + " after it.");
        return false;
    }

    /**
     * Deletes the node at the beginning (head). Time Complexity: O(1).
     */
    public int deleteAtHead(StepCollector collector) {
        operationsCount++;
        if (head == null) {
            recordMessageStep(collector, "Cannot delete at beginning: Doubly Linked List is empty.");
            return Integer.MIN_VALUE;
        }
        int removed = head.data;
        if (head == tail) {
            head = null;
            tail = null;
            operationsCount += 2;
        } else {
            head = head.next;
            head.prev = null;
            operationsCount += 2;
        }
        size--;
        recordMessageStep(collector, "Deleted " + removed + " from beginning (HEAD). Size = " + size);
        return removed;
    }

    /**
     * Deletes the node at the end (tail) in O(1) using tail.prev. Time Complexity: O(1).
     */
    public int deleteAtTail(StepCollector collector) {
        operationsCount++;
        if (tail == null) {
            recordMessageStep(collector, "Cannot delete at end: Doubly Linked List is empty.");
            return Integer.MIN_VALUE;
        }
        int removed = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
            operationsCount += 2;
        } else {
            tail = tail.prev;
            tail.next = null;
            operationsCount += 2;
        }
        size--;
        recordMessageStep(collector, "Deleted " + removed + " from end (TAIL via tail.prev). Size = " + size);
        return removed;
    }

    /**
     * Deletes the node immediately BEFORE targetElement using curr.prev. Time Complexity: O(n).
     */
    public int deleteBefore(int targetElement, StepCollector collector) {
        DoublyNode curr = head;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                if (curr.prev == null) {
                    recordMessageStep(collector, "Target " + targetElement + " is at HEAD; no node exists before it.");
                    return Integer.MIN_VALUE;
                }
                DoublyNode toDelete = curr.prev;
                int removed = toDelete.data;
                if (toDelete == head) {
                    return deleteAtHead(collector);
                }
                toDelete.prev.next = curr;
                curr.prev = toDelete.prev;
                size--;
                operationsCount += 3;
                recordMessageStep(collector, "Deleted " + removed + " (before " + targetElement + "). Size = " + size);
                return removed;
            }
            curr = curr.next;
        }
        recordMessageStep(collector, "Target " + targetElement + " not found.");
        return Integer.MIN_VALUE;
    }

    /**
     * Deletes the node immediately AFTER targetElement using curr.next. Time Complexity: O(n).
     */
    public int deleteAfter(int targetElement, StepCollector collector) {
        DoublyNode curr = head;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == targetElement) {
                if (curr.next == null) {
                    recordMessageStep(collector, "Target " + targetElement + " is at TAIL; no node exists after it.");
                    return Integer.MIN_VALUE;
                }
                DoublyNode toDelete = curr.next;
                if (toDelete == tail) {
                    return deleteAtTail(collector);
                }
                int removed = toDelete.data;
                curr.next = toDelete.next;
                toDelete.next.prev = curr;
                size--;
                operationsCount += 3;
                recordMessageStep(collector, "Deleted " + removed + " (after " + targetElement + "). Size = " + size);
                return removed;
            }
            curr = curr.next;
        }
        recordMessageStep(collector, "Target " + targetElement + " not found.");
        return Integer.MIN_VALUE;
    }

    /**
     * Deletes the first occurrence of value. Time Complexity: O(n).
     */
    public boolean deleteByValue(int value, StepCollector collector) {
        DoublyNode curr = head;
        while (curr != null) {
            operationsCount += 2;
            if (curr.data == value) {
                if (curr == head) {
                    deleteAtHead(collector);
                } else if (curr == tail) {
                    deleteAtTail(collector);
                } else {
                    curr.prev.next = curr.next;
                    curr.next.prev = curr.prev;
                    size--;
                    operationsCount += 3;
                    recordMessageStep(collector, "Deleted node [" + value + "] by relinking prev.next and next.prev.");
                }
                return true;
            }
            curr = curr.next;
        }
        recordMessageStep(collector, "Value " + value + " not found in Doubly Linked List.");
        return false;
    }

    /**
     * Searches for target forward from HEAD. Time Complexity: O(n).
     */
    public int search(int target, StepCollector collector) {
        DoublyNode curr = head;
        int idx = 0;
        while (curr != null) {
            operationsCount += 2;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayForwardRaw(), new int[]{idx}, new int[0], new int[0],
                        0, "HEAD", size - 1, "TAIL", idx, "SCAN", size, operationsCount,
                        "Searching Doubly Linked List: checking [" + curr.data + "] at index " + idx
                );
            }
            if (curr.data == target) {
                if (collector != null) {
                    collector.recordArrayStep(
                            toArrayForwardRaw(), new int[0], new int[0], new int[]{idx},
                            0, "HEAD", size - 1, "TAIL", idx, "FOUND " + target, size, operationsCount,
                            "Found " + target + " at index " + idx + "!"
                    );
                }
                return idx;
            }
            curr = curr.next;
            idx++;
        }
        recordMessageStep(collector, "Target " + target + " not found.");
        return -1;
    }

    /**
     * Traverses forward from HEAD to TAIL via next pointers. Time Complexity: O(n).
     */
    public int[] displayForward(StepCollector collector) {
        int[] res = new int[size];
        DoublyNode curr = head;
        int idx = 0;
        while (curr != null) {
            operationsCount++;
            res[idx] = curr.data;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayForwardRaw(), new int[]{idx}, new int[0], ArrayUtils.rangeIndices(0, idx),
                        0, "HEAD", size - 1, "TAIL", idx, "FWD ->", size, operationsCount,
                        "Forward traversal (next): visited [" + curr.data + "] at index " + idx
                );
            }
            curr = curr.next;
            idx++;
        }
        return res;
    }

    /**
     * Traverses backward from TAIL to HEAD via prev pointers. Time Complexity: O(n).
     */
    public int[] displayBackward(StepCollector collector) {
        int[] res = new int[size];
        DoublyNode curr = tail;
        int forwardIdx = size - 1;
        int outIdx = 0;
        while (curr != null) {
            operationsCount++;
            res[outIdx++] = curr.data;
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayForwardRaw(),
                        new int[]{forwardIdx},
                        new int[0],
                        ArrayUtils.rangeIndices(forwardIdx, size - 1),
                        0, "HEAD", size - 1, "TAIL", forwardIdx, "<- BWD", size, operationsCount,
                        "Backward traversal (prev): visited [" + curr.data + "] at index " + forwardIdx
                );
            }
            curr = curr.prev;
            forwardIdx--;
        }
        return res;
    }

    private void recordMessageStep(StepCollector collector, String message) {
        if (collector != null) {
            int[] snap = toArrayForwardRaw();
            collector.recordArrayStep(
                    snap, new int[0], new int[0], new int[0],
                    size > 0 ? 0 : -1, "HEAD",
                    size > 0 ? size - 1 : -1, "TAIL",
                    -1, "", size, operationsCount, message
            );
        }
    }

    public int[] toArrayForwardRaw() {
        int[] arr = new int[size];
        DoublyNode curr = head;
        int i = 0;
        while (curr != null && i < size) {
            arr[i++] = curr.data;
            curr = curr.next;
        }
        return arr;
    }

    public int[] toArrayBackwardRaw() {
        int[] arr = new int[size];
        DoublyNode curr = tail;
        int i = 0;
        while (curr != null && i < size) {
            arr[i++] = curr.data;
            curr = curr.prev;
        }
        return arr;
    }

    public static String formatDoublyChain(int[] arr) {
        if (arr == null || arr.length == 0) {
            return "NULL <- HEAD/TAIL -> NULL (Empty)";
        }
        StringBuilder sb = new StringBuilder("NULL <- ");
        for (int i = 0; i < arr.length; i++) {
            sb.append("[").append(arr[i]).append("]");
            if (i < arr.length - 1) {
                sb.append(" <-> ");
            }
        }
        sb.append(" -> NULL");
        return sb.toString();
    }

    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, int secondValue, String mode) {
        StepCollector collector = new StepCollector();

        new DoublyNode(0);
        CustomDoublyLinkedList timingList = new CustomDoublyLinkedList();
        int[] existing = toArrayForwardRaw();
        for (int i = 0; i < existing.length; i++) {
            timingList.insertAtTail(existing[i], null);
        }
        timingList.resetCounter();

        long t0 = System.nanoTime();
        runModeInternal(timingList, inputValues, targetValue, secondValue, mode, null);
        long elapsedNanos = System.nanoTime() - t0;

        resetCounter();
        String summary = runModeInternal(this, inputValues, targetValue, secondValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.DOUBLY_LINKED_LIST,
                summary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private String runModeInternal(
            CustomDoublyLinkedList list,
            int[] inputValues,
            int targetValue,
            int secondValue,
            String mode,
            StepCollector collector) {
        if ("INSERT_HEAD".equals(mode)) {
            list.insertAtHead(targetValue, collector);
            if (collector == null) return "";
            return "Inserted " + targetValue + " at Beginning | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("INSERT_TAIL".equals(mode)) {
            list.insertAtTail(targetValue, collector);
            if (collector == null) return "";
            return "Inserted " + targetValue + " at End | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("INSERT_BEFORE".equals(mode)) {
            boolean ok = list.insertBefore(secondValue, targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Inserted " + targetValue + " before " + secondValue : "Element " + secondValue + " not found")
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("INSERT_AFTER".equals(mode)) {
            boolean ok = list.insertAfter(secondValue, targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Inserted " + targetValue + " after " + secondValue : "Element " + secondValue + " not found")
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DELETE_HEAD".equals(mode)) {
            int rem = list.deleteAtHead(collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "List is empty" : "Deleted " + rem + " at Beginning")
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DELETE_TAIL".equals(mode)) {
            int rem = list.deleteAtTail(collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "List is empty" : "Deleted " + rem + " at End")
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DELETE_BEFORE".equals(mode)) {
            int rem = list.deleteBefore(targetValue, collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "Cannot delete before " + targetValue : "Deleted " + rem + " before " + targetValue)
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DELETE_AFTER".equals(mode)) {
            int rem = list.deleteAfter(targetValue, collector);
            if (collector == null) return "";
            return (rem == Integer.MIN_VALUE ? "Cannot delete after " + targetValue : "Deleted " + rem + " after " + targetValue)
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DELETE_VALUE".equals(mode)) {
            boolean ok = list.deleteByValue(targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Deleted " + targetValue : "Value " + targetValue + " not found")
                    + " | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("SEARCH".equals(mode)) {
            int idx = list.search(targetValue, collector);
            if (collector == null) return "";
            return (idx >= 0 ? "Found " + targetValue + " at index " + idx : "Value " + targetValue + " not found")
                    + " (size=" + list.size() + ") | " + formatDoublyChain(list.toArrayForwardRaw());
        } else if ("DISPLAY_FORWARD".equals(mode)) {
            int[] fwd = list.displayForward(collector);
            if (collector == null) return "";
            return "Forward (HEAD->TAIL, size=" + list.size() + "): " + ArrayUtils.arrayToString(fwd);
        } else if ("DISPLAY_BACKWARD".equals(mode)) {
            int[] bwd = list.displayBackward(collector);
            if (collector == null) return "";
            return "Backward (TAIL->HEAD via prev, size=" + list.size() + "): " + ArrayUtils.arrayToString(bwd);
        } else {
            list.clear();
            for (int i = 0; i < inputValues.length; i++) {
                list.insertAtTail(inputValues[i], collector);
            }
            int[] fwd = list.displayForward(collector);
            int[] bwd = list.displayBackward(collector);
            if (collector == null) return "";
            return "Forward: " + ArrayUtils.arrayToString(fwd)
                    + " | Backward: " + ArrayUtils.arrayToString(bwd)
                    + " (size=" + list.size() + ")";
        }
    }
}
