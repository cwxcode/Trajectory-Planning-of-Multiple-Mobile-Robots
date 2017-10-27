package tt.euclid2i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.util.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

public class ToGoalEdgeExtension extends AbstractDirectedGraphWrapper<Point, Line> {

    DirectedGraph<Point, Line> graph;
    Point goalPoint;
    int radius;

    public ToGoalEdgeExtension(DirectedGraph<Point, Line> graph,
                               Point goalPoint, int radius) {
        super();
        this.graph = graph;
        this.goalPoint = goalPoint;
        this.radius = radius;
    }

    @Override
    public boolean containsVertex(Point p) {
        return graph.containsVertex(p) || goalPoint.equals(p);
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
        Set<Line> edges = new HashSet<Line>();
        edges.add(new Line(start, end));
        edges.add(new Line(end, start));
        return edges;
    }

    @Override
    public Line getEdge(Point start, Point end) {
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
        if (vertex.equals(goalPoint)) {
            throw new NotImplementedException();
        } else {
            return graph.incomingEdgesOf(vertex);
        }
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        if (!vertex.equals(goalPoint)) {
            if (vertex.distance(goalPoint) <= radius) {
                Set<Line> edges = new HashSet<Line>();
                edges.addAll(graph.outgoingEdgesOf(vertex));
                edges.add(new Line(vertex, goalPoint));
                return edges;
            } else {
                return graph.outgoingEdgesOf(vertex);
            }
        } else {
            if (graph.containsVertex(vertex)) {
                return graph.outgoingEdgesOf(vertex);
            } else {
                return new HashSet<Line>();
            }
        }
    }

    @Override
	public Set<Point> vertexSet() {
		Set<Point> vertexSet = new HashSet<Point>(graph.vertexSet());
		vertexSet.add(goalPoint);
		return vertexSet;
	}



	public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        //FIXME if the underlying graph is lazygrid, it may happen that this method causes infinite loop
        DirectedGraph<Point, Line> fullGraph
                = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build(this, fullGraph, initialPoint);
    }
}
