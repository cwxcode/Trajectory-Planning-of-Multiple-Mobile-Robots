package org.jgrapht.util;

public class QueueEntry<V, K extends Comparable<K>> implements Comparable<QueueEntry<V, K>> {

    public V vertex;
    public K key;

    public QueueEntry(V vertex, K key) {
        this.vertex = vertex;
        this.key = key;
    }

    public QueueEntry() {
    }

    @Override
    public int compareTo(QueueEntry<V, K> o) {
        return key.compareTo(o.key);
    }

    @Override
    public int hashCode() {
        return vertex.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueueEntry<V, K> other = (QueueEntry<V, K>) obj;
        if (vertex != other.vertex && (vertex == null || !vertex.equals(other.vertex))) {
            return false;
        }
        return true;
    }
}
