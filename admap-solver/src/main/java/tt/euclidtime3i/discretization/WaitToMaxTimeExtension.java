package tt.euclidtime3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WaitToMaxTimeExtension extends AbstractDirectedGraphWrapper<Point, Straight> {

    private DirectedGraph<Point, Straight> graph;
    private Point goal;

    private int maxTime;
    private Collection<? extends Region> dynamicObstacles;
    private tt.euclid2i.Point spatialGoal;

    public WaitToMaxTimeExtension(DirectedGraph<Point, Straight> graph, Collection<? extends Region> dynamicObstacles, tt.euclid2i.Point spatialGoal, int maxTime) {
        this.graph = graph;
        this.dynamicObstacles = dynamicObstacles;
        this.spatialGoal = spatialGoal;
        this.maxTime = maxTime;
        this.goal = new Point(spatialGoal, maxTime);
    }

    public Point getSpatialGoal() {
        return (Point) goal.clone();
    }

    @Override
    public Point getEdgeSource(Straight straight) {
        return straight.getStart();
    }

    @Override
    public Point getEdgeTarget(Straight straight) {
        return straight.getEnd();
    }

    @Override
    public double getEdgeWeight(Straight straight) {
        Point start = straight.getStart();
        Point end = straight.getEnd();

        if (isInTarget(start) && isInTarget(end) && endsInMaxTime(end))
            return 0;
        else
            return graph.getEdgeWeight(straight);
    }

    private boolean endsInMaxTime(Point end) {
        return end.getTime() == maxTime;
    }

    private boolean isInTarget(Point point) {
        tt.euclid2i.Point position = point.getPosition();
        return position.equals(spatialGoal);
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> underlyingEdges = graph.outgoingEdgesOf(vertex);

        if (!isInTarget(vertex))
            return underlyingEdges;
        else
            return addWaitToMaxTimeEdge(vertex, underlyingEdges);
    }

    private Set<Straight> addWaitToMaxTimeEdge(Point vertex, Set<Straight> underlyingEdges) {
        Straight toMaxTime = new Straight(vertex, goal);
        if (!isCollisionFree(toMaxTime))
            return underlyingEdges;

        Set<Straight> edges = new HashSet<Straight>(underlyingEdges);
        edges.add(toMaxTime);
        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Straight> edgesOf(Point vertex) {
        Set<Straight> edges = new HashSet<Straight>();
        edges.addAll(outgoingEdgesOf(vertex));
        edges.addAll(incomingEdgesOf(vertex));
        return edges;
    }

    private boolean isCollisionFree(Straight straight) {
        for (Region obstacle : dynamicObstacles) {
            Point start = straight.getStart();
            Point end = straight.getEnd();
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }
}
