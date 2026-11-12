# Algorithm Explorer (CSC2107 Group Project)

## What this project is

This is our CSC2107 Design and Analysis of Algorithms coursework project. It is a desktop app written in Java with JavaFX. You type in some numbers (or load them from a file, or let the app generate random ones), pick a module and task, and the app runs it and shows you what happened step by step. Everything is built from first principles. We did not use ArrayList, LinkedList, Stack, Queue, PriorityQueue, Collections or Arrays.sort anywhere. All the lists, stacks, queues and trees are made from plain arrays and our own node classes, and every sort and recursion algorithm is written by hand.

## The four modules

The dropdown at the top of the window is organised into the four modules from the coursework brief.

Module A is the Recursion Lab. It computes the nth Fibonacci number in three ways (naive recursion with O(2^n) time capped at n up to 30 so it does not freeze, memoized recursion with O(n) time, and bottom-up dynamic programming with O(n) time) and compares their results, call or iteration counts, and timings side by side. It also computes n factorial both recursively and iteratively while handling 0! equals 1 and rejecting negative numbers with a clear warning. For the Tower of Hanoi, it supports 1 to 8 disks, lets you pick the source, destination and auxiliary pegs, lists every single move, animates the disks across the three pegs, and shows a running counter confirming the total moves equal 2^n minus 1. It also includes recursive binary search.

Module B is the Data Structures Lab. It has an array-based Stack where you can set the fixed capacity yourself, run push, pop, peek, size, is_empty, is_full and display, and see clear overflow and underflow messages. It also has the Balanced Bracket Checker stack application for expressions like { ( [ ] ) } that displays the stack state after each symbol is processed. For queues, it implements both a Linear Queue on a fixed-size array (which demonstrates the False Full problem when rear reaches the end while empty slots remain at the front) and a Circular Queue on a fixed-size array (which solves False Full by wrapping front and rear around with modulo arithmetic). For linked lists, it implements both a Singly Linked List and a Doubly Linked List with insertion at the beginning, at the end, before a given element, and after a given element, plus deletion from the beginning, from the end, before a given element, after a given element, and of a given element, along with search, size, forward display for both lists, and backward display from tail to head for the doubly linked list. A custom Binary Search Tree is also included.

Module C is the Sorting Lab. It implements all five required sorting algorithms from first principles, which are Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, and Quick Sort (both the first-element pivot version with the yellow and green partition regions taught in class and the randomised-pivot version). You can step through every pass, merge or partition or let it auto-play with colour highlights. For every sort, the right panel shows the number of comparisons, the number of swaps or moves, the execution time, the theoretical best, average and worst time complexity, the extra space, and whether the sort is stable or unstable. There is also a Compare All button that runs all sorting algorithms on the exact same input array and displays their comparisons, swaps or moves, time and stability side by side in one table.

Module D is the Time Complexity Analysis Lab. It runs all sorting algorithms on input sizes n equals 100, 500, 1000, 2000, 5000 and 10000 (and also 50000 and 100000 for Merge Sort and Quick Sort) across Random, Already Sorted and Reverse Sorted inputs, averaging 3 runs per test. It displays the results in tables and plots running time against input size n on two charts, one chart for the O(n squared) sorts and one chart for the O(n log n) sorts.

## How the window is laid out

The top bar has the module and algorithm selectors. Depending on which module you pick, a second toolbar appears with the controls for that task, such as peg selectors for Hanoi, push/pop/bracket controls for the stack, linear and circular queue buttons, before/after reference value boxes for the linked lists, or the Compare All button for sorting. The left side is where the input goes. The middle is the drawing area where the bars, nodes, stack frames, queue cells, pegs, comparison tables and growth charts are drawn. The right side shows the sorted or output data, comparisons, swaps or moves, running time, stability and Big-O table. The bottom has the play, pause, step forward, step back and reset buttons, a speed slider and a scrolling log of every step.

Colours mean the same thing everywhere. Yellow is for elements being compared or the yellow partition region, red is for swaps or changes, and green is for elements that are sorted, found, or in the green partition region. Blue is the normal colour.

## How the tracking works

Each sorting algorithm tracks comparisonsCount, swapsCount (or movesCount for Merge Sort) and total operations right inside the loops, so the counts shown in the window and in the Compare All table are exact.

For the timing, we use System.nanoTime(). Drawing the steps would distort the clock, so each algorithm runs twice. The first run has no step recording at all and is the one that gets timed. The second run records a snapshot after each change to fill the step log and animation timeline.

## The files

The code is in src/main/java and is split into four packages, with one class per file.

The algorithms package has BubbleSort, SelectionSort, InsertionSort, MergeSort, QuickSort, SortingComparisonLab, FibonacciLab, FactorialLab, TowerOfHanoi and RecursiveBinarySearch.

The structures package has Node, DoublyNode, TreeNode, CustomSinglyLinkedList, CustomDoublyLinkedList, CustomStack, CustomLinearQueue, CustomQueue and CustomBinarySearchTree.

The ui package has Launcher, MainApp and VisualizationCanvas.

The model package has AlgorithmStep, StepCollector, ExecutionResult, ComplexityInfo, AlgorithmType, AlgorithmCategory, TreeNodeSnapshot, VisualizationType and ArrayUtils.

The file TEAM_SPLIT.md shows which files each of the five team members wrote.

## How to run it

You need a JDK installed (21 or newer works) and an internet connection the first time. Maven and Gradle are not needed.

Open PowerShell in the project folder and run the run.ps1 file. The first time it downloads the JavaFX jars into a folder called lib, then it compiles the code and opens the app.

    powershell -ExecutionPolicy Bypass -File run.ps1
