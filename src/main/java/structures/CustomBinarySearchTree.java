package structures;

import model.AlgorithmType;
import model.ArrayUtils;
import model.ExecutionResult;
import model.StepCollector;
import model.TreeNodeSnapshot;

/**
 * Custom Binary Search Tree (BST) implemented 100% from scratch using recursive TreeNode pointers.
 * Supports recursive insertion, recursive deletion (leaf, 1-child, 2-children via inorder successor),
 * recursive search, and recursive Inorder, Preorder, and Postorder traversals.
 */
public class CustomBinarySearchTree {
    private TreeNode root;
    private int size;
    private long operationsCount;

    // Temporary traversal accumulator using raw primitive array
    private int[] traversalBuffer;
    private int traversalCount;

    public CustomBinarySearchTree() {
        this.root = null;
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
        return root == null;
    }

    public void clear() {
        root = null;
        size = 0;
        operationsCount = 0;
    }

    /**
     * Public entry point for recursive BST insertion.
     */
    public void insert(int value, StepCollector collector) {
        root = insertRecursive(root, value, collector);
    }

    private TreeNode insertRecursive(TreeNode current, int value, StepCollector collector) {
        operationsCount++; // Null check comparison: current == null
        if (current == null) {
            TreeNode created = new TreeNode(value);
            operationsCount++; // Node allocation
            size++;
            if (root == null) {
                root = created;
            }
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTreeWithRootOverride(root, created, value, TreeNodeSnapshot.STATE_VISITED_OR_FOUND),
                        getInorderRaw(),
                        operationsCount,
                        "Inserted new TreeNode(" + value + ") at null leaf position."
                );
            }
            return created;
        }

        if (collector != null) {
            collector.recordTreeStep(
                    snapshotTree(root, current.data, TreeNodeSnapshot.STATE_COMPARING, null),
                    getInorderRaw(),
                    operationsCount,
                    "Comparing insert value " + value + " with TreeNode(" + current.data + ")."
            );
        }

        operationsCount++; // Comparison: value < current.data
        if (value < current.data) {
            current.left = insertRecursive(current.left, value, collector);
            operationsCount++; // Pointer assignment: current.left
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, value, TreeNodeSnapshot.STATE_VISITED_OR_FOUND, null),
                        getInorderRaw(),
                        operationsCount,
                        "Linked " + value + " in left subtree of (" + current.data + ")."
                );
            }
        } else if (value > current.data) {
            operationsCount++; // Comparison: value > current.data
            current.right = insertRecursive(current.right, value, collector);
            operationsCount++; // Pointer assignment: current.right
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, value, TreeNodeSnapshot.STATE_VISITED_OR_FOUND, null),
                        getInorderRaw(),
                        operationsCount,
                        "Linked " + value + " in right subtree of (" + current.data + ")."
                );
            }
        } else {
            operationsCount++; // Duplicate detected
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, current.data, TreeNodeSnapshot.STATE_VISITED_OR_FOUND, null),
                        getInorderRaw(),
                        operationsCount,
                        "Value " + value + " already exists in BST (duplicates ignored)."
                );
            }
        }
        return current;
    }

    /**
     * Public entry point for recursive BST search.
     */
    public boolean search(int target, StepCollector collector) {
        return searchRecursive(root, target, collector);
    }

    private boolean searchRecursive(TreeNode current, int target, StepCollector collector) {
        operationsCount++; // Check current == null
        if (current == null) {
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, Integer.MIN_VALUE, TreeNodeSnapshot.STATE_DEFAULT, null),
                        getInorderRaw(),
                        operationsCount,
                        "Reached NULL child: target " + target + " NOT FOUND in BST."
                );
            }
            return false;
        }

        if (collector != null) {
            collector.recordTreeStep(
                    snapshotTree(root, current.data, TreeNodeSnapshot.STATE_COMPARING, null),
                    getInorderRaw(),
                    operationsCount,
                    "Recursive search: comparing target " + target + " with node (" + current.data + ")."
            );
        }

        operationsCount++; // Equality check: target == current.data
        if (target == current.data) {
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, current.data, TreeNodeSnapshot.STATE_VISITED_OR_FOUND, null),
                        getInorderRaw(),
                        operationsCount,
                        "Target " + target + " FOUND at TreeNode(" + current.data + ")!"
                );
            }
            return true;
        }

        operationsCount++; // Comparison: target < current.data
        if (target < current.data) {
            return searchRecursive(current.left, target, collector);
        } else {
            return searchRecursive(current.right, target, collector);
        }
    }

    /**
     * Public entry point for recursive BST deletion.
     */
    public void delete(int value, StepCollector collector) {
        root = deleteRecursive(root, value, collector);
    }

    private TreeNode deleteRecursive(TreeNode current, int value, StepCollector collector) {
        operationsCount++; // Check current == null
        if (current == null) {
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, Integer.MIN_VALUE, TreeNodeSnapshot.STATE_DEFAULT, null),
                        getInorderRaw(),
                        operationsCount,
                        "Delete(" + value + "): value not found in BST."
                );
            }
            return null;
        }

        if (collector != null) {
            collector.recordTreeStep(
                    snapshotTree(root, current.data, TreeNodeSnapshot.STATE_COMPARING, null),
                    getInorderRaw(),
                    operationsCount,
                    "Delete search: comparing " + value + " with node (" + current.data + ")."
            );
        }

        operationsCount++; // Comparison: value < current.data
        if (value < current.data) {
            current.left = deleteRecursive(current.left, value, collector);
            operationsCount++;
            return current;
        } else if (value > current.data) {
            operationsCount++; // Comparison: value > current.data
            current.right = deleteRecursive(current.right, value, collector);
            operationsCount++;
            return current;
        } else {
            operationsCount++; // Found node to delete
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, current.data, TreeNodeSnapshot.STATE_SWAPPING_OR_MODIFIED, null),
                        getInorderRaw(),
                        operationsCount,
                        "Found node (" + value + ") to delete."
                );
            }

            // Case 1 & 2: Leaf node or only one child
            operationsCount++;
            if (current.left == null) {
                size--;
                return current.right;
            }
            operationsCount++;
            if (current.right == null) {
                size--;
                return current.left;
            }

            // Case 3: Two children -> find inorder successor (smallest value in right subtree)
            int successorVal = findMinValue(current.right);
            if (collector != null) {
                collector.recordTreeStep(
                        snapshotTree(root, successorVal, TreeNodeSnapshot.STATE_SWAPPING_OR_MODIFIED, null),
                        getInorderRaw(),
                        operationsCount,
                        "Node (" + value + ") has 2 children. Replacing with inorder successor (" + successorVal + ")."
                );
            }
            current.data = successorVal;
            operationsCount++; // Data assignment
            current.right = deleteRecursive(current.right, successorVal, collector);
            operationsCount++; // Right pointer update
            return current;
        }
    }

    private int findMinValue(TreeNode node) {
        TreeNode curr = node;
        operationsCount++;
        while (curr.left != null) {
            operationsCount++;
            curr = curr.left;
            operationsCount++;
        }
        return curr.data;
    }

    /**
     * Recursive Inorder Traversal (Left -> Node -> Right). Produces sorted ascending order.
     */
    public int[] inorderTraversal(StepCollector collector) {
        initTraversalBuffer();
        inorderRecursive(root, collector);
        return getTrimmedTraversal();
    }

    private void inorderRecursive(TreeNode node, StepCollector collector) {
        operationsCount++; // Null check
        if (node == null) {
            return;
        }
        inorderRecursive(node.left, collector);

        appendToTraversal(node.data);
        operationsCount++; // Visit node

        if (collector != null) {
            int[] visited = getTrimmedTraversal();
            collector.recordTreeStep(
                    snapshotTree(root, node.data, TreeNodeSnapshot.STATE_COMPARING, visited),
                    visited,
                    operationsCount,
                    "Inorder Traversal (L -> Root -> R): visited node (" + node.data + ") -> " + ArrayUtils.arrayToString(visited)
            );
        }

        inorderRecursive(node.right, collector);
    }

    /**
     * Recursive Preorder Traversal (Node -> Left -> Right).
     */
    public int[] preorderTraversal(StepCollector collector) {
        initTraversalBuffer();
        preorderRecursive(root, collector);
        return getTrimmedTraversal();
    }

    private void preorderRecursive(TreeNode node, StepCollector collector) {
        operationsCount++; // Null check
        if (node == null) {
            return;
        }

        appendToTraversal(node.data);
        operationsCount++; // Visit node
if (collector != null) {
            int[] visited = getTrimmedTraversal();
            collector.recordTreeStep(
                    snapshotTree(root, node.data, TreeNodeSnapshot.STATE_COMPARING, visited),
                    visited,
                    operationsCount,
                    "Preorder Traversal (Root -> L -> R): visited node (" + node.data + ") -> " + ArrayUtils.arrayToString(visited)
            );
        }

        preorderRecursive(node.left, collector);
        preorderRecursive(node.right, collector);
    }

    /**
     * Recursive Postorder Traversal (Left -> Right -> Node).
     */
    public int[] postorderTraversal(StepCollector collector) {
        initTraversalBuffer();
        postorderRecursive(root, collector);
        return getTrimmedTraversal();
    }

    private void postorderRecursive(TreeNode node, StepCollector collector) {
        operationsCount++; // Null check
        if (node == null) {
            return;
        }

        postorderRecursive(node.left, collector);
        postorderRecursive(node.right, collector);

        appendToTraversal(node.data);
        operationsCount++; // Visit node

        if (collector != null) {
            int[] visited = getTrimmedTraversal();
            collector.recordTreeStep(
                    snapshotTree(root, node.data, TreeNodeSnapshot.STATE_COMPARING, visited),
                    visited,
                    operationsCount,
                    "Postorder Traversal (L -> R -> Root): visited node (" + node.data + ") -> " + ArrayUtils.arrayToString(visited)
            );
        }
    }

    private void initTraversalBuffer() {
        int cap = size > 0 ? size + 4 : 16;
        traversalBuffer = new int[cap];
        traversalCount = 0;
    }

    private void appendToTraversal(int value) {
        if (traversalCount >= traversalBuffer.length) {
            int[] next = new int[traversalBuffer.length * 2];
            for (int i = 0; i < traversalCount; i++) {
                next[i] = traversalBuffer[i];
            }
            traversalBuffer = next;
        }
        traversalBuffer[traversalCount++] = value;
    }

    private int[] getTrimmedTraversal() {
        int[] res = new int[traversalCount];
        for (int i = 0; i < traversalCount; i++) {
            res[i] = traversalBuffer[i];
        }
        return res;
    }

    public int[] getInorderRaw() {
        int[] buf = new int[size > 0 ? size + 2 : 4];
        int[] countHolder = new int[]{0};
        fillInorderRaw(root, buf, countHolder);
        int[] res = new int[countHolder[0]];
        for (int i = 0; i < countHolder[0]; i++) {
            res[i] = buf[i];
        }
        return res;
    }

    private void fillInorderRaw(TreeNode node, int[] buf, int[] countHolder) {
        if (node == null) {
            return;
        }
        fillInorderRaw(node.left, buf, countHolder);
        if (countHolder[0] < buf.length) {
            buf[countHolder[0]++] = node.data;
        }
        fillInorderRaw(node.right, buf, countHolder);
    }

    private TreeNodeSnapshot snapshotTreeWithRootOverride(
            TreeNode currentRoot,
            TreeNode newlyCreated,
            int highlightVal,
            int highlightState) {
        TreeNode effectiveRoot = currentRoot != null ? currentRoot : newlyCreated;
        return snapshotTree(effectiveRoot, highlightVal, highlightState, null);
    }

    private TreeNodeSnapshot snapshotTree(
            TreeNode node,
            int activeValue,
            int activeState,
            int[] visitedValues) {
        if (node == null) {
            return null;
        }
        int state = TreeNodeSnapshot.STATE_DEFAULT;
        if (node.data == activeValue) {
            state = activeState;
        } else if (visitedValues != null && ArrayUtils.contains(visitedValues, node.data)) {
            state = TreeNodeSnapshot.STATE_VISITED_OR_FOUND;
        }
        TreeNodeSnapshot leftSnap = snapshotTree(node.left, activeValue, activeState, visitedValues);
        TreeNodeSnapshot rightSnap = snapshotTree(node.right, activeValue, activeState, visitedValues);
        return new TreeNodeSnapshot(node.data, state, leftSnap, rightSnap);
    }

    /**
     * Executes a BST workflow and returns an ExecutionResult with pure nanoTime metrics.
     */
    public ExecutionResult executeFullWorkflow(int[] inputValues, int targetValue, String mode) {
        StepCollector collector = new StepCollector();

        // Clone current state into a timing tree for pure nanoTime measurement
        new TreeNode(0);
        CustomBinarySearchTree timingTree = new CustomBinarySearchTree();
        int[] existingNodes = getPreorderRaw();
        for (int i = 0; i < existingNodes.length; i++) {
            timingTree.insert(existingNodes[i], null);
        }
        timingTree.resetCounter();

        long startTime = System.nanoTime();
        runModeInternal(timingTree, inputValues, targetValue, mode, null);
        long elapsedNanos = System.nanoTime() - startTime;

        // Visual trace run
        resetCounter();
        String summary = runModeInternal(this, inputValues, targetValue, mode, collector);

        return new ExecutionResult(
                AlgorithmType.BINARY_SEARCH_TREE,
                summary,
                collector.toArray(),
                this.operationsCount,
                elapsedNanos
        );
    }

    private int[] getPreorderRaw() {
        int[] buf = new int[size > 0 ? size + 2 : 4];
        int[] countHolder = new int[]{0};
        fillPreorderRaw(root, buf, countHolder);
        int[] res = new int[countHolder[0]];
        for (int i = 0; i < countHolder[0]; i++) {
            res[i] = buf[i];
        }
        return res;
    }

    private void fillPreorderRaw(TreeNode node, int[] buf, int[] countHolder) {
        if (node == null) {
            return;
        }
        if (countHolder[0] < buf.length) {
            buf[countHolder[0]++] = node.data;
        }
        fillPreorderRaw(node.left, buf, countHolder);
        fillPreorderRaw(node.right, buf, countHolder);
    }

    private String runModeInternal(
            CustomBinarySearchTree bst,
            int[] inputValues,
            int targetValue,
            String mode,
            StepCollector collector) {
        if ("INSERT".equals(mode)) {
            bst.insert(targetValue, collector);
            if (collector == null) return "";
            return "Inserted " + targetValue + " | Inorder: " + ArrayUtils.arrayToString(bst.getInorderRaw());
        } else if ("DELETE".equals(mode)) {
            bst.delete(targetValue, collector);
            if (collector == null) return "";
            collector.recordTreeStep(
                    bst.snapshotTree(bst.root, Integer.MIN_VALUE, TreeNodeSnapshot.STATE_DEFAULT, null),
                    bst.getInorderRaw(),
                    bst.getOperationsCount(),
                    "Completed delete(" + targetValue + "). Current Inorder: " + ArrayUtils.arrayToString(bst.getInorderRaw())
            );
            return "Deleted " + targetValue + " | Inorder: " + ArrayUtils.arrayToString(bst.getInorderRaw());
        } else if ("SEARCH".equals(mode)) {
            boolean found = bst.search(targetValue, collector);
            if (collector == null) return "";
            return (found ? "Found " + targetValue + " in BST" : "Value " + targetValue + " Not Found")
                    + " | Inorder: " + ArrayUtils.arrayToString(bst.getInorderRaw());
        } else if ("INORDER".equals(mode)) {
            int[] order = bst.inorderTraversal(collector);
            if (collector == null) return "";
            return "Inorder (L-Root-R): " + ArrayUtils.arrayToString(order);
        } else if ("PREORDER".equals(mode)) {
            int[] order = bst.preorderTraversal(collector);
            if (collector == null) return "";
            return "Preorder (Root-L-R): " + ArrayUtils.arrayToString(order);
        } else if ("POSTORDER".equals(mode)) {
            int[] order = bst.postorderTraversal(collector);
            if (collector == null) return "";
            return "Postorder (L-R-Root): " + ArrayUtils.arrayToString(order);
        } else {
            // Default full workflow: build BST from inputValues, search targetValue, run Inorder/Preorder/Postorder
            bst.clear();
            for (int i = 0; i < inputValues.length; i++) {
                bst.insert(inputValues[i], collector);
            }
            int[] in = bst.inorderTraversal(collector);
            int[] pre = bst.preorderTraversal(collector);
            int[] post = bst.postorderTraversal(collector);
            boolean found = bst.search(targetValue, collector);
            if (collector == null) return "";
            return "Inorder: " + ArrayUtils.arrayToString(in)
                    + " | Preorder: " + ArrayUtils.arrayToString(pre)
                    + " | Postorder: " + ArrayUtils.arrayToString(post)
                    + " | Search(" + targetValue + ")=" + (found ? "FOUND" : "NOT FOUND");
        }
