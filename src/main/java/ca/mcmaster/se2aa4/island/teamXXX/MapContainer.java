package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class MapContainer<K, V> implements Iterable<K> {
    protected final Map<K, V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key, value);
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public V getValue(K key) {
        return map.get(key);
    }

    public K getFirtKey(){
        return map.keySet().stream().findFirst().get();
    }

    @Override
    public Iterator<K> iterator() {
        return map.keySet().iterator(); // Iterate over keys
    }
}
