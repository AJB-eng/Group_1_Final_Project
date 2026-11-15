package structures;

/**
 * Custom pointer-based Node class for CustomSinglyLinkedList.
 * Implemented from scratch without any built-in Java collection wrappers.
 */
public class Node {
    public int data;
    public Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

