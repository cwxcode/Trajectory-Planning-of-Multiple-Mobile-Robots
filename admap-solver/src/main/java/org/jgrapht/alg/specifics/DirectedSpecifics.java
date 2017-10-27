package org.jgrapht.alg.specifics;

import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

/**
 * An implementation of {@link Specifics} for a directed graph.
 */
public class DirectedSpecifics<V, E>
        extends Specifics<V, E> {

    private DirectedGraph<V, E> graph;

    /**
     * Creates a new DirectedSpecifics object.
     *
     * @param g the graph for which this specifics object to be created.
     */
    public DirectedSpecifics(DirectedGraph<V, E> g) {
        graph = g;
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    Graph<V, E> getGraph() {
        return graph;
    }
}