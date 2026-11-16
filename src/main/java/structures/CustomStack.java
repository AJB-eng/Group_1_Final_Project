package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;

/**
 * Module B(a): Custom Array-Based Stack with fixed capacity set by the user,
 * plus the Stack Application for checking whether a bracket expression such as { ( [ ] ) } is balanced.
 * Built 100% from first principles using a raw int[] array and manual top pointer.
 */
public class CustomStack {
    private int[] elements;
    private int top;
    private int capacity;
    private long operationsCount;

    public CustomStack() {
        this(6);
    }

    public CustomStack(int initialCapacity) {
        this.capacity = initialCapacity > 0 ? initialCapacity : 6;
        this.elements = new int[this.capacity];
        this.top = -1;
        this.operationsCount = 0;
    }

    public void setFixedCapacity(int newCapacity) {
        int cap = newCapacity > 0 ? newCapacity : 6;
        int[] next = new int[cap];
        int copyCount = Math.min(size(), cap);
        for (int i = 0; i < copyCount; i++) {
            next[i] = elements[i];
        }
        this.elements = next;
        this.capacity = cap;
        this.top = copyCount - 1;
    }

    public void resetCounter() {
        this.operationsCount = 0;
    }

    public long getOperationsCount() {
        return operationsCount;
    }

