package tt.euclidtime3i.sipp;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipp.intervals.Interval;
import tt.euclidtime3i.sipp.intervals.SafeIntervalBuilder;
import tt.euclidtime3i.sipp.intervals.SafeIntervalList;
import tt.euclidtime3i.sipprrts.DynamicObstacles;
import tt.util.Verbose;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SippWrapper extends AbstractDirectedGraphWrapper<SippNode, SippEdge> {

    private DirectedGraph<Point, Line> graph;
    private DynamicObstacles env;

    private SafeIntervalBuilder<Point> pointSIBuilder;
    private SafeIntervalBuilder<Line> edgeSIBuilder;

    private int[] separations;
    private int bodyRadius;
    private float speed;
    private int step;

    public SippWrapper(DirectedGraph<Point, Line> graph, DynamicObstacles environment, int bodyRadius, float speed, int samplingStep, int maxTime) {
        this.graph = graph;
        this.env = environment;
        this.bodyRadius = bodyRadius;
        this.speed = speed;
        this.step = samplingStep;
        this.separations = SippUtils.radiusesToSeparations(bodyRadius, env.getObstacleRadiuses());
        initIntervals(maxTime);
    }

    protected void initIntervals(int maxTime) {
        pointSIBuilder = SafeIntervalBuilder.safeIntervalsOfVertices(graph.vertexSet(), env, bodyRadius, step, maxTime);
        edgeSIBuilder = SafeIntervalBuilder.safeIntervalsOfEdges(graph.edgeSet(), env, bodyRadius, step, maxTime);
    }

    public SippNode wrapNode(Point point, int time) {
        SafeIntervalList intervals = pointSIBuilder.getSafeIntervals(point);

        if (intervals == null)
            throw new IllegalArgumentException("No such point in the graph " + point);

        for (Interval interval : intervals) {
            if (interval.contains(time))
                return new SippNode(point, interval, time);
        }


        throw new IllegalArgumentException("Specified time is not in any of the safe intervals of given point " + time);
    }

    @Override
    public SippNode getEdgeSource(SippEdge edge) {
        return edge.getSource();
    }

    @Override
    public SippNode getEdgeTarget(SippEdge edge) {
        return edge.getTarget();
    }

    @Override
    public double getEdgeWeight(SippEdge edge) {
        return edge.weight();
    }

    @Override
    public Set<SippEdge> outgoingEdgesOf(SippNode node) {
        Verbose.println("Expanding " + node);
        Set<SippEdge> outgoingEdges = new HashSet<SippEdge>();

        Set<Line> edgeSet = graph.outgoingEdgesOf(node.getPoint());
        for (Line e : edgeSet) {
            Verbose.println(" Trying edge: " + e);
            outgoingEdges.addAll(expandEdge(node, e));
        }

        return outgoingEdges;
    }

    private List<SippEdge> expandEdge(SippNode node, Line edge) {
        List<SippEdge> edges = new ArrayList<SippEdge>();

        SafeIntervalList childIntervals = pointSIBuilder.getSafeIntervals(edge.getEnd());

        for (Interval childSI : childIntervals) {
            Verbose.println("  Safe interval (child): " + childSI);
            SippEdge sippEdge = planTraversalTroughEdge(node, edge, childSI);
            Verbose.println("  Found sippEdge: " + sippEdge);
            if (sippEdge != null && !collideWithAnyDynamicObstacle(sippEdge))
                edges.add(sippEdge);
        }

        return edges;
    }

    private SippEdge planTraversalTroughEdge(SippNode node, Line edge, Interval childSI) {
        SafeIntervalList edgeSIs = edgeSIBuilder.getSafeIntervals(edge);
        Interval nodeSI = node.getSafeInterval();
        int duration = (int) Math.round(edge.getDistance() / speed);

        for (Interval edgeSI : edgeSIs) {
            Verbose.println("   Trying edgeSI: " + edgeSI);
            Interval interval = SippUtils.safeIntervalToTraverse(nodeSI, edgeSI, childSI, node.getTime(), duration);
            if (interval != null) {
                List<Straight> straights = SippUtils.traverseInGivenInterval(edge, node.getTime(), interval);

                SippNode childNode = new SippNode(edge.getEnd(), childSI, interval.end);
                return new SippEdge(node, childNode, straights);
            }
        }

        return null;
    }

    private boolean collideWithAnyDynamicObstacle(SippEdge sippEdge) {
        boolean collide = SeparationDetector.hasAnyPairwiseConflict(sippEdge.trajectory(), env.getObstacles(), separations, step);
        Verbose.println("   Collide: " + collide);
        return collide;
    }

    public int getNoOfSafeIntervals(Point point) {
    	return pointSIBuilder.getSafeIntervals(point).size();
    }

}
