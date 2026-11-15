package structures;

/**
 * Custom pointer-based TreeNode class for CustomBinarySearchTree.
 * Holds integer payload and explicit left/right child pointers.
 */
public class TreeNode {
    public int data;
    public TreeNode left;
    public TreeNode right;

    public TreeNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

