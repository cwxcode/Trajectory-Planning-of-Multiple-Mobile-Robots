package tt.euclid2d.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;

import tt.euclid2d.Line;
import tt.euclid2d.Point;
import tt.euclid2d.region.Rectangle;
import tt.euclid2d.region.Region;
import tt.util.NotImplementedException;

public class LazyGrid extends AbstractDirectedGraphWrapper<Point, Line> {

    private static final double SPEED = 1.0;
    private Point initialPoint;
    private Rectangle bounds;
    private double step;
    private double[][] pattern;
    private Collection<Region> obstacles;

    public LazyGrid(Point initialPoint, Collection<Region> obstacles, Rectangle bounds, double step) {
        this.initialPoint = initialPoint;
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.step = step;


        this.pattern = new double[][] {           {0,-step},
                                        {-step, 0},         { step, 0},
                                                  {0, step},          };
    }

    @Override
    public Set<Line> edgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Line> getAllEdges(Point start, Point end) {
        Set<Line> edges = new HashSet<Line>();
        edges.add(new Line(start, end, SPEED) );
        edges.add(new Line(end, start, SPEED) );
        return edges;
    }

    @Override
    public Line getEdge(Point start, Point end) {
        return new Line(start, end, SPEED);
    }

    @Override
    public EdgeFactory<Point, Line> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(Line edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(Line edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(Line edge) {
        return edge.getDistance();
    }


    @Override
    public int inDegreeOf(Point vertex) {
        return incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> incomingEdgesOf(Point vertex) {
        Set<Point> children = new HashSet<Point>();

        for (double[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && isVisible(vertex, child)) {
                children.add(child);
            }
        }

        Set<Line> edges = new HashSet<Line>();
        for (Point child : children) {
            edges.add(new Line(child, vertex, SPEED));
        }

        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        Set<Point> children = new HashSet<Point>();

        for (double[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && isVisible(vertex, child)) {
                children.add(child);
            }
        }

        Set<Line> edges = new HashSet<Line>();
        for (Point child : children) {
            edges.add(new Line(vertex, child, SPEED));
        }

        return edges;
    }

    protected boolean isVisible(Point start, Point end) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isInFreeSpace(Point point) {
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(point)) {
                return false;
            }
        }

        return true;
    }

    public DirectedGraph<Point, Line> generateFullGraph() {
        DefaultDirectedGraph<Point, Line> fullGraph
            = new DefaultDirectedGraph<Point, Line>( new EdgeFactory<Point, Line>() {

                @Override
                public Line createEdge(Point sourceVertex,
                        Point targetVertex) {
                    return new Line(sourceVertex, targetVertex, SPEED);
                }
            });


        Queue<Point> open = new LinkedList<Point>();
        open.offer(initialPoint);
        Set<Point> closed = new HashSet<Point>();
        int iterations = 0;

        while (!open.isEmpty()) {
            iterations++;
            Point current = open.poll();
            fullGraph.addVertex(current);

            Set<Line> outEdges = outgoingEdgesOf(current);
            for (Line edge : outEdges) {
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
}
