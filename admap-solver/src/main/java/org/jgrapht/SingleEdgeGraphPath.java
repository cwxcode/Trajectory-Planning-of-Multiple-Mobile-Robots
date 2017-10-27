package org.jgrapht;

import java.util.ArrayList;
import java.util.List;

public class SingleEdgeGraphPath<V, E> implements GraphPath<V, E> {
    Graph<V, E> graph;
    private V start;
    private V end;
    private E edge;
    private double weight;

    public SingleEdgeGraphPath(Graph<V, E> graph, V start, E edge, V end, double weight) {
        super();
        this.graph = graph;
        this.edge = edge;
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public SingleEdgeGraphPath(Graph<V, E> graph, E edge) {
        super();
        this.graph = graph;
        this.edge = edge;
        this.start = graph.getEdgeSource(edge);
        this.end = graph.getEdgeTarget(edge);
        this.weight = graph.getEdgeWeight(edge);
    }

    @Override
    public Graph<V, E> getGraph() {
        return graph;
    }

    @Override
    public V getStartVertex() {
        return start;
    }

    @Override
    public V getEndVertex() {
        return end;
    }

    @Override
    public List<E> getEdgeList() {
        List<E> list = new ArrayList<E>();
        list.add(edge);
        return list;
    }

    @Override
    public double getWeight() {
        return weight;
    }

}
