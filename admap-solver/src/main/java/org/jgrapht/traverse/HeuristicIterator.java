package org.jgrapht.traverse;

import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.util.FibonacciHeapNode;
import org.jgrapht.util.HeuristicToGoal;

public class HeuristicIterator<V, E> extends ClosestFirstIterator<V, E> {

    private final HeuristicToGoal<V> heuristicToGoal;
    private final HashMap<V, Double> shortestPathLengths;

    public HeuristicIterator(Graph<V, E> g, HeuristicToGoal<V> heuristic, V startVertex) {
        this(g, heuristic, startVertex, Double.MAX_VALUE);
    }

    public HeuristicIterator(Graph<V, E> g, HeuristicToGoal<V> heuristic, V startVertex, double radius) {
        super(g, startVertex, radius);
        this.heuristicToGoal = heuristic;
        this.shortestPathLengths = new HashMap<V, Double>();
    }

    @Override
    protected void encounterVertex(V vertex, E edge) {
        double shortestPathLength;
        double heuristicEstimate;

        if (edge == null) {
            shortestPathLength = 0; //vertex is a starting vertex
        } else {
            shortestPathLength = calculatePathLength(vertex, edge);
        }
        heuristicEstimate = heuristicToGoal.getCostToGoalEstimate(vertex);

        double key = shortestPathLength + heuristicEstimate;

        FibonacciHeapNode<QueueEntry<V, E>> node = createSeenData(vertex, edge);
        putSeenData(vertex, node);
        savePathLength(vertex, shortestPathLength);
        heap.insert(node, key);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
        FibonacciHeapNode<QueueEntry<V, E>> node = getSeenData(vertex);

        if (node.getData().frozen) {
            return;
        }

        double candidatePathLength = calculatePathLength(vertex, edge);
        double heuristicEstimate = heuristicToGoal.getCostToGoalEstimate(vertex);
        double candidateKey = candidatePathLength + heuristicEstimate;

        if (candidateKey < node.getKey()) {
            node.getData().spanningTreeEdge = edge;
            node.getData().vertex = vertex;
            heap.decreaseKey(node, candidateKey);
            savePathLength(vertex, candidatePathLength);
        }
    }

    private double calculatePathLength(V vertex, E edge) {
        assertNonNegativeEdge(edge);

        V otherVertex = Graphs.getOppositeVertex(getGraph(), edge, vertex);
        return shortestPathLengths.get(otherVertex)
                + getGraph().getEdgeWeight(edge);
    }

    private void savePathLength(V vertex, double length) {
        shortestPathLengths.put(vertex, length);
    }
}
