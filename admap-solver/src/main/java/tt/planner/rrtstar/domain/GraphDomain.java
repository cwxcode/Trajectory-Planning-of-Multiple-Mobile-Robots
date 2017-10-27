package tt.planner.rrtstar.domain;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.GreedyBestFirstSearch;
import org.jgrapht.util.GeneralHeuristic;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.ExtensionEstimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphDomain<S, E> implements Domain<S, GraphPathEdge<S, E>> {

    protected Graph<S, E> graph;
    protected S goal;
    protected GeneralHeuristic<S> heuristic;
    protected Random random;
    protected double costLimit;
    protected double tryGoalRatio;

    protected List<S> vertexSet;
    protected int vertexCount;

    public GraphDomain(Graph<S, E> graph, GeneralHeuristic<S> heuristic, S goal, int seed, double costLimit, double tryGoalRatio) {
        this.graph = graph;
        this.heuristic = heuristic;
        this.goal = goal;
        this.costLimit = costLimit;
        this.tryGoalRatio = tryGoalRatio;

        this.random = new Random(seed);
        this.vertexSet = new ArrayList<S>(graph.vertexSet());
        this.vertexCount = vertexSet.size();
    }

    @Override
    public S sampleState() {
        if (random.nextDouble() > tryGoalRatio) {
            int rnd = random.nextInt(vertexCount);
            return vertexSet.get(rnd);
        } else {
            return goal;
        }

    }

    public void setCostLimit(double costLimit) {
        this.costLimit = costLimit;
    }

    @Override
    public Extension<S, GraphPathEdge<S, E>> extendTo(S from, final S to) {

        GreedyBestFirstSearch<S, E> algorithm = new GreedyBestFirstSearch<S, E>(graph, new HeuristicToGoal<S>() {
            @Override
            public double getCostToGoalEstimate(S current) {
                return heuristic.getCostEstimate(current, to);
            }

        }, from, new Goal<S>() {
            @Override
            public boolean isGoal(S current) {
                return current.equals(to);
            }
        }, costLimit
        );

        GraphPath<S, E> path = algorithm.findPath(Integer.MAX_VALUE);

        if (path == null) {
            path = algorithm.getTraversedPath();
        }

        return new Extension<S, GraphPathEdge<S, E>>(path.getStartVertex(), path.getEndVertex(), new GraphPathEdge<S, E>(path), path.getWeight(), true);
    }

    @Override
    public ExtensionEstimate estimateExtension(S from, S to) {
        return new ExtensionEstimate(heuristic.getCostEstimate(from, to), false);
    }

    @Override
    public double estimateCostToGo(S s) {
        return heuristic.getCostToGoalEstimate(s);
    }

    @Override
    public double distance(S s1, S s2) {
        return heuristic.getCostEstimate(s1, s2);
    }

    @Override
    public double nDimensions() {
        return 2;
    }

    @Override
    public boolean isInTargetRegion(S s) {
        return goal.equals(s);
    }
}
