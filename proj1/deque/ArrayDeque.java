package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T item){
        items[nextFirst] = item;
        size++;
        if(size == items.length){
            resize(items.length * 2);
        }
        nextFirst = Math.floorMod(nextFirst - 1, items.length);
    }

    public void addLast(T item){
        items[nextLast] = item;
        size++;
        resize(items.length * 2);
        nextLast = Math.floorMod(nextLast + 1, items.length);
    }

    public boolean isEmpty(){
        return (size == 0);
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        int i = (nextFirst + 1) % items.length;
        while(i != nextLast){
            System.out.println(items[i]);
            i = (i + 1) % items.length;
        }
    }

    public T removeFirst() {
        if (isEmpty()) { return null; }
        int firstIndex = Math.floorMod(nextFirst + 1, items.length);
        T target = items[firstIndex];
        items[firstIndex] = null;
        nextFirst = firstIndex;
        size--;
        return target;
    }

    public T removeLast(){
        if (isEmpty()) { return null; }
        int lastIndex = Math.floorMod(nextLast - 1, items.length);
        T target = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size--;
        return target;
    }

    public T get(int index){
        if(index >= size){
            return null;
        } else{
            return items[index + 1 + nextFirst];
        }
    }

    public void resize(int capacity){
        T[] newitems;
        newitems = (T[]) new Object[capacity];
        int i = (nextFirst + 1) % items.length;
        int j = 0;
        while(i != nextLast){
            newitems[j] = items[i];
            i = (i + 1) % items.length;
        }
        items = newitems;
        nextFirst = items.length - 1;
        nextLast = items.length / 2;
    }
}
