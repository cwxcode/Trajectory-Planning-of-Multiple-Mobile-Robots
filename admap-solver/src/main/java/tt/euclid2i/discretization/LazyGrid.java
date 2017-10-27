package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.util.NotImplementedException;

public class LazyGrid extends AbstractDirectedGraphWrapper<Point, Line> {

    public static LazyGrid zeroOriginGrid(Environment environment,int[][]pattern, int step){
        return new LazyGrid(new Point(0,0), environment, pattern, step);
    }

    public static int[][] PATTERN_4_WAY = {{0, -1},
            {-1, 0}, {1, 0},
            {0, 1},};

    public static int[][] PATTERN_4_WAY_WAIT = {{0, -1},
            {-1, 0}, {0, 0}, {1, 0},
            {0, 1},};

    public static int[][] PATTERN_8_WAY = {{-1, -1}, {0, -1}, {1, -1},
            {-1, 0}, {1, 0},
            {-1, 1}, {0, 1}, {1, 1}};

    public static int[][] PATTERN_8_WAY_WAIT = {{-1, -1}, {0, -1}, {1, -1},
            {-1, 0}, {0, 0}, {1, 0},
            {-1, 1}, {0, 1}, {1, 1}};

    public static int[][] PATTERN_16_WAY = {
            {-1, -2}, {1, -2},
            {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {2, -1},
            {-1, 0}, {1, 0},
            {-2, 1}, {-1, 1}, {0, 1}, {1, 1}, {2, 1},
            {-1, 2}, {1, 2}};

    public static int[][] PATTERN_16_WAY_WAIT = {
        {-1, -2}, {1, -2},
        {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {2, -1},
        {-1, 0}, {0, 0}, {1, 0},
        {-2, 1}, {-1, 1}, {0, 1}, {1, 1}, {2, 1},
        {-1, 2}, {1, 2}};

    private Point initialPoint;
    private Region bounds;
    private int step;
    private int[][] pattern;
    private Collection<Region> obstacles;


    public LazyGrid(Point initialPoint, Environment environment, int[][] pattern, int step) {
        this(initialPoint, environment.getObstacles(),environment.getBoundary(), pattern, step);
    }

    public LazyGrid(Point initialPoint, Collection<Region> obstacles, Region bounds, int[][] pattern, int step) {
        this.initialPoint = initialPoint;
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.step = step;

        // scale the pattern by step parameter
        this.pattern = new int[pattern.length][2];
        for (int i = 0; i < pattern.length; i++) {
            this.pattern[i][0] = pattern[i][0] * step;
            this.pattern[i][1] = pattern[i][1] * step;
        }
    }

    @Override
    public boolean containsVertex(Point p) {
        return (p.x - initialPoint.x) % step == 0 && (p.y - initialPoint.y) % step == 0 && bounds.isInside(p);
    }

    private void checkEdgeIsInGraph(Point start, Point end) {
        if (!containsVertex(start) || !containsVertex(end))
            throw new RuntimeException("At least one of the nodes is not present in the graph");
    }

    @Override
    public Set<Line> edgeSet() {
        throw new NotImplementedException();
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
        checkEdgeIsInGraph(start, end);

        Set<Line> edges = new HashSet<Line>();
        edges.add(new Line(start, end));
        edges.add(new Line(end, start));
        return edges;
    }

    @Override
    public Line getEdge(Point start, Point end) {
        checkEdgeIsInGraph(start, end);
        return new Line(start, end);
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
        Set<Line> edges = new HashSet<Line>();

        if (containsVertex(vertex)) {
            for (int[] offset : pattern) {
                Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
                if (bounds.isInside(child) && Util.isVisible(vertex, child, obstacles)) {
                    edges.add(new Line(child, vertex));
                }
            }
        }

        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();

        if (containsVertex(vertex)) {
            for (int[] offset : pattern) {
                Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
                if (bounds.isInside(child) && Util.isVisible(vertex, child, obstacles)) {
                    edges.add(new Line(vertex, child));
                }
            }
        }

        return edges;
    }

    public DirectedGraph<Point, Line> generateFullGraph() {
        return GraphBuilder.build(this, new DefaultDirectedGraph<Point, Line>(Line.class), initialPoint);
    }
}
