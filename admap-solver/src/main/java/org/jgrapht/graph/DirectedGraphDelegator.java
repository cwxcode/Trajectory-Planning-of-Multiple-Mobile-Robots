package org.jgrapht.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

import java.util.Collection;
import java.util.Set;

public abstract class DirectedGraphDelegator<V, E> implements DirectedGraph<V, E> {

    private DirectedGraph<V, E> graph;

    protected DirectedGraphDelegator(DirectedGraph<V, E> graph) {
        this.graph = graph;
    }

    @Override
    public int inDegreeOf(V vertex) {
        return graph.inDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        return graph.outDegreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        return graph.getAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        return graph.getEdge(sourceVertex, targetVertex);
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory() {
        return graph.getEdgeFactory();
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        return graph.addEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        return graph.addEdge(sourceVertex, targetVertex, e);
    }

    @Override
    public boolean addVertex(V v) {
        return graph.addVertex(v);
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        return graph.containsEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean containsEdge(E e) {
        return graph.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return graph.containsVertex(v);
    }

    @Override
    public Set<E> edgeSet() {
        return graph.edgeSet();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        return graph.removeAllEdges(edges);
    }

    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        return graph.removeAllEdges(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        return graph.removeAllVertices(vertices);
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        return graph.removeEdge(sourceVertex, targetVertex);
    }

    @Override
    public boolean removeEdge(E e) {
        return graph.removeEdge(e);
    }

    @Override
    public boolean removeVertex(V v) {
        return graph.removeVertex(v);
    }

    @Override
    public Set<V> vertexSet() {
        return graph.vertexSet();
    }

    @Override
    public V getEdgeSource(E e) {
        return graph.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return graph.getEdgeTarget(e);
    }

    @Override
    public double getEdgeWeight(E e) {
        return graph.getEdgeWeight(e);
    }
}
