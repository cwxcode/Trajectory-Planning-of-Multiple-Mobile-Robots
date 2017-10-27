package org.jgrapht.graph;

import org.jgrapht.DirectedGraph;
import tt.util.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDirectedGraphWrapper<V, E> extends AbstractGraphWrapper<V, E> implements DirectedGraph<V, E> {

    @Override
    public int inDegreeOf(V vertex) {
        return incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public int outDegreeOf(V vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        HashSet<E> edges = new HashSet<E>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }
}
