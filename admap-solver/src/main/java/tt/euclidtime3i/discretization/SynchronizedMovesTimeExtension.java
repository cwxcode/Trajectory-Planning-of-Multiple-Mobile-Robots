package tt.euclidtime3i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import tt.euclid2i.Line;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.util.NotImplementedException;

public class SynchronizedMovesTimeExtension extends AbstractDirectedGraphWrapper<Point, Straight> {

    private DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph;
    private int maxTime;
    private int timeStepDuration;
    private Collection<Region> dynamicObstacles;

    public SynchronizedMovesTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int timeStepDuration, Collection<Region> dynamicObstacles) {
        super();
        this.spatialGraph = spatialGraph;
        this.maxTime = maxTime;
        this.timeStepDuration = timeStepDuration;
        this.dynamicObstacles = dynamicObstacles;
    }

    @Override
    public boolean containsVertex(Point p) {
        return spatialGraph.containsVertex(p.getPosition()) && p.getTime() <= maxTime;
    }

    @Override
    public Set<Straight> edgeSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<Straight> edgesOf(Point vertex) {
        Set<Straight> edges = new HashSet<Straight>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Straight> getAllEdges(Point start, Point end) {
        Set<Straight> edges = new HashSet<Straight>();
        edges.add(new Straight(start, end) );
        edges.add(new Straight(end, start) );
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
        Set<Point> children = new HashSet<Point>();

        Set<Line> spatialEdges = spatialGraph.outgoingEdgesOf(new tt.euclid2i.Point(vertex.x, vertex.y));
        for (Line spatialEdge : spatialEdges) {
                Point child = new Point(spatialEdge.getEnd().x, spatialEdge.getEnd().y, vertex.getTime() + timeStepDuration);
                if (child.getTime() < maxTime && isVisible(vertex, child, dynamicObstacles)) {
                    children.add(child);
                }
        }

        Set<Straight> edges = new HashSet<Straight>();
        for (Point child : children) {
            edges.add(new Straight(vertex, child));
        }

        return edges;
    }

    private boolean isVisible(Point start, Point end, Collection<Region> obstacles) {
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    /*
    public DirectedGraph<Point, Straight> generateFullGraph() {
        DefaultDirectedGraph<Point, Straight> fullGraph
            = new DefaultDirectedGraph<Point, Straight>( new EdgeFactory<Point, Straight>() {

                @Override
                public Straight createEdge(Point sourceVertex,
                        Point targetVertex) {
                    return new Straight(sourceVertex, targetVertex);
                }
            });

        Queue<Point> open = new LinkedList<Point>();

        open.offer(spatialGraph.getInitialPoint());
        Set<Point> closed = new HashSet<Point>();
        int iterations = 0;

        while (!open.isEmpty()) {
            iterations++;
            Point current = open.poll();
            fullGraph.addVertex(current);

            Set<Straight> outEdges = outgoingEdgesOf(current);
            for (Straight edge : outEdges) {
                Point target = getEdgeTarget(edge);
                fullGraph.addVertex(target);
                fullGraph.addEdge(current, target);

                if (!closed.contains(target)) { // it must mean that this closed.contains(target) is always true
                    closed.add(target);
                    open.offer(target);
                }
            }
        }

        System.out.println("iterations:" + iterations);
        System.out.println("closed:" + closed.size());
        return fullGraph;
    }
    */
}
