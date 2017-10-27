package org.jgrapht.listenable;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;

public class ListenableWrapperFactory<V, E> {

    public static <V, E> ListenableWrapper<V, E> createListenableWrapper(Graph<V, E> graph) {
        if (graph instanceof DirectedGraph) {
            if (graph instanceof WeightedGraph) {
                return new ListenableDirectedWeightedWrapper<V, E>((DirectedGraph<V, E>) graph);
            } else {
                return new ListenableDirectedWrapper<V, E>((DirectedGraph<V, E>) graph);
            }

        } else {
            if (graph instanceof WeightedGraph) {
                return new ListenableUndirectedWeightedWrapper<V, E>((UndirectedGraph<V, E>) graph);
            } else {
                return new ListenableUndirectedWrapper<V, E>((UndirectedGraph<V, E>) graph);
            }
        }
    }
}
