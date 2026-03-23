package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private double loadFactor;
    private HashSet<K> keys = new HashSet<>();
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        loadFactor = 0.75;
        for (int i = 0; i < 16; i++) {
            buckets[i] = createBucket();
        }
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        loadFactor = 0.75;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return (Collection<Node>[]) new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!


    /** Removes all of the mappings from this map. */
    public void clear() {
        buckets = createTable(buckets.length);
        keys = new HashSet<>();
        size = 0;
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return (keys.contains(key));
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int index = getBucketIndex(key);
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
                return i.value;
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        if (containsKey(key)) {
            for (Node i : buckets[index]) {
                if (i.key.equals(key)) {
                    i.value = value;
                }
            }
        } else {
            buckets[index].add(createNode(key, value));
            keys.add(key);
            size++;
            if ((double) size / buckets.length > loadFactor) {
                resize();
            }
        }
    }

    public void resize() {
        Collection<Node>[] buckets1 = buckets;
        buckets = createTable(buckets.length * 2);
        clear();
        for (int i = 0; i < buckets1.length; i++) {
            for (Node j : buckets1[i]) {
                put(j.key, j.value);
            }
        }
    }

    public int getBucketIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        return keys;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private class HashMapIterator implements Iterator<K> {
        private Iterator<K> setIterator;
        public HashMapIterator() {
            setIterator = keys.iterator();
        }

        public boolean hasNext() {
            return setIterator.hasNext();
        }

        public K next() {
            return setIterator.next();
        }
    }
}
