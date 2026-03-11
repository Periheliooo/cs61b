package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
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

    /*
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
        if(size == items.length){
            resize(items.length * 2);
        }
        nextLast = Math.floorMod(nextLast + 1, items.length);
    }
    */

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = Math.floorMod(nextFirst - 1, items.length);
        size++;
    }

    public void addLast(T item){
        if (size == items.length) {resize(items.length * 2);}
        items[nextLast] = item;
        nextLast = Math.floorMod(nextLast + 1, items.length);
        size++;
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
        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }
        return target;
    }

    public T removeLast(){
        if (isEmpty()) { return null; }
        int lastIndex = Math.floorMod(nextLast - 1, items.length);
        T target = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size--;
        if (items.length >= 16 && size < items.length / 4) {
            resize(items.length / 2);
        }
        return target;
    }

    public T get(int index){
        if(index >= size){
            return null;
        } else{
            return items[Math.floorMod(nextFirst + 1 + index, items.length)];
        }
    }

    /*
    public void resize(int capacity){
        T[] newitems;
        newitems = (T[]) new Object[capacity];
        int i = (nextFirst + 1) % items.length;
        int j = 0;
        while(i != nextLast){
            newitems[j] = items[i];
            i = (i + 1) % items.length;
            j++;
        }
        items = newitems;
        nextFirst = items.length - 1;
        nextLast = items.length / 2;
    }

     */

    private void resize(int capacity){
        T[] newitems;
        newitems = (T[]) new Object[capacity];
        int i = (nextFirst + 1) % items.length;
        for(int j = 0; j < size; j++){
            newitems[j] = items[i];
            i = (i + 1) % items.length;
        }
        items = newitems;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        private int i;

        public ArrayDequeIterator(){
            i = 0;
        }

        public boolean hasNext(){
            return (i < size());
        }

        public T next(){
            T returnItem = get(i);
            i++;
            return returnItem;
        }
    }

    public boolean equals(Object o){
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;

        if (this.size() != other.size()) {
            return false;
        }
        int i = 0;
        for (T item : this) {
            if (!item.equals(other.get(i))) {
                return false;
            }
            i++;
        }
        return true;
    }
}
