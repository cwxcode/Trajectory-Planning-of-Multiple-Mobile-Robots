package tt.euclidtime3i.gsipp;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;

import tt.euclid2i.Line;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.WaitStraight;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.sipp.SippUtils;
import tt.euclidtime3i.sipp.intervals.SafeIntervalBuilder;
import tt.euclidtime3i.sipprrts.DynamicObstaclesImpl;
import tt.util.NotImplementedException;

public class GSippTimeExtension extends AbstractDirectedGraphWrapper<Point, WaitStraight> {

    private DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph;
    private int maxTime;
    private int[] speeds;
    private Collection<? extends Region> dynamicObstacles;

    public final static int DISABLE_WAIT_MOVE = 0;
    private int waitMoveDuration;

    public GSippTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds, Collection<? extends Region> dynamicObstacles, int waitMoveDuration) {
        super();
        this.spatialGraph = spatialGraph;
        this.maxTime = maxTime;
        this.speeds = speeds;
        this.dynamicObstacles = dynamicObstacles;
        this.waitMoveDuration = waitMoveDuration;
    }

    @Override
    public boolean containsVertex(Point p) {
        return spatialGraph.containsVertex(p.getPosition()) &&
                p.getTime() <= maxTime;
    }

    @Override
    public Set<WaitStraight> edgesOf(Point vertex) {
        Set<WaitStraight> edges = new LinkedHashSet<WaitStraight>();
        //edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<WaitStraight> getAllEdges(Point start, Point end) {
    	throw new NotImplementedException();
    }

    @Override
    public WaitStraight getEdge(Point start, Point end) {
        throw new NotImplementedException();
    }

    @Override
    public EdgeFactory<Point, WaitStraight> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(WaitStraight edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(WaitStraight edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(WaitStraight edge) {
        return edge.getEnd().getTime() - edge.getStart().getTime();
    }


    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<WaitStraight> outgoingEdgesOf(Point vertex) {
//        if (vertex.getTime() < maxTime) {
//            Set<Point> children = new HashSet<Point>();
//
//            Set<Line> spatialEdges = spatialGraph.outgoingEdgesOf(new tt.euclid2i.Point(vertex.x, vertex.y));
//            for (Line spatialEdge : spatialEdges) {
//            	tt.euclid2i.Point spatialNeighbor = spatialEdge.getEnd();
//
//            	getSafeIntervals();
//
//
//
//                for (int speed : speeds) {
//                	Point child = new Point(spatialEdge.getEnd().x, spatialEdge.getEnd().y, vertex.getTime() + (int) Math.round(spatialEdge.getDistance() / speed));
//                    if (child.getTime() <= maxTime && isVisible(vertex, child, dynamicObstacles)) {
//                        children.add(child);
//                    }
//                }
//            }
//
//            Set<Straight> edges = new HashSet<Straight>();
//
//            if (waitMoveDuration != DISABLE_WAIT_MOVE) {
//                int endTime = vertex.getTime() + waitMoveDuration;
//
//                if (endTime > maxTime) {
//                    endTime = maxTime;
//                }
//
//                Point endPoint = new tt.euclidtime3i.Point(vertex.x, vertex.y, endTime);
//                if (isVisible(vertex, endPoint, dynamicObstacles)) {
//                    edges.add(new Straight(vertex, endPoint));
//                }
//            }
//
//            for (Point child : children) {
//                edges.add(new Straight(vertex, child));
//            }
//
//            return edges;
//        } else {
//            return new HashSet<Straight>();
//        }
    	return new HashSet<WaitStraight>();
    }

    private void getSafeIntervals(tt.euclid2i.Point point) {

    	SegmentedTrajectory[] trajs = new SegmentedTrajectory[dynamicObstacles.size()];
    	int[] radiuses = new int[dynamicObstacles.size()];
    	int i=0;
    	for (Region dynObst : dynamicObstacles) {
    		trajs[i] = (SegmentedTrajectory) ((MovingCircle)dynObst).getTrajectory();
    		radiuses[i] = ((MovingCircle)dynObst).getRadius();
    		i++;
    	}
	}

	private boolean isVisible(Point start, Point end, Collection<? extends Region> obstacles) {
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }
}

