package org.jgrapht.alg.specifics;

import java.util.Set;

import org.jgrapht.Graph;

/**
 * An implementation of {@link Specifics} in which edge direction (if any) is
 * ignored.
 */
public class UndirectedSpecifics<V, E> extends Specifics<V, E> {

    private Graph<V, E> graph;

    /**
     * Creates a new UndirectedSpecifics object.
     *
     * @param g the graph for which this specifics object to be created.
     */
    public UndirectedSpecifics(Graph<V, E> g) {
        graph = g;
    }

    /**
     * @see CrossComponentIterator.Specifics#edgesOf(Object)
     */
    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    Graph<V, E> getGraph() {
        return graph;
    }
}
