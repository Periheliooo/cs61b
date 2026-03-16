package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    int size = 0;
    private BSTNode root;

    private class BSTNode {
        private K key;
        private V val;
        private BSTNode left;
        private BSTNode right;

        public BSTNode(K k, V v){
            key = k;
            val = v;
            left = null;
            right = null;
        }

    }

    /** Removes all of the mappings from this map. */
    public void clear() {
        size = 0;
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return (get(key) != null);
    }


    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return getHelper(root, key);
    }

    private V getHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        } else if (node.key.equals(key)) {
            return node.val;
        } else if (node.key.compareTo(key) > 0) {
            return getHelper(node.left, key);
        } else {
            return getHelper(node.right, key);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
            size += 1;
        } else {
            putHelper(root, key, value);
        }
    }

    private void putHelper(BSTNode node, K key, V value) {
        if (node.key.equals(key)) {
            node.val = value;
        } else if (node.key.compareTo(key) > 0) {
            if (node.left == null) {
                node.left = new BSTNode(key, value);
                size += 1;
            } else{
                putHelper(node.left, key, value);
            }
        } else {
            if (node.right == null) {
                node.right = new BSTNode(key, value);
                size += 1;
            } else{
                putHelper(node.right, key, value);
            }
        }
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrderHelper(root);
    }

    private void printInOrderHelper(BSTNode node) {
        if (node.left == null) {
            System.out.println(node.val);
            printInOrderHelper(node.right);
        } else {
          printInOrderHelper(node.left);
        }
    }
}
