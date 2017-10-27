package tt.euclidtime3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import tt.euclid2i.Line;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;

import java.util.*;

public class ConstantSpeedTimeExtension extends AbstractDirectedGraphWrapper<Point, Straight> {

    private DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph;
    private int maxTime;
    private float[] speeds;
    private Collection<? extends Region> dynamicObstacles;

    public final static int DISABLE_WAIT_MOVE = 0;    
    
    /** All edges will end in a time that is a multiple of timeStep. Can be disabled by setting to timeStep=1. **/ 
    
    private int timeStep; 
    private int waitMoveDuration;


    private static float[] toFloatArray(int[] intArr) {
		float[] floatArr = new float[intArr.length];
		for (int i = 0; i < floatArr.length; i++) {
			floatArr[i] = (float) intArr[i];
		}
    	return floatArr;
	}

	public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            float[] speeds, Collection<? extends Region> dynamicObstacles, int waitMoveDuration, int timeStep) {
        super();
        this.spatialGraph = spatialGraph;
        this.maxTime = maxTime;
        this.speeds = speeds;
        this.dynamicObstacles = dynamicObstacles;
        this.waitMoveDuration = waitMoveDuration;
        this.timeStep = timeStep;
    }
	
    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds, Collection<? extends Region> dynamicObstacles, int waitMoveDuration) {
    	this(spatialGraph, maxTime, toFloatArray(speeds), dynamicObstacles, waitMoveDuration, 1);
    }
    
    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            float[] speeds, Collection<? extends Region> dynamicObstacles, int waitMoveDuration) {
    	this(spatialGraph, maxTime, speeds, dynamicObstacles, waitMoveDuration, 1);
    }

    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            float[] speeds, int waitMoveDuration) {
        this(spatialGraph, maxTime, speeds, new LinkedList<Region>(), waitMoveDuration);
    }

    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            float[] speeds) {
        this(spatialGraph, maxTime, speeds, new LinkedList<Region>(), DISABLE_WAIT_MOVE);
    }
    
    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds) {
        this(spatialGraph, maxTime, toFloatArray(speeds), new LinkedList<Region>(), DISABLE_WAIT_MOVE);
    }
    
    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds, Collection<? extends Region> dynamicObstacles, int waitMoveDuration, int timeStep) {
    	this(spatialGraph, maxTime, toFloatArray(speeds),dynamicObstacles, waitMoveDuration, timeStep);    	
    }
    
    

    @Override
    public boolean containsVertex(Point p) {
        return spatialGraph.containsVertex(p.getPosition()) &&
                p.getTime() <= maxTime;
    }

    @Override
    public Set<Straight> edgesOf(Point vertex) {
        Set<Straight> edges = new LinkedHashSet<Straight>();
        //edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Straight> getAllEdges(Point start, Point end) {
        Set<Straight> edges = new LinkedHashSet<Straight>();
        edges.add(new Straight(start, end));
        edges.add(new Straight(end, start));
        return edges;
    }

    @Override
    public Straight getEdge(Point start, Point end) {
        return new Straight(start, end);
    }

    @Override
    public EdgeFactory<Point, Straight> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(Straight edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(Straight edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(Straight edge) {
        return edge.getEnd().getTime() - edge.getStart().getTime();
    }


    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        if (vertex.getTime() < maxTime) {
            Set<Point> children = new HashSet<Point>();

            Set<Line> spatialEdges = spatialGraph.outgoingEdgesOf(new tt.euclid2i.Point(vertex.x, vertex.y));
            for (Line spatialEdge : spatialEdges) {
                for (float speed : speeds) {
                	Point child = new Point(spatialEdge.getEnd().x, spatialEdge.getEnd().y, vertex.getTime() + (int) Math.round(spatialEdge.getDistance() / speed));
                    
                	if (timeStep != 1) {
                		int regularizedTime = (int) Math.ceil(child.getTime() / (float) timeStep) * timeStep;
                		child = new Point(child.getPosition(), regularizedTime);
                	}
                	
                	if (child.getTime() <= maxTime && isVisible(vertex, child, dynamicObstacles)) {
                        children.add(child);
                    }
                }
            }

            Set<Straight> edges = new HashSet<Straight>();

            if (waitMoveDuration != DISABLE_WAIT_MOVE) {
                int endTime = vertex.getTime() + waitMoveDuration;
                
            	if (timeStep != 1) {
            		endTime = (int) Math.ceil(endTime / (float) timeStep) * timeStep;
            	}

                if (endTime > maxTime) {
                    endTime = maxTime;
                }

                Point endPoint = new tt.euclidtime3i.Point(vertex.x, vertex.y, endTime);
                if (isVisible(vertex, endPoint, dynamicObstacles)) {
                    edges.add(new Straight(vertex, endPoint));
                }
            }

            for (Point child : children) {
                edges.add(new Straight(vertex, child));
            }

            return edges;
        } else {
            return new HashSet<Straight>();
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
