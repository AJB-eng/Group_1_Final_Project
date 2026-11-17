package model;

/**
 * Immutable snapshot of a Binary Search Tree node at a specific step in time.
 * Used by the visualization engine to render recursive tree hierarchies and highlight active nodes.
 */
public class TreeNodeSnapshot {
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_COMPARING = 1; // Yellow
    public static final int STATE_SWAPPING_OR_MODIFIED = 2; // Red
    public static final int STATE_VISITED_OR_FOUND = 3; // Green

    private final int value;
    private final int highlightState;
    private final TreeNodeSnapshot left;
    private final TreeNodeSnapshot right;

    public TreeNodeSnapshot(int value, int highlightState, TreeNodeSnapshot left, TreeNodeSnapshot right) {
        this.value = value;
        this.highlightState = highlightState;
        this.left = left;
        this.right = right;
    }

    public int getValue() {
        return value;
    }

    public int getHighlightState() {
        return highlightState;
    }

    public TreeNodeSnapshot getLeft() {
        return left;
    }

    public TreeNodeSnapshot getRight() {
        return right;
    }
}
