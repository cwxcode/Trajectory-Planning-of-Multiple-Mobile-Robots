package tt.euclid2i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import tt.euclid2i.Line;
import tt.euclid2i.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AdditionalPointsExtension extends AbstractDirectedGraphWrapper<Point, Line> {

    DirectedGraph<Point, Line> graph;
    Set<Point> points;
    int radius;

    HashMap<Point, Set<Line>> additionalIncomingEdges;
    HashMap<Point, Set<Line>> additionalOutgoingEdges;

    public AdditionalPointsExtension(DirectedGraph<Point, Line> graph, Set<Point> points, int radius) {
    	this(graph, points, radius, false);
    }

    public AdditionalPointsExtension(DirectedGraph<Point, Line> graph, Set<Point> points, int radius, boolean addLoop) {
        super();
        this.graph = graph;
        this.points = points;
        this.radius = radius;
        this.additionalIncomingEdges = new HashMap<Point, Set<Line>>();
        this.additionalOutgoingEdges = new HashMap<Point, Set<Line>>();
        prepareAdditionalEdges(addLoop);
    }

    private void prepareAdditionalEdges(boolean addLoop) {
        Set<Point> vertexSet = graph.vertexSet();
        for (Point additionalPoint : points) {
            Set<Line> incoming = new HashSet<Line>();
            Set<Line> outgoing = new HashSet<Line>();

            for (Point graphPoint : vertexSet) {
                if (additionalPoint.distance(graphPoint) > radius)
                    continue;

                incoming.add(new Line(graphPoint, additionalPoint));
                outgoing.add(new Line(additionalPoint, graphPoint));
            }

            if (addLoop) {
            	incoming.add(new Line(additionalPoint, additionalPoint));
            	outgoing.add(new Line(additionalPoint, additionalPoint));
            }

            additionalIncomingEdges.put(additionalPoint, incoming);
            additionalOutgoingEdges.put(additionalPoint, outgoing);
        }
    }

    @Override
    public boolean containsVertex(Point p) {
        return graph.containsVertex(p) || points.contains(p);
    }

    @Override
    public Set<Line> edgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
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
    public Set<Line> incomingEdgesOf(Point vertex) {
        if (graph.containsVertex(vertex)) {
            Set<Line> edges = new HashSet<Line>();
            edges.addAll(graph.incomingEdgesOf(vertex));

            for (Point point : points) {
                if (vertex.distance(point) <= radius)
                    edges.add(new Line(point, vertex));
            }

            return edges;

        } else if (points.contains(vertex)) {
            return additionalIncomingEdges.get(vertex);
        } else {
            throw new RuntimeException("Decorated graph and decorator itself do not contain vertex" + vertex);
        }
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        if (graph.containsVertex(vertex)) {
            Set<Line> edges = new HashSet<Line>();
            edges.addAll(graph.outgoingEdgesOf(vertex));

            for (Point point : points) {
                if (vertex.distance(point) <= radius)
                    edges.add(new Line(vertex, point));
            }

            return edges;
        } else if (points.contains(vertex)) {
            return additionalOutgoingEdges.get(vertex);

        } else {
            throw new RuntimeException("Decorated graph and decorator itself do not contain vertex" + vertex);
        }
    }

    public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        DirectedGraph<Point, Line> fullGraph
                = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build(this, fullGraph, initialPoint);
    }
}
