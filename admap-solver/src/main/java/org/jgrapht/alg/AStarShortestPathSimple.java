package org.jgrapht.alg;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.teneighty.heap.FibonacciHeap;
import org.teneighty.heap.Heap;

import tt.util.Counters;

public class AStarShortestPathSimple<V, E> extends PlanningAlgorithm<V, E> {

    public static interface TerminatingCondition<V> {
        boolean proceed(V value, double heapMinimum);
    }

    public static interface StateDominance<V> {
    	boolean isDominated(V state, double stateCost);
    	void addedState(V state, double cost);
    }

    protected Heap<Double, V> heap;
    protected HeuristicToGoal<V> heuristicToGoal;
    protected StateDominance<V> stateDominance;
    protected Map<V, Heap.Entry<Double, V>> opened;
    protected Set<V> closed;
    protected int iterationCounter;
    protected GraphPath<V, E> path;
    protected V current;

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, V endVertex) {
        return findPathBetween(graph, heuristic, startVertex, endVertex, Integer.MAX_VALUE);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal) {
        return findPathBetween(graph, heuristic, startVertex, goal, Integer.MAX_VALUE);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, V endVertex, int iterationLimit) {
        AStarShortestPathSimple<V, E> alg = new AStarShortestPathSimple<V, E>(graph, heuristic, startVertex, endVertex);
        return alg.findPath(iterationLimit);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, int iterationLimit) {
        AStarShortestPathSimple<V, E> alg = new AStarShortestPathSimple<V, E>(graph, heuristic, startVertex, goal);
        return alg.findPath(iterationLimit);
    }
    
    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, int iterationLimit, int runtimeLimitMs) {
        AStarShortestPathSimple<V, E> alg = new AStarShortestPathSimple<V, E>(graph, heuristic, startVertex, goal);
        return alg.findPathRuntimeLimit(iterationLimit, runtimeLimitMs);
    }

    public AStarShortestPathSimple(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex) {
        this(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return endVertex.equals(current);
            }
        });
    }

    public AStarShortestPathSimple(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal) {
    	this(graph, heuristic, new StateDominance<V>() {

			@Override
			public boolean isDominated(V state, double stateCost) {
				return false;
			}

			@Override
			public void addedState(V state, double cost) {}

		}, startVertex, goal);
    }

    public AStarShortestPathSimple(Graph<V, E> graph, HeuristicToGoal<V> heuristic, StateDominance<V> stateDominance, V startVertex, Goal<V> goal) {
        super(graph, startVertex, goal);

        this.heuristicToGoal = heuristic;
        this.stateDominance = stateDominance;
        this.heap = new FibonacciHeap<Double, V>();
        this.opened = new HashMap<V, Heap.Entry<Double, V>>();
        this.closed = new HashSet<V>();
        initialize();
    }

    private void initialize() {
        setShortestDistanceTo(startVertex, 0.);
        Heap.Entry<Double, V> entry = heap.insert(calculateKey(startVertex), startVertex);
        opened.put(startVertex, entry);
    }

    public GraphPath<V, E> findPath(final int iterationLimit) {
        return findPath(new TerminatingCondition<V>() {
            private int iteration = 0;

            @Override
            public boolean proceed(V value, double heapMinimum) {
                return iteration++ < iterationLimit;
            }
        });
    }

    public GraphPath<V, E> findPathRuntimeLimit(int iterationLimit, int runtimeLimitMs) {
        long deadline = System.currentTimeMillis() + runtimeLimitMs;
        return findPathDeadlineLimit(iterationLimit, deadline);
    }

    public GraphPath<V, E> findPathDeadlineLimit(final int iterationLimit, final long deadlineLimit) {
        return findPath(new TerminatingCondition<V>() {

            private long startTime = 0;
            private int iteration = 0;

            @Override
            public boolean proceed(V value, double heapMinimum) {
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                }
                return System.currentTimeMillis() < deadlineLimit
                        && iteration++ < iterationLimit;
            }
        });
    }

    public GraphPath<V, E> findPathCostAndDeadlineLimit(final double costLimit, final long deadlineLimitMs) {
        return findPath(new TerminatingCondition<V>() {

            private long startTime = 0;

            @Override
            public boolean proceed(V value, double heapMinimum) {
                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                }
                return System.currentTimeMillis() < deadlineLimitMs
                        && heapMinimum < costLimit;
            }
        });
    }


    public GraphPath<V, E> findPath(TerminatingCondition condition) {

        while (!heap.isEmpty() && checkProceedCondition(condition)) {
            Counters.expandedStatesCounter++;
            iterationCounter++;
            current = heap.extractMinimum().getValue();

            opened.remove(current);
            closed.add(current);

            notifyExpansionListeners(current);

            if (goal.isGoal(current)) {
                path = reconstructPath(startVertex, current);
                break;
            }

            double vertexDistance = getShortestDistanceTo(current);

            Set<E> edges = specifics.outgoingEdgesOf(current);
            for (E edge : edges) {
                V child = Graphs.getOppositeVertex(graph, edge, current);

                if (closed.contains(child)) {
                    continue;
                    //TODO - does not handle non-admissible heuristic
                }

                double edgeCost = graph.getEdgeWeight(edge);
                double childDistance = getShortestDistanceTo(child);
                double candidateDistance = vertexDistance + edgeCost;
                double estOverallDistance = candidateDistance + heuristicToGoal.getCostToGoalEstimate(child);

                Heap.Entry<Double, V> entry = opened.get(child);
                if (entry == null) {
                	if (!stateDominance.isDominated(child, candidateDistance)) {
	                    entry = heap.insert(estOverallDistance, child);
	                    opened.put(child, entry);
	                    stateDominance.addedState(child, candidateDistance);

	                    setShortestDistanceTo(child, candidateDistance);
	                    setShortestPathTreeEdge(child, edge);
                	}
                } else if (candidateDistance < childDistance) {
                    heap.decreaseKey(entry, estOverallDistance);
                    stateDominance.addedState(child, candidateDistance);

                    setShortestDistanceTo(child, candidateDistance);
                    setShortestPathTreeEdge(child, edge);
                }
            }
        }

        return path;
    }

    private boolean checkProceedCondition(TerminatingCondition condition) {
        Heap.Entry<Double, V> minimum = heap.getMinimum();
        return condition.proceed(minimum.getValue(), minimum.getKey());
    }

    private double calculateKey(V vertex) {
        return getShortestDistanceTo(vertex) + heuristicToGoal.getCostToGoalEstimate(vertex);
    }

    public GraphPath<V, E> getTraversedPath() {
        return path;
    }

    public Collection<V> getOpenedNodes() {
        return opened.keySet();
    }

    public Collection<V> getClosedNodes() {
        return closed;
    }

    public V getCurrentVertex() {
        return current;
    }

    public int getIterationCount() {
        return iterationCounter;
    }
}
