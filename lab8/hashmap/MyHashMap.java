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
    // You should probably define some more!
    private double loadFactor = 0.75;
    private static int defaultSize = 16;
    private int size;
    private Set<K> set;
    /** Constructors */
    public MyHashMap() {
        this(defaultSize);
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        set = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this(initialSize);
        loadFactor = maxLoad;
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
        return new ArrayList<>(); // 默认是这个
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
        return new Collection[tableSize];
    }


    @Override
    public void clear() {
        size = 0;
        for (int i = 0; i < buckets.length; i++) {
            buckets[i].clear();
        }
        set.clear();
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> collection = buckets[index];
        for (Node node : collection) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if ((size + 1) * 1.0 / buckets.length > loadFactor) {
            resize(buckets.length * 2);
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        if (get(key) == null) {
            size++;
        }
        Collection<Node> collection = buckets[index];
        for (Node node : collection) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        Node node = createNode(key, value);
        collection.add(node);
    }

    private void resize(int capacity) {
        /** 重新创建一个hashtable */
        Collection<Node>[] temp = createTable(capacity);
        for (int i = 0; i < temp.length; i++) {
            temp[i] = createBucket();
        }
        /** 将原先的Node迁移到目前的hashtable中 */
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> bucket = buckets[i];
            for (Node node : bucket) {
                int index = Math.floorMod(node.key.hashCode(), temp.length);
                temp[index].add(node);
            }
        }
        this.buckets = temp;
    }

    @Override
    public Set<K> keySet() {
        for (int i = 0; i < buckets.length; i++) {
            Collection<Node> collection = buckets[i];
            for (Node node : collection) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        if (get(key) == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> collection = buckets[index];
        for (Node node : collection) {
            if (node.key.equals(key)) {
                collection.remove(node);
                return node.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) == null) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Collection<Node> collection = buckets[index];
        for (Node node : collection) {
            if (node.key.equals(key) && node.value.equals(value)) {
                collection.remove(node);
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
