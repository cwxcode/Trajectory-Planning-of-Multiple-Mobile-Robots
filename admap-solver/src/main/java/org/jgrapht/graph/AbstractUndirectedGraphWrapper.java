package org.jgrapht.graph;

import org.jgrapht.UndirectedGraph;

public abstract class AbstractUndirectedGraphWrapper<V, E> extends AbstractGraphWrapper<V, E> implements UndirectedGraph<V, E> {

    @Override
    public int degreeOf(V vertex) {
        return edgesOf(vertex).size();
    }
}