    /**
     * Returns the current number of elements in the stack. Time Complexity: O(1).
     */
    public int size() {
        return top + 1;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTopIndex() {
        return top;
    }

    /**
     * Checks whether the stack is empty (top == -1). Time Complexity: O(1).
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Checks whether the stack has reached its fixed capacity (top == capacity - 1). Time Complexity: O(1).
     */
    public boolean isFull() {
        return top + 1 >= capacity;
    }

    public void clear() {
        this.top = -1;
        this.operationsCount = 0;
    }

    /**
     * Pushes a value onto the top of the fixed-capacity stack.
     * Handles Stack Overflow with a clear message when isFull() is true.
     * Time Complexity: O(1).
     */
    public boolean push(int value, StepCollector collector) {
        operationsCount++;
        if (isFull()) {
            if (collector != null) {
                collector.recordArrayStep(
                        toArrayRaw(),
                        new int[0],
                        new int[]{top},
                        new int[0],
                        top, "TOP (FULL)",
                        -1, "",
                        -1, "OVERFLOW",
                        capacity,
                        operationsCount,
                        "Stack Overflow! Stack is full (size = " + size() + ", capacity = " + capacity
                                + "). Cannot push " + value + "."
                );
            }
            return false;
        }

        top++;
        elements[top] = value;
        operationsCount += 2;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(),
                    new int[0],
                    new int[0],
                    new int[]{top},
                    top, "TOP",
                    -1, "",
                    top, "PUSHED " + value,
                    capacity,
                    operationsCount,
                    "Pushed " + value + " onto stack at index " + top + " (size=" + size() + "/" + capacity + ")."
            );
        }
        return true;
    }

    /**
     * Pops and returns the top element of the stack.
     * Handles Stack Underflow with a clear message when isEmpty() is true.
     * Time Complexity: O(1).
     */
    public int pop(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        new int[0],
                        new int[0],
                        new int[0],
                        new int[0],
                        -1, "TOP = -1 (EMPTY)",
                        -1, "",
                        -1, "UNDERFLOW",
                        capacity,
                        operationsCount,
                        "Stack Underflow! Stack is empty (top == -1). Cannot pop."
                );
            }
            return Integer.MIN_VALUE;
        }

        int poppedValue = elements[top];
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(),
                    new int[0],
                    new int[]{top},
                    new int[0],
                    top, "TOP",
                    -1, "",
                    top, "POP " + poppedValue,
                    capacity,
                    operationsCount,
                    "Popping " + poppedValue + " from top index " + top + "."
            );
        }

        top--;
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(),
                    new int[0],
                    new int[0],
                    top >= 0 ? new int[]{top} : new int[0],
                    top, "TOP",
                    -1, "",
                    -1, "",
                    capacity,
                    operationsCount,
                    "Popped " + poppedValue + ". New top index = " + top + " (size=" + size() + "/" + capacity + ")."
            );
        }
        return poppedValue;
    }

    /**
     * Returns the top element without removing it. Time Complexity: O(1).
     */
    public int peek(StepCollector collector) {
        operationsCount++;
        if (isEmpty()) {
            if (collector != null) {
                collector.recordArrayStep(
                        new int[0], new int[0], new int[0], new int[0],
                        -1, "TOP = -1 (EMPTY)", -1, "", -1, "",
                        capacity, operationsCount,
                        "Peek failed: Stack is empty (top == -1)."
                );
            }
            return Integer.MIN_VALUE;
        }

        int topValue = elements[top];
        operationsCount++;

        if (collector != null) {
            collector.recordArrayStep(
                    toArrayRaw(),
                    new int[]{top},
                    new int[0],
                    new int[]{top},
                    top, "TOP",
                    -1, "",
                    top, "PEEK=" + topValue,
                    capacity,
                    operationsCount,
                    "Peeked top element: " + topValue + " at index " + top + "."
            );
        }
        return topValue;
    }

    public int[] toArrayRaw() {
        if (top < 0) {
            return new int[0];
        }
        int[] copy = new int[top + 1];
        for (int i = 0; i <= top; i++) {
            copy[i] = elements[i];
        }
        return copy;
    }

    /**
     * Executes a Stack operation or full demo and returns an ExecutionResult.
     */
    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, String mode) {
        StepCollector collector = new StepCollector();

        CustomStack timingStack = new CustomStack(this.capacity);
        int[] currentSnap = toArrayRaw();
        for (int i = 0; i < currentSnap.length; i++) {
            timingStack.push(currentSnap[i], null);
        }
        timingStack.resetCounter();

        long startTime = System.nanoTime();
        runModeInternal(timingStack, inputValues, targetValue, mode, null);
        long elapsedNanos = System.nanoTime() - startTime;

        resetCounter();
        String summary = runModeInternal(this, inputValues, targetValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.CUSTOM_STACK,
                summary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private String runModeInternal(
            CustomStack stack,
            int[] inputValues,
            int targetValue,
            String mode,
            StepCollector collector) {
        if ("SET_CAPACITY".equals(mode)) {
            int newCap = targetValue > 0 ? targetValue : 6;
            stack.setFixedCapacity(newCap);
            stack.operationsCount++;
            if (collector == null) return "";
            collector.recordArrayStep(
                    stack.toArrayRaw(), new int[0], new int[0], new int[0],
                    stack.getTopIndex(), "TOP", -1, "", -1, "", stack.getCapacity(),
                    stack.operationsCount,
                    "Set fixed stack capacity to " + newCap + " slots (current size = " + stack.size() + ")."
            );
            return "Fixed Stack Capacity set to " + newCap + " | Stack: " + ArrayUtils.arrayToString(stack.toArrayRaw());
        } else if ("PUSH".equals(mode)) {
            boolean ok = stack.push(targetValue, collector);
            if (collector == null) return "";
            return (ok ? "Pushed " + targetValue : "Stack Overflow! Stack is full (capacity=" + stack.getCapacity() + ")")
                    + " | Stack: " + ArrayUtils.arrayToString(stack.toArrayRaw());
        } else if ("POP".equals(mode)) {
            int popped = stack.pop(collector);
            if (collector == null) return "";
            return (popped == Integer.MIN_VALUE ? "Stack Underflow! Stack is empty" : "Popped " + popped)
                    + " | Stack: " + ArrayUtils.arrayToString(stack.toArrayRaw());
        } else if ("PEEK".equals(mode)) {
            int peeked = stack.peek(collector);
            if (collector == null) return "";
            return (peeked == Integer.MIN_VALUE ? "Stack is empty" : "Peek Top = " + peeked)
                    + " | Stack: " + ArrayUtils.arrayToString(stack.toArrayRaw());
        } else if ("STATUS".equals(mode)) {
            stack.operationsCount += 3;
            if (collector == null) return "";
            String msg = "size() = " + stack.size()
                    + ", is_empty() = " + stack.isEmpty()
                    + ", is_full() = " + stack.isFull()
                    + " (capacity = " + stack.getCapacity() + ") | Elements: " + ArrayUtils.arrayToString(stack.toArrayRaw());
            collector.recordArrayStep(
                    stack.toArrayRaw(), new int[0], new int[0], ArrayUtils.rangeIndices(0, stack.size() - 1),
                    stack.getTopIndex(), "TOP", -1, "", -1, "", stack.getCapacity(), stack.operationsCount, msg
            );
            return msg;
        } else {
            stack.clear();
            int limit = Math.min(inputValues.length, stack.getCapacity());
            for (int i = 0; i < limit; i++) {
                stack.push(inputValues[i], collector);
            }
            // If more elements than capacity, attempt one extra push to demonstrate Overflow handling!
            if (inputValues.length > stack.getCapacity()) {
                stack.push(inputValues[stack.getCapacity()], collector);
            }
            stack.peek(collector);
            if (collector == null) return "";
            return "Stack (Bottom->Top): " + ArrayUtils.arrayToString(stack.toArrayRaw())
                    + " | size=" + stack.size() + "/" + stack.getCapacity()
                    + ", is_empty=" + stack.isEmpty() + ", is_full=" + stack.isFull();
        }
    }

    /**
     * Module B Stack Application: Uses CustomStack to check whether a bracket expression
     * such as "{ ( [ ] ) }" is balanced, recording the stack after each symbol!
     * Time Complexity: O(n) for an expression of length n, Space Complexity: O(n).
     */
    public static ExecutionResult checkBalancedBrackets(String expression) {
        String expr = (expression == null || expression.trim().isEmpty()) ? "{ ( [ ] ) }" : expression.trim();
        StepCollector collector = new StepCollector();

        long t0 = System.nanoTime();
        runBracketCheck(expr, null);
        long elapsedNanos = System.nanoTime() - t0;

        BracketCheckOutcome outcome = runBracketCheck(expr, collector);

        return new ExecutionResult(
                AlgorithmType.STACK_BRACKET_CHECKER,
                outcome.summary,
                collector.toArray(),
                outcome.ops,
                outcome.ops,
                0,
                elapsedNanos
        );
    }

    private static final class BracketCheckOutcome {
        final boolean balanced;
        final long ops;
        final String summary;

        BracketCheckOutcome(boolean balanced, long ops, String summary) {
            this.balanced = balanced;
            this.ops = ops;
            this.summary = summary;
        }
    }

    private static BracketCheckOutcome runBracketCheck(String expr, StepCollector collector) {
        int n = expr.length();
        CustomStack stack = new CustomStack(Math.max(8, n));
        long ops = 0;

        if (collector != null) {
            collector.recordArrayStep(
                    new int[0], new int[0], new int[0], new int[0],
                    -1, "TOP (EMPTY)", -1, "", -1, "", stack.getCapacity(), 0,
                    "Checking bracket expression: \"" + expr + "\" using CustomStack."
            );
        }

        for (int i = 0; i < n; i++) {
            char ch = expr.charAt(i);
            ops++;

            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push((int) ch, null);
                ops += 2;
                if (collector != null) {
                    collector.recordArrayStep(
                            stack.toArrayRaw(),
                            new int[0],
                            new int[0],
                            new int[]{stack.getTopIndex()},
                            stack.getTopIndex(), "TOP='" + ch + "'",
                            i, "SYM[" + i + "]='" + ch + "'",
                            -1, "PUSH '" + ch + "'",
                            stack.getCapacity(),
                            ops,
                            "Symbol #" + (i + 1) + " is opening bracket '" + ch + "': PUSH('" + ch + "') onto stack -> "
                                    + formatCharStack(stack.toArrayRaw())
                    );
                }
            } else if (ch == ')' || ch == ']' || ch == '}') {
                ops++;
                if (stack.isEmpty()) {
                    String msg = "UNBALANCED at symbol #" + (i + 1) + " ('" + ch
                            + "'): encountered closing bracket '" + ch + "' when stack is EMPTY!";
                    if (collector != null) {
                        collector.recordArrayStep(
                                new int[0], new int[0], new int[0], new int[0],
                                -1, "EMPTY!", i, "ERR '" + ch + "'", -1, "UNBALANCED",
                                stack.getCapacity(), ops, msg
                        );
                    }
                    return new BracketCheckOutcome(false, ops, "Expression \"" + expr + "\" is UNBALANCED (" + msg + ")");
                }

                char topChar = (char) stack.pop(null);
                ops += 2;
                boolean matches = (topChar == '(' && ch == ')')
                        || (topChar == '[' && ch == ']')
                        || (topChar == '{' && ch == '}');

                if (!matches) {
                    String msg = "UNBALANCED at symbol #" + (i + 1) + " ('" + ch
                            + "'): popped '" + topChar + "' does NOT match closing '" + ch + "'!";
                    if (collector != null) {
                        collector.recordArrayStep(
                                stack.toArrayRaw(), new int[0], new int[]{Math.max(0, stack.getTopIndex())}, new int[0],
                                stack.getTopIndex(), "MISMATCH", i, "SYM='" + ch + "'", -1, "POP '" + topChar + "'",
                                stack.getCapacity(), ops, msg
                        );
                    }
                    return new BracketCheckOutcome(false, ops, "Expression \"" + expr + "\" is UNBALANCED (" + msg + ")");
                }

                if (collector != null) {
                    collector.recordArrayStep(
                            stack.toArrayRaw(),
                            new int[0],
                            new int[0],
                            stack.getTopIndex() >= 0 ? new int[]{stack.getTopIndex()} : new int[0],
                            stack.getTopIndex(), "TOP",
                            i, "MATCH '" + topChar + ch + "'",
                            -1, "",
                            stack.getCapacity(),
                            ops,
                            "Symbol #" + (i + 1) + " is closing '" + ch + "': POP() returned matching '"
                                    + topChar + "'. Stack now: " + formatCharStack(stack.toArrayRaw())
                    );
                }
            }
        }

        boolean balanced = stack.isEmpty();
        ops++;
        String finalMsg = balanced
                ? "Expression \"" + expr + "\" is BALANCED! All brackets matched and stack is empty."
                : "Expression \"" + expr + "\" is UNBALANCED! Unclosed opening brackets remain on stack: "
                + formatCharStack(stack.toArrayRaw());

        if (collector != null) {
            collector.recordArrayStep(
                    stack.toArrayRaw(),
                    new int[0],
                    balanced ? new int[0] : ArrayUtils.rangeIndices(0, stack.size() - 1),
                    new int[0],
                    stack.getTopIndex(), balanced ? "EMPTY (BALANCED)" : "UNCLOSED!",
                    -1, "", -1, "",
                    stack.getCapacity(),
                    ops,
                    finalMsg
            );
        }
        return new BracketCheckOutcome(balanced, ops, finalMsg);
    }

    public static String formatCharStack(int[] asciiCodes) {
        if (asciiCodes == null || asciiCodes.length == 0) {
            return "[Empty]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < asciiCodes.length; i++) {
            sb.append("'").append((char) asciiCodes[i]).append("'");
            if (i < asciiCodes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
