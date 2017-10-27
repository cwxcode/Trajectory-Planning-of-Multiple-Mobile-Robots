package org.jgrapht.listenable;

import org.jgrapht.DirectedGraph;

import java.util.Set;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class ListenableDirectedWrapper<V, E> extends ListenableWrapper<V, E> implements DirectedGraph<V, E> {

    private DirectedGraph<V, E> graph;

    ListenableDirectedWrapper(DirectedGraph<V, E> graph) {
        super(graph);
        this.graph = graph;
    }

    @Override
    public void setEdgeWeight(E e, double v) {
        throw new RuntimeException("Parent Graph is not weighted!");
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
}
