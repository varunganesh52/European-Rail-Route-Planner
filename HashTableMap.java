/*
 * Author: Varun Ganesh
 * Email: vganesh6@wisc.edu
 * Course: CS400
 * Assignment: P2.12: Hashtable
 */

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;

public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

    /**
     * Inner class used to store a key-value pair
     */
    protected class Pair {
        public KeyType key;
        public ValueType value;

        /**
         * Constructor for Pair class that creates a new key-value pair
         * @param key the key to store
         * @param value the value to store
         */
        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    protected LinkedList<Pair>[] table = null;
    private int size;

    /**
     * Creates new hash table with given initial capacity
     * @param capacity starting capacity of table
     */
    @SuppressWarnings("unchecked")
    public HashTableMap(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity cannot be below 1");
        }
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        size = 0;
    }

    /**
     * No-args default constructor with capacity 8
     */
    public HashTableMap() {
        this(8);
    }

    /**
     * Adds a new key,value pair/mapping to this collection.
     * @param key the key of the key,value pair
     * @param value the value that key maps to (may be null)
     * @throws IllegalArgumentException if key already maps to a value without
     *         making any changes to the table
     * @throws NullPointerException if key is null
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
    }

    /**
     * Retrieves the specific value that a key maps to.
     * @param key the key to look up
     * @return the value that key maps to
     * @throws NoSuchElementException when key is not stored in this collection
     * @throws NullPointerException if key is null
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("Key must exist!");
        }

        int hash = key.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        int index = hash % table.length;

        if (table[index] != null) {
            for (Pair pair : table[index]) {
                if (pair.key.equals(key)) {
                    return pair.value;
                }
            }
        }

        throw new NoSuchElementException("Key was not found!");

    }

    /**
     * Checks whether a key maps to a value in this collection.
     * @param key the key to check
     * @throws NullPointerException if key is null
     * @return true if the key maps to a value, and false is the key doesn't
     *         map to a value
     */
    @Override
    public boolean containsKey(KeyType key) {

        if (key == null) {
            throw new NullPointerException("Key must exist!");
        }

        int hash = key.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        int index = hash % table.length;

        if (table[index] == null) {
            return false;
        }

        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }

        return false;
    }
}


