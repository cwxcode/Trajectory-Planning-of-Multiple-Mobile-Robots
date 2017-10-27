package org.jgrapht.alg.specifics;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides unified interface for operations that are different in directed
 * graphs and in undirected graphs.
 */
public abstract class Specifics<V, E> {

    abstract Graph<V, E> getGraph();

    public abstract Set<E> outgoingEdgesOf(V vertex);

    public abstract Set<E> incomingEdgesOf(V vertex);

    public Set<? extends E> edgesOf(V vertex) {
        Set<E> edges = new LinkedHashSet<E>();
        edges.addAll(outgoingEdgesOf(vertex));
        edges.addAll(incomingEdgesOf(vertex));
        return edges;
    }

    public Set<V> predecessorVertexSet(V vertex) {
        return toSet(predecessorVertexIterator(vertex));
    }

    public Set<V> succesorVertexSet(V vertex) {
        return toSet(succesorVertexIterator(vertex));
    }

    private Set<V> toSet(Iterator<V> iterator) {
        Set<V> set = new HashSet<V>();
        while (iterator.hasNext()) {
            V v = iterator.next();
            set.add(v);
        }
        return set;
    }

    public Iterator<V> predecessorVertexIterator(V vertex) {
        return opositeVertexIterator(vertex, incomingEdgesOf(vertex));
    }

    public Iterator<V> succesorVertexIterator(V vertex) {
        return opositeVertexIterator(vertex, outgoingEdgesOf(vertex));
    }

    private Iterator<V> opositeVertexIterator(final V vertex, Set<E> edges) {
        final Iterator<E> edgeIterator = edges.iterator();

        Iterator<V> verticeIterator = new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return edgeIterator.hasNext();
            }

            @Override
            public V next() {
                E next = edgeIterator.next();
                return Graphs.getOppositeVertex(getGraph(), next, vertex);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing vertices is not supported.");
            }
        };

        return verticeIterator;
    }
}