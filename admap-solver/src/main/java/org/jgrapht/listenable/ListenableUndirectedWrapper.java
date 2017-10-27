package org.jgrapht.listenable;

import org.jgrapht.UndirectedGraph;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class ListenableUndirectedWrapper<V, E> extends ListenableWrapper<V, E> implements UndirectedGraph<V, E> {

    private UndirectedGraph<V, E> graph;

    ListenableUndirectedWrapper(UndirectedGraph<V, E> graph) {
        super(graph);
        this.graph = graph;
    }

    @Override
    public void setEdgeWeight(E e, double v) {
        throw new RuntimeException("Parent Graph is not weighted!");
    }

    @Override
    public int degreeOf(V vertex) {
        return graph.degreeOf(vertex);
    }
}
