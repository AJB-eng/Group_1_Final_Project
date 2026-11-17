package model;

/**
 * Enumeration of all algorithms and custom data structures across Modules A, B, C, and D,
 * including theoretical Big-O time, space, and stability metadata.
 */
public enum AlgorithmType {

    // ================= MODULE A: RECURSION LAB =================
    FIBONACCI_THREE_WAYS(
            "A(a) Fibonacci (Naive vs Memoized vs DP)",
            AlgorithmCategory.MODULE_A_RECURSION,
            VisualizationType.RECURSION_TABLE_VIEW,
            new ComplexityInfo(
                    "O(n) [Memoized / DP]",
                    "O(n) [Memo/DP] | O(2^n) [Naive]",
                    "O(2^n) [Naive Recursion]",
                    "O(n) [Call Stack / DP Table]",
                    "N/A",
                    "Naive recursion recomputes overlapping subproblems exponentially O(2^n) and is capped at n <= 30. Memoization and Bottom-Up DP compute each state once in O(n)."
            )
    ),
    FACTORIAL_TWO_WAYS(
            "A(b) Factorial n! (Recursive vs Iterative)",
            AlgorithmCategory.MODULE_A_RECURSION,
            VisualizationType.RECURSION_TABLE_VIEW,
            new ComplexityInfo(
                    "O(1) [n = 0 or n = 1]",
                    "O(n) [n Calls / Iterations]",
                    "O(n)",
                    "O(n) [Recursive] | O(1) [Iterative]",
                    "N/A",
                    "Computes n! = n * (n-1)! with base case 0! = 1. Rejects negative n. Recursive uses O(n) stack frames; Iterative uses O(1) space."
            )
    ),
    TOWER_OF_HANOI(
            "A(c) Tower of Hanoi (Animated 3-Peg Solver)",
            AlgorithmCategory.MODULE_A_RECURSION,
            VisualizationType.HANOI_TOWERS,
            new ComplexityInfo(
                    "O(2^n)",
                    "O(2^n)",
                    "O(2^n) [Exactly 2^n - 1 Moves]",
                    "O(n) [Recursion Stack Depth]",
                    "N/A",
                    "Satisfies recurrence T(n) = 2T(n - 1) + 1. Transfers n disks from chosen Source peg to Destination peg via Auxiliary peg in exactly 2^n - 1 moves."
            )
    ),
    RECURSIVE_BINARY_SEARCH(
            "A(d) Recursive Binary Search",
            AlgorithmCategory.MODULE_A_RECURSION,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(1) [Target at First Midpoint]",
                    "O(log n)",
                    "O(log n) [Target at Leaf / Missing]",
                    "O(log n) [Recursive Call Stack]",
                    "N/A",
                    "Halves the sorted search interval [low..high] on each recursive call by comparing target against arr[mid]."
            )
    ),

    // ================= MODULE B: DATA STRUCTURES LAB =================
    CUSTOM_STACK(
            "B(a) Stack (Fixed Capacity Array)",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.STACK_VIEW,
            new ComplexityInfo(
                    "O(1) [push / pop / peek / is_empty / is_full]",
                    "O(1) [Stack Operations]",
                    "O(n) [Display All Elements]",
                    "O(capacity) [Fixed Array]",
                    "N/A",
                    "Array-based LIFO stack with fixed user-defined capacity. Reports clear Overflow when top == capacity - 1 and Underflow when top == -1."
            )
    ),
    STACK_BRACKET_CHECKER(
            "B(a-App) Stack Bracket Checker { [ ( ) ] }",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.BRACKET_CHECKER_VIEW,
            new ComplexityInfo(
                    "O(1) [First Symbol Mismatch]",
                    "O(n) [Single Pass over Symbols]",
                    "O(n) [All Symbols Scanned]",
                    "O(n) [CustomStack of Opening Brackets]",
                    "N/A",
                    "Pushes opening brackets (, [, { onto our CustomStack and pops/matches on closing brackets ), ], }. Balanced iff stack is empty at end."
            )
    ),
    LINEAR_QUEUE(
            "B(b-i) Linear Queue (Demonstrates 'False Full')",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.LINEAR_QUEUE_VIEW,
            new ComplexityInfo(
                    "O(1) [enqueue / dequeue / front]",
                    "O(1) [Operations]",
                    "O(n) [Display All]",
                    "O(capacity) [Fixed Array]",
                    "N/A",
                    "Linear queue advances rear++ without wrap-around. When rear reaches capacity - 1 after dequeues (front > 0), it suffers 'False Full' even with empty slots at 0..front-1!"
            )
    ),
    CUSTOM_QUEUE(
            "B(b-ii) Circular Queue (Solves 'False Full')",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.QUEUE_VIEW,
            new ComplexityInfo(
                    "O(1) [enqueue / dequeue / front]",
                    "O(1) [Modular Wrap-Around]",
                    "O(n) [Display All]",
                    "O(capacity) [Fixed Circular Buffer]",
                    "N/A",
                    "Uses modular arithmetic rear = (rear + 1) % capacity and front = (front + 1) % capacity so vacated front slots are reused, solving the False Full problem."
            )
    ),
    SINGLY_LINKED_LIST(
            "B(c-i) Singly Linked List (All 10 Ops)",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.LINKED_LIST_NODES,
            new ComplexityInfo(
                    "O(1) [Insert/Delete Beginning]",
                    "O(n) [Before/After/End/Search]",
                    "O(n) [Traversal]",
                    "O(n) [n Node Objects]",
                    "N/A",
                    "Pointer-based singly linked nodes supporting insert/delete at beginning, end, before element, after element, delete value, search, size, and forward display."
            )
    ),
    DOUBLY_LINKED_LIST(
            "B(c-ii) Doubly Linked List (Forward & Backward)",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.DOUBLY_LINKED_LIST_NODES,
            new ComplexityInfo(
                    "O(1) [Insert/Delete Beginning or End]",
                    "O(n) [Insert/Delete Before or After]",
                    "O(n) [Search / Forward & Backward Display]",
                    "O(n) [Nodes with next & prev Pointers]",
                    "N/A",
                    "Bidirectional DoublyNode chain with explicit next and prev pointers plus head and tail references, enabling O(1) end ops and backward traversal."
            )
    ),
    BINARY_SEARCH_TREE(
            "B(Bonus) Binary Search Tree (Recursive)",
            AlgorithmCategory.MODULE_B_STRUCTURES,
            VisualizationType.BST_TREE,
            new ComplexityInfo(
                    "O(log n) [Balanced Tree]",
                    "O(log n) [Insert / Delete / Search]",
                    "O(n) [Skewed Tree / Traversals]",
                    "O(n) Nodes, O(h) Call Stack",
                    "N/A",
                    "Hierarchical TreeNode structure with recursive insert, delete, search, and Inorder, Preorder, and Postorder traversals."
            )
    ),

    // ================= MODULE C: SORTING LAB =================
    BUBBLE_SORT(
            "C(1) Bubble Sort",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n) [Already Sorted w/ Early Exit]",
                    "O(n^2)",
                    "O(n^2) [Reverse Sorted]",
                    "O(1) [In-Place]",
                    "Stable (Yes)",
                    "Repeatedly compares adjacent elements (arr[j] vs arr[j+1]) and swaps only when strictly greater (arr[j] > arr[j+1]), preserving relative order of equal keys."
            )
    ),
    SELECTION_SORT(
            "C(2) Selection Sort",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n^2)",
                    "O(n^2)",
                    "O(n^2)",
                    "O(1) [In-Place]",
                    "Unstable (No)",
                    "Scans unsorted suffix [i..n-1] each pass to find minIndex and swaps arr[i] <-> arr[minIndex]. Long-range swaps can jump an element past an equal key (unstable)."
            )
    ),
    INSERTION_SORT(
            "C(3) Insertion Sort",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n) [Already Sorted - 1 Compare/Pass]",
                    "O(n^2)",
                    "O(n^2) [Reverse Sorted]",
                    "O(1) [In-Place]",
                    "Stable (Yes)",
                    "Inserts key = arr[i] into sorted prefix [0..i-1] by shifting elements strictly greater than key one slot right. Fast O(n) on already-sorted input!"
            )
    ),
    MERGE_SORT(
            "C(4) Merge Sort",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n log n)",
                    "O(n log n)",
                    "O(n log n)",
                    "O(n) [Auxiliary Merge Arrays]",
                    "Stable (Yes)",
                    "Recursively splits array at midpoint and merges sorted halves using leftArr[i] <= rightArr[j] (tie-breaking left first guarantees stability)."
            )
    ),
    QUICK_SORT(
            "C(5a) Quick Sort (First-Element Pivot, Yellow/Green)",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n log n) [Balanced Partitions]",
                    "O(n log n)",
                    "O(n^2) [Already Sorted / Reverse Sorted]",
                    "O(log n) [Recursion Stack]",
                    "Unstable (No)",
                    "Uses first element arr[low] as pivot with Yellow (<= pivot) and Green (> pivot) regions taught in class. Degrades to O(n^2) on sorted input."
            )
    ),
    QUICK_SORT_RANDOMIZED(
            "C(5b) Quick Sort (Randomised Pivot, Yellow/Green)",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.ARRAY_BARS,
            new ComplexityInfo(
                    "O(n log n)",
                    "O(n log n) [Expected on ALL Inputs]",
                    "O(n^2) [Extremely Rare]",
                    "O(log n) [Recursion Stack]",
                    "Unstable (No)",
                    "Swaps a randomly chosen element in [low..high] into arr[low] before running the Yellow/Green partition, eliminating O(n^2) degradation on sorted inputs!"
            )
    ),
    COMPARE_ALL_SORTS(
            "C(Compare All) Side-by-Side Comparison of All Sorts",
            AlgorithmCategory.MODULE_C_SORTING,
            VisualizationType.SORT_COMPARE_TABLE_VIEW,
            new ComplexityInfo(
                    "O(n) to O(n log n)",
                    "O(n log n) vs O(n^2)",
                    "O(n log n) [Merge] vs O(n^2)",
                    "O(1) to O(n)",
                    "Mixed (See Table)",
                    "Runs Bubble, Selection, Insertion, Merge, First-Pivot QuickSort, and Random-Pivot QuickSort on the exact same input and compares Comparisons, Swaps/Moves, Time, and Stability."
            )
    ),

    // ================= MODULE D: TIME COMPLEXITY ANALYSIS =================
    MODULE_D_BENCHMARK(
            "D(Benchmark) Experimental Time Complexity & Plots",
            AlgorithmCategory.MODULE_D_COMPLEXITY,
            VisualizationType.COMPLEXITY_BENCHMARK_VIEW,
            new ComplexityInfo(
                    "O(n) [Insertion on Sorted]",
                    "O(n^2) vs O(n log n)",
                    "O(n^2) [Quadratic Sorts & 1st-Pivot QS on Sorted]",
                    "O(1) to O(n)",
                    "Empirical Benchmark",
                    "Times all sorting algorithms across n = 100, 500, 1000, 2000, 5000, 10000 (and 50000, 100000 for O(n log n) sorts) on Random, Sorted, and Reverse-Sorted inputs (average of 3 runs) and plots O(n^2) vs O(n log n) growth curves."
            )
    );

    private final String displayName;
    private final AlgorithmCategory category;
    private final VisualizationType visualizationType;
    private final ComplexityInfo complexityInfo;

    AlgorithmType(String displayName,
                  AlgorithmCategory category,
                  VisualizationType visualizationType,
                  ComplexityInfo complexityInfo) {
        this.displayName = displayName;
        this.category = category;
        this.visualizationType = visualizationType;
        this.complexityInfo = complexityInfo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AlgorithmCategory getCategory() {
        return category;
    }

    public VisualizationType getVisualizationType() {
        return visualizationType;
    }

    public ComplexityInfo getComplexityInfo() {
        return complexityInfo;
    }

    public static AlgorithmType[] getByCategory(AlgorithmCategory category) {
        AlgorithmType[] all = values();
        int count = 0;
        for (int i = 0; i < all.length; i++) {
            if (all[i].getCategory() == category) {
                count++;
            }
        }
        AlgorithmType[] filtered = new AlgorithmType[count];
        int idx = 0;
        for (int i = 0; i < all.length; i++) {
            if (all[i].getCategory() == category) {
                filtered[idx++] = all[i];
            }
        }
        return filtered;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
