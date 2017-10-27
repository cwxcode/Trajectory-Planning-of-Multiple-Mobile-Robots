package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.teneighty.heap.Heap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ARAStarShortestPath<V, E> extends PlanningAlgorithm<V, E> {

    protected HeuristicToGoal<V> heuristicToGoal;
    protected Result<V, E> result;
    protected double suboptimalityScale;
    protected double suboptimalityDecreaseStep;
    //
    protected Set<V> closed;
    protected Set<V> inconsistent;
    protected Heap<Double, V> heap;
    protected V current;
    private int iterationCounter;

    public ARAStarShortestPath(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex,
                               final V endVertex, double suboptimalityScale, double suboptimalityDecreaseStep,
                               Heap<Double, V> heap) {

        this(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return endVertex.equals(current);
            }
        }, suboptimalityScale, suboptimalityDecreaseStep, heap);
    }

    public ARAStarShortestPath(Graph<V, E> graph, HeuristicToGoal<V> heuristic,
                               V startVertex, Goal<V> goal, double suboptimalityScale,
                               double suboptimalityDecreaseStep, Heap<Double, V> heap) {
        super(graph, startVertex, goal);

        this.heuristicToGoal = heuristic;
        this.suboptimalityScale = suboptimalityScale;
        this.suboptimalityDecreaseStep = suboptimalityDecreaseStep;
        this.heap = heap;
        initialize();
    }

    private void initialize() {
        closed = new HashSet<V>();
        inconsistent = new HashSet<V>();

        setShortestDistanceTo(startVertex, 0.);
        heap.insert(calculateKey(startVertex), startVertex);
    }

    public void setSuboptimalityDecreseStep(double step) {
        this.suboptimalityDecreaseStep = step;
    }

    @Override
    public GraphPath<V, E> findPath(int iterationLimit) {
        return iterate(iterationLimit).path;
    }

    public Result<V, E> iterate(int iterationLimit) {
        prepareOpenQueue();
        clearCloseAndInconsistentSet();
        improvePath(iterationLimit);
        decreaseEpsilon();
        return result;
    }

    protected void prepareOpenQueue() {
        updateKeysOfEncounteredVertices();
        moveInconsistentVerticesIntoQueue();
    }

    protected void updateKeysOfEncounteredVertices() {
        Collection<Heap.Entry<Double, V>> keys = new ArrayList<Heap.Entry<Double, V>>(heap.getEntries());
        for (Heap.Entry<Double, V> entry : keys) {
            V vertex = entry.getValue();
            heap.decreaseKey(entry, calculateKey(vertex));
        }
    }

    protected void moveInconsistentVerticesIntoQueue() {
        for (V vertex : inconsistent) {
            double key = calculateKey(vertex);
            heap.insert(key, vertex);
        }
    }

    protected void clearCloseAndInconsistentSet() {
        closed.clear();
        inconsistent.clear();
    }

    protected double calculateKey(V vertex) {
        return getShortestDistanceTo(vertex)
                + suboptimalityScale * heuristicToGoal.getCostToGoalEstimate(vertex);
    }

    protected void decreaseEpsilon() {
        suboptimalityScale -= suboptimalityDecreaseStep;
        if (suboptimalityScale < 1) {
            suboptimalityScale = 1;
        }
    }

    protected void improvePath(int iterationLimit) {
        V foundGoal = null;

        while (!heap.isEmpty() && iterationCounter++ < iterationLimit) {

            V vertex = heap.extractMinimum().getValue();

            notifyExpansionListeners(vertex);

            if (goal.isGoal(vertex)) {
                inconsistent.add(vertex); //goal state should remain in the queue
                foundGoal = vertex;
                break;
            }

            closed.add(vertex);
            double vertexDistance = getShortestDistanceTo(vertex);

            Set<E> edges = specifics.outgoingEdgesOf(vertex);
            for (E edge : edges) {
                V child = Graphs.getOppositeVertex(graph, edge, vertex);

                double edgeCost = graph.getEdgeWeight(edge);
                double childDistance = getShortestDistanceTo(child);
                double candidateDistance = vertexDistance + edgeCost;

                if (childDistance > candidateDistance) {
                    encounterVertex(child, edge, candidateDistance);
                }
            }
        }

        GraphPath<V, E> path;
        if (foundGoal != null) {
            path = reconstructPath(startVertex, foundGoal);
        } else {
            path = null;
        }

        result = new Result<V, E>(path, suboptimalityScale);
    }

    protected void encounterVertex(V vertex, E edge, double distanceToVertex) {
        setShortestDistanceTo(vertex, distanceToVertex);
        setShortestPathTreeEdge(vertex, edge);

        if (closed.contains(vertex)) {
            inconsistent.add(vertex);
        } else {
            heap.insert(calculateKey(vertex), vertex);
        }
    }

    @Override
    public GraphPath<V, E> getTraversedPath() {
        return (result == null) ? null : result.path;
    }

    public Collection<V> getOpenedNodes() {
        return heap.getValues();
    }

    public Collection<V> getClosedNodes() {
        return closed;
    }

    @Override
    public V getCurrentVertex() {
        return current;
    }

    @Override
    public int getIterationCount() {
        return iterationCounter;
    }

    public Result<V, E> getResult() {
        return result;
    }

    public static class Result<V, E> {

        public GraphPath<V, E> path;
        public double suboptimalityScale;

        public Result(GraphPath<V, E> path, double suboptimalityScale) {
            this.path = path;
            this.suboptimalityScale = suboptimalityScale;
        }

        @Override
        public String toString() {
            return String.format("Result{weight=%f, edges=%d, heuristicScale=%f}", path.getWeight(),
                    path.getEdgeList().size(), suboptimalityScale);
        }
    }
}
