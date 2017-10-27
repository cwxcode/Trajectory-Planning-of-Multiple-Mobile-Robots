package org.jgrapht.listenable;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.listenable.event.EdgeChangeEvent;
import org.jgrapht.listenable.event.VertexChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public abstract class ListenableWrapper<V, E> implements WeightedGraph<V, E> {

    private Graph<V, E> graph;
    private Set<ListenableWrapperListener<V, E>> listeners;

    ListenableWrapper(Graph<V, E> graph) {
        this.listeners = new HashSet<ListenableWrapperListener<V, E>>();
        this.graph = graph;
    }

    public Graph<V, E> getGraph() {
        return graph;
    }

    // -------------- Wrapper GETTERS --------------
    @Override
    public Set<E> getAllEdges(V source, V target) {
        return graph.getAllEdges(source, target);
    }

    @Override
    public E getEdge(V source, V target) {
        return graph.getEdge(source, target);
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory() {
        return graph.getEdgeFactory();
    }

    @Override
    public V getEdgeSource(E edge) {
        return graph.getEdgeSource(edge);
    }

    @Override
    public V getEdgeTarget(E edge) {
        return graph.getEdgeTarget(edge);
    }

    @Override
    public double getEdgeWeight(E edge) {
        return graph.getEdgeWeight(edge);
    }

    @Override
    public boolean containsEdge(V source, V target) {
        return graph.containsEdge(source, target);
    }

    @Override
    public boolean containsEdge(E edge) {
        return graph.containsEdge(edge);
    }

    @Override
    public boolean containsVertex(V vertex) {
        return graph.containsVertex(vertex);
    }

    @Override
    public Set<V> vertexSet() {
        return graph.vertexSet();
    }

    @Override
    public Set<E> edgeSet() {
        return graph.edgeSet();
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        return graph.edgesOf(vertex);
    }
    // ---------------- Wrapper SETTERS ----------------

    @Override
    public E addEdge(V source, V target) {
        E edge = graph.addEdge(source, target);

        if (edge != null) {
            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_ADDED);
        }

        return edge;
    }

    @Override
    public boolean addEdge(V source, V target, E edge) {
        boolean isAdded = graph.addEdge(source, target, edge);

        if (isAdded) {
            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_ADDED);
        }

        return isAdded;
    }

    @Override
    public boolean addVertex(V vertex) {
        boolean isAdded = graph.addVertex(vertex);

        if (isAdded) {
            fireVertexEvent(vertex, VertexChangeEvent.VERTEX_ADDED);
        }

        return isAdded;
    }

    @Override
    public boolean removeEdge(E edge) {
        V source = graph.getEdgeSource(edge);
        V target = graph.getEdgeTarget(edge);

        boolean isRemoved = graph.removeEdge(edge);
        if (isRemoved) {
            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_REMOVED);
        }
        return isRemoved;
    }

    @Override
    public E removeEdge(V source, V target) {
        E edge = graph.removeEdge(source, target);

        if (edge != null) {
            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_REMOVED);
        }

        return edge;
    }

    @Override
    public boolean removeVertex(V vertex) {
        if (containsVertex(vertex)) {
            Set<E> touchingEdgesList = graph.edgesOf(vertex);
            removeAllEdges(new ArrayList<E>(touchingEdgesList));

            graph.removeVertex(vertex);
            fireVertexEvent(vertex, VertexChangeEvent.VERTEX_REMOVED);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        boolean modified = false;

        for (E e : edges) {
            modified |= removeEdge(e);
        }

        return modified;
    }

    @Override
    public Set<E> removeAllEdges(V source, V target) {
        Set<E> removed = getAllEdges(source, target);

        removeAllEdges(removed);

        return removed;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean modified = false;

        for (V vertex : vertices) {
            modified |= removeVertex(vertex);
        }

        return modified;
    }

    @Override
    public int hashCode() {
        return this.graph.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof ListenableWrapper) {
            Graph<V, E> target = ((ListenableWrapper) that).graph;
            return this.graph.equals(target);
        } else {
            return false;
        }
    }

    // -------------- Listenable --------------
    public void addListener(ListenableWrapperListener<V, E> listener) {
        listeners.add(listener);
    }

    public void removeListener(ListenableWrapperListener<V, E> listener) {
        listeners.remove(listener);
    }

    public boolean containsListener(ListenableWrapperListener<V, E> listener) {
        return listeners.contains(listener);
    }

    protected void fireVertexEvent(VertexChangeEvent<V, E> event) {
        for (ListenableWrapperListener<V, E> listener : listeners) {
            listener.handleVertexEvent(event);
        }
    }

    protected void fireEdgeEvent(EdgeChangeEvent<V, E> event) {
        for (ListenableWrapperListener<V, E> listener : listeners) {
            listener.handleEdgeEvent(event);
        }
    }

    protected void fireVertexEvent(V vertex, int type) {
        VertexChangeEvent<V, E> event = new VertexChangeEvent<V, E>(this, vertex, type);
        fireVertexEvent(event);
    }

    protected void fireEdgeEvent(E edge, V source, V target, int type) {
        EdgeChangeEvent<V, E> event = new EdgeChangeEvent<V, E>(this, edge, source, target, type);
        fireEdgeEvent(event);
    }
}
