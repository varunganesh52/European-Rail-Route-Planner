/*
 * Author: Varun Ganesh
 * Email: vganesh6@wisc.edu
 * Course: CS400
 * Assignment: P2.12: Hashtable
 */

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class that implements MapADT to create a hash table using an array of linked lists
 * Collisions are handled through chaining, table is resized automatically when
 * load factor is >= 0.75
 * Contains methods for insertion, removal, traversal, etc.
 * @param <KeyType> type of keys stored in Map
 * @param <ValueType> type of values stored in Map
 */
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
        if (key == null) {
            throw new NullPointerException("Key must exist!");
        }

        int index = Math.abs(key.hashCode()) % table.length;

        //Check linked list at this index for potential duplicate key
        if (table[index] != null) {
            for (Pair pair : table[index]) {
                if (pair.key.equals(key)) {
                    throw new IllegalArgumentException("Duplicate keys not allowed!");
                }
            }
        }

        //Resize and rehash if load factor will be >= .75 after insertion
        if (((double) (size + 1) / table.length) >= 0.75) {
            LinkedList<Pair>[] oldTable = table;
            LinkedList<Pair>[] newTable = (LinkedList<Pair>[]) new LinkedList[oldTable.length * 2];

            //Rehash every old key-value pair into new table, based on new table length
            for (int i = 0; i < oldTable.length; i++) {
                if (oldTable[i] != null) {
                    for (Pair pair : oldTable[i]) {
                        int newIndex = Math.abs(pair.key.hashCode()) % newTable.length;

                        if (newTable[newIndex] == null) {
                            newTable[newIndex] = new LinkedList<Pair>();
                        }

                        newTable[newIndex].add(pair);
                    }
                }
            }

            //Replace old table with resized one and recompute key index
            table = newTable;
            index = Math.abs(key.hashCode()) % table.length;
        }

        if (table[index] == null) {
            table[index] = new LinkedList<Pair>();
        }

        //Add new key-value pair at index value
        table[index].add(new Pair(key, value));
        size++;
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

        int index = Math.abs(key.hashCode()) % table.length;

        //Search through table at index
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

        int index = Math.abs(key.hashCode()) % table.length;

        if (table[index] == null) {
            return false;
        }

        //Search through table for matching key
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Remove the mapping for a key from this collection.
     * @param key the key whose mapping to remove
     * @return the value that the removed key mapped to
     * @throws NoSuchElementException when key is not stored in this collection
     * @throws NullPointerException if key is null
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("Key must exist!");
        }

        int index = Math.abs(key.hashCode()) % table.length;

        //Search through table at this index
        if (table[index] != null) {
            for (int i = 0; i < table[index].size(); i++) {
                Pair pair = table[index].get(i);

                //If key match found, remove pair and return its value
                if (pair.key.equals(key)) {
                    ValueType remove = pair.value;
                    table[index].remove(i);
                    size--;
                    return remove;
                }
            }
        }

        throw new NoSuchElementException("Key was not found!");
    }

    /**
     * Removes all key,value pairs from this collection without changing the
     * capacity of the underlying array.
     */
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /**
     * Retrieves the number of keys stored in this collection.
     * @return the number of keys stored in this collection
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Retrieves this collection's capacity.
     * @return the size of the underlying array for this collection
     */
    @Override
    public int getCapacity() {
        return table.length;
    }

    /**
     * Retrieves this collection's keys.
     * @return a list of keys in the underlying array for this collection
     */
    @Override
    public List<KeyType> getKeys() {
        LinkedList<KeyType> keys = new LinkedList<KeyType>();

        //Go through each index and every key from index into list
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                for (Pair pair : table[i]) {
                    keys.add(pair.key);
                }
            }
        }

        return keys;
    }

    /**
     * Tests put(), get(), and containsKey() work properly
     * when inserting and getting a key-value pair
     */
    @Test
    public void testInsertion() {
        HashTableMap<String, Integer> table = new HashTableMap<>();
        table.put("Varun", 5);

        //Checks that key is stored, correct value returned, and size is updated
        assertTrue(table.containsKey("Varun"));
        assertEquals(5, table.get("Varun"));
        assertEquals(1, table.getSize());
    }

    /**
     * Tests that put() throws IllegalArgumentException when inserting a
     * duplicate key
     */
    @Test
    public void testDuplicateKeyInsertion() {
        HashTableMap<String, Integer> table = new HashTableMap<>();
        table.put("Varun", 7);

        //Checks that duplicate key will throw exception, and original size and value are unchanged
        assertThrows(IllegalArgumentException.class, () -> {table.put("Varun", 2);});
        assertEquals(7, table.get("Varun"));
        assertEquals(1, table.getSize());
    }

    /**
     * Tests that remove() removes a key-value pair properly,
     * returning correct value and NoSuchElementException thrown if
     * there are attempts to access removed key in table
     */
    @Test
    public void testRemove() {
        HashTableMap<String, Integer> table = new HashTableMap<>();
        table.put("First", 7);
        table.put("Second", 4);
        table.put("Third", 2);

        //Check that correct value is returned after removal, and that removed key does not exist in table
        assertEquals(4, table.remove("Second"));
        assertFalse(table.containsKey("Second"));

        //Check that size is decremented and exception is thrown if user tries to access the removed key
        assertEquals(2, table.getSize());
        assertThrows(NoSuchElementException.class, () -> {table.get("Second");});
    }

    /**
     * Tests that clear() removes all pairs without changing table capacity
     * when the no-args default constructor is used
     */
    @Test
    public void testClear() {
        HashTableMap<String, Integer> table = new HashTableMap<>();
        table.put("First", 7);
        table.put("Second", 4);
        table.put("Third", 2);

        table.clear();

        //Check that table and keys list are empty while capacity stays the same
        assertEquals(0, table.getSize());
        assertEquals(0, table.getKeys().size());
        assertEquals(8, table.getCapacity());
    }

    /**
     * Tests that table resizes automatically when load factor threshold is reached
     * (0.75) and that all pairs are rehashed properly and still exist within new table
     */
    @Test
    public void testResize() {
        HashTableMap<Integer, String> table = new HashTableMap<>(4);
        //Insert pairs where capacity should still be 4 since load factor < 0.75
        table.put(1, "First");
        table.put(2, "Second");
        assertEquals(4, table.getCapacity());

        //Insert a pair which causes table resizing and rehashing
        table.put(3, "Third");
        assertEquals(8, table.getCapacity());

        //Checks that both old and new pairs are present and can access after rehashing
        assertEquals("First", table.get(1));
        assertEquals("Second", table.get(2));
        assertEquals("Third", table.get(3));
        assertTrue(table.containsKey(1));
        assertTrue(table.containsKey(2));
        assertTrue(table.containsKey(3));
        assertEquals(3, table.getSize());
    }
}


