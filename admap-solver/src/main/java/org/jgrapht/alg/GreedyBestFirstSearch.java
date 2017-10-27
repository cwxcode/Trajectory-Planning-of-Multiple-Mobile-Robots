package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import java.util.*;

public class GreedyBestFirstSearch<V, E> extends PlanningAlgorithm<V, E> {

    protected GraphPath<V, E> path;
    protected Set<V> visited;
    protected HeuristicToGoal<V> heuristic;
    protected double radius;

    protected V current;
    protected int iteration;
    protected List<E> edgeList = new ArrayList<E>();


    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, double radius) {
        return findPathBetween(graph, heuristic, startVertex, goal, radius, Integer.MAX_VALUE);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, double radius, int iterationLimit) {
        GreedyBestFirstSearch<V, E> alg = new GreedyBestFirstSearch<V, E>(graph, heuristic, startVertex, goal, radius);
        return alg.findPath(iterationLimit);
    }

    public GreedyBestFirstSearch(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, double radius) {
        super(graph, startVertex, goal);
        this.visited = new HashSet<V>();
        this.heuristic = heuristic;
        this.radius = radius;
    }

    public GraphPath<V, E> findPath(int iterationLimit) {

        double currentPathCost = 0;
        current = startVertex;

        while (currentPathCost < radius && visited.size() < iterationLimit && !goal.isGoal(current)) {
            iteration++;
            visited.add(current);

            double minCost = Double.POSITIVE_INFINITY;
            V bestSuccessorVertex = null;
            E bestSuccessorEdge = null;

            Set<E> outgoingEdges = specifics.outgoingEdgesOf(current);
            for (E edge : outgoingEdges) {
                V successor = Graphs.getOppositeVertex(graph, edge, current);

                if (visited.contains(successor))
                    continue;

                double edgeCost = graph.getEdgeWeight(edge);
                double costToGoalEstimate = heuristic.getCostToGoalEstimate(successor);

                double costToGoEstimate = edgeCost + costToGoalEstimate;
                if (costToGoEstimate < minCost) {
                    minCost = costToGoEstimate;
                    bestSuccessorVertex = successor;
                    bestSuccessorEdge = edge;
                }
            }

            if (bestSuccessorVertex == null)
                break;

            edgeList.add(bestSuccessorEdge);

            current = bestSuccessorVertex;
            currentPathCost += graph.getEdgeWeight(bestSuccessorEdge);
        }

        path = new GraphPathImpl<V, E>(graph, startVertex, current, edgeList, currentPathCost);

        if (!goal.isGoal(current)) {
            return null;
        } else {
            return path;
        }
    }

    @Override
    public GraphPath<V, E> getTraversedPath() {
        return path;
    }

    @Override
    public Collection<V> getOpenedNodes() {
        return null;
    }

    @Override
    public Collection<V> getClosedNodes() {
        return visited;
    }

    @Override
    public V getCurrentVertex() {
        return current;
    }

    @Override
    public int getIterationCount() {
        return iteration;
    }
}
