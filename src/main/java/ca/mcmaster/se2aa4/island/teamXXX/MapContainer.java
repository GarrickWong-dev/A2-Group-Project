package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Abstract class to manage a map with key-value pairs of type K and V
public abstract class MapContainer<K, V> implements Iterable<K> {
    
    // Map to store key-value pairs
    protected final Map<K, V> map = new HashMap<>();

    // Method to add a key-value pair to the map
    public void put(K key, V value) {
        map.put(key, value);
    }

    // Method to check if the map is empty
    public boolean isEmpty(){
        return map.isEmpty();
    }

    // Method to get the value associated with a key
    public V getValue(K key) {
        return map.get(key);
    }

    // Method to get the first key in the map (based on key set order)
    public K getFirtKey(){
        return map.keySet().stream().findFirst().get(); // Find the first key in the map
    }

    // Implementing the Iterable interface to allow iteration over the keys
    @Override
    public Iterator<K> iterator() {
        return map.keySet().iterator(); // Iterate over the keys of the map
    }
}
