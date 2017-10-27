package org.jgrapht.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.teneighty.heap.Heap;

public class PlanningHeapWrapper<K extends Comparable<K>, V> {

    private Heap<K, V> heap;
    private Map<V, Heap.Entry<K, V>> entryMap;

    public PlanningHeapWrapper(Heap<K, V> heap) {
        this.heap = heap;
        this.entryMap = new HashMap<V, Heap.Entry<K, V>>();
    }

    public void insert(K key, V value) {
        Heap.Entry<K, V> entry = heap.insert(key, value);
        entryMap.put(value, entry);
    }

    public void remove(V value) {
        Heap.Entry<K, V> entry = entryMap.remove(value);
        if (entry != null) {
            heap.delete(entry);
        }
    }

    public Heap.Entry<K, V> extractMinimum() {
        Heap.Entry<K, V> entry = heap.extractMinimum();
        entryMap.remove(entry.getValue());
        return entry;
    }

    public Heap.Entry<K, V> peekMinimum() {
        return heap.getMinimum();
    }

    public void insertOrUpdateKey(K key, V value) {
        Heap.Entry<K, V> entry = entryMap.get(value);

        if (entry == null) {
            insert(key, value);
        } else {
            K oldKey = entry.getKey();
            if (key.compareTo(oldKey) < 0) {
                heap.decreaseKey(entry, key);
            } else {
                remove(value);
                insert(key, value);
            }
        }
    }

    public Iterator<Heap.Entry<K, V>> iterator() {
        return heap.iterator();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }
}
