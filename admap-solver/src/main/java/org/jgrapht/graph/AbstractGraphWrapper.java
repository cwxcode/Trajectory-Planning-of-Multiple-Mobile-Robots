package org.jgrapht.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import tt.util.NotImplementedException;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractGraphWrapper<V, E> implements Graph<V, E> {

    //TODO implement all most general functions (remove all XY, etc.)

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory() {
        throw new NotImplementedException();
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addVertex(V v) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(E e) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsVertex(V v) {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> edgeSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        throw new NotImplementedException();
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEdge(E e) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeVertex(V v) {
        throw new NotImplementedException();
    }

    @Override
    public Set<V> vertexSet() {
        throw new NotImplementedException();
    }

    @Override
    public V getEdgeSource(E e) {
        throw new NotImplementedException();
    }

    @Override
    public V getEdgeTarget(E e) {
        throw new NotImplementedException();
    }

    @Override
    public double getEdgeWeight(E e) {
        throw new NotImplementedException();
    }
}
