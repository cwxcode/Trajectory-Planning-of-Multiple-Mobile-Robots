package org.jgrapht.listenable;

import org.jgrapht.DirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.listenable.event.EdgeChangeEvent;

public class ListenableDirectedWeightedWrapper<V, E> extends ListenableDirectedWrapper<V, E> implements WeightedGraph<V, E> {

    private WeightedGraph<V, E> graph;

    ListenableDirectedWeightedWrapper(DirectedGraph<V, E> graph) {
        super(graph);
        this.graph = (WeightedGraph<V, E>) graph;
    }

    @Override
    public void setEdgeWeight(E edge, double weight) {
        if (graph.containsEdge(edge)) {
            graph.setEdgeWeight(edge, weight);

            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);

            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_WEIGHT_CHANGED);
        }
    }
}