package structures;

/**
 * Custom pointer-based DoublyNode class for CustomDoublyLinkedList.
 * Contains integer data and explicit next and prev node references.
 */
public class DoublyNode {
    public int data;
    public DoublyNode next;
    public DoublyNode prev;

    public DoublyNode(int data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
