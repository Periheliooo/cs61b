package deque;

public class LinkedListDeque<T> {
    private class Node{
        public Node prev;
        public T item;
        public Node next;

        public Node(Node p, T i, Node n){
            prev = p;
            item = i;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item){
        Node firstNode = new Node(sentinel, item, sentinel.next);
        sentinel.next.prev = firstNode;
        sentinel.next = firstNode;
        size++;
    }

    public void addLast(T item){
        Node lastNode = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.next = lastNode;
        sentinel.prev = lastNode;
        size++;
    }

    public boolean isEmpty(){
        return (size == 0);
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node curr = sentinel.next;
        while(curr != sentinel){
            System.out.println(curr.item);
            curr = curr.next;
        }
    }

    public T removeFirst(){
        if(size == 0){
            return null;
        } else {
            Node FirstNode = sentinel.next;
            sentinel.next = FirstNode.next;
            FirstNode.next.prev = sentinel;
            size--;
            return FirstNode.item;
        }
    }

    public T removeLast(){
        if(size == 0){
            return null;
        } else {
            Node LastNode = sentinel.prev;
            sentinel.prev = LastNode.prev;
            LastNode.prev.next = sentinel;
            size--;
            return LastNode.item;
        }
    }

    public T get(int index){
        Node curr = sentinel.next;
        int i = 0;
        while(i < index && curr != sentinel){
            curr = curr.next;
            i++;
        }
        if(curr == sentinel){
            return null;
        } else {
            return curr.item;
        }
    }

    public T getRecursive(int index){
        if(index >= size){
            return null;
        }
        return getRecursiveHelper(sentinel.next, index);
    }

    private T getRecursiveHelper(Node curr, int index){
        if (index == 0){
            return curr.item;
        }
        return getRecursiveHelper(curr.next, index - 1);
    }

    /** to be finished
    public Iterator<T> iterator(){

    }

    public boolean equals(Object o){

    }
     **/
}
