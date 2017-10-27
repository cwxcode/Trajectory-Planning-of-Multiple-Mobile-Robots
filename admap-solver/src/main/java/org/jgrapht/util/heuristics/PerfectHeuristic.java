package org.jgrapht.util.heuristics;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import java.util.Map;

public class PerfectHeuristic<S, E> implements HeuristicToGoal<S> {

    private Graph<S, E> graph;
    private Map<S, Double> distances;

    public PerfectHeuristic(Graph<S, E> graph, S goal) {
        if (graph instanceof DirectedGraph) {
        	this.graph = new EdgeReversedGraph<S, E>((DirectedGraph<S, E>) graph);
        } else {
        	this.graph = graph;
        }
        
        this.distances = getDistances(goal);
    }

    @SuppressWarnings("unchecked")
    private Map<S, Double> getDistances(S goal) {
        return DijkstraShortestPath.calculateDistancesToGoal(graph, goal);
    }

    @Override
    public double getCostToGoalEstimate(S current) {
        Double estimate = distances.get(current);
        return (estimate == null) ? Double.POSITIVE_INFINITY : estimate;
    }

    private static class DijkstraShortestPath<S, E> extends AStarShortestPathSimple<S, E> {

        private static <S, E> Map<S, Double> calculateDistancesToGoal(Graph<S, E> graph, S goal) {
        	
            DijkstraShortestPath<S, E> alg = new DijkstraShortestPath<S, E>(graph, goal, new Goal<S>() {
                @Override
                public boolean isGoal(S current) {
                    return false;
                }
            });

            alg.findPath(Integer.MAX_VALUE);
            return alg.getShortestDistances();
        }

        private DijkstraShortestPath(Graph<S, E> graph, S startVertex, Goal<S> goal) {
            super(graph, new ZeroHeuristic<S>(), startVertex, goal);
        }

        public Map<S, Double> getShortestDistances() {
            return shortestDistanceToVertex;
        }
    }
}
