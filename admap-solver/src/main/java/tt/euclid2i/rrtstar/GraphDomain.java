package tt.euclid2i.rrtstar;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.ShortestPathProblem;
import tt.euclid2i.region.Rectangle;
import tt.planner.rrtstar.util.Extension;

public class GraphDomain extends StraightLineDomain {

    DirectedGraph<Point, Line> graph;

    public GraphDomain(DirectedGraph<Point, Line> graph,
            ShortestPathProblem problem, int seed, double tryGoalRatio) {
        super(problem, seed, tryGoalRatio);
        this.graph = graph;
    }

    public GraphDomain(Rectangle bounds, DirectedGraph<Point, Line> graph,
            Collection<Region> obstacles,
            Region target, Point targetPoint, int seed,
            double tryGoalRatio) {
        super(bounds, obstacles, target, targetPoint, seed, tryGoalRatio);
        this.graph = graph;
    }

    @Override
    public Extension<Point, Line> extendTo(
            Point from, Point to) {
        assert (graph.containsVertex(from));

        Set<Line> outEdges = graph.outgoingEdgesOf(from);

        Line bestEdge = null;
        double bestEdgeDistance = Double.POSITIVE_INFINITY;

        for (Line edge : outEdges) {
            if (edge.getEnd().distance(to) < bestEdgeDistance) {
                bestEdgeDistance = edge.getEnd().distance(to);
                bestEdge = edge;
            }
        }

        return new Extension<Point, Line>(from, bestEdge.getEnd(), bestEdge, graph.getEdgeWeight(bestEdge), bestEdge.getEnd().equals(to));
    }
}
