package tt.euclid2i.discretization;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.util.Util;
import tt.util.NotImplementedException;

public class VisibilityGraph {

    public static WeightedGraph<Point, Line> createVisibilityGraph(Collection<Region> obstacles, int agentRadius, Collection<Point> additionalSignificantPoints) {

        Collection<Region> inflatedObstaclesForCollisionChecking = Util.inflateRegions(obstacles, agentRadius);
        Collection<Region> inflatedObstaclesForGraph = Util.inflateRegions(obstacles, agentRadius+1);

        return createVisibilityGraph(inflatedObstaclesForCollisionChecking, inflatedObstaclesForGraph, additionalSignificantPoints);
    }

    public static WeightedGraph<Point, Line> createVisibilityGraph(Collection<Region> inflatedObstaclesForCollisionChecking,
            Collection<Region> inflatedObstaclesForGraph, Collection<Point> additionalSignificantPoints) {

        @SuppressWarnings("serial")
        WeightedGraph<Point, Line> visibilityGraph = new SimpleWeightedGraph<Point, Line>(
        new EdgeFactory<Point, Line>() {

            @Override
            public Line createEdge(Point from, Point to) {
                return new Line(from, to);
            }

        }) {

            @Override
            public double getEdgeWeight(Line line) {
                return line.getDistance();
            }

            @Override
            @Deprecated
            public void setEdgeWeight(Line arg0, double arg1) {
                throw new NotImplementedException();
            }
        };

        Collection<Point> significantPoints = new LinkedList<Point>();
        for (Region inflatedObstacle : inflatedObstaclesForGraph) {
            Polygon polygon = (Polygon) inflatedObstacle;
            significantPoints.addAll(Arrays.asList(polygon.getPoints()));
        }

        significantPoints.addAll(additionalSignificantPoints);

        // add points
        for (Point signPoint : significantPoints) {
             if (!conflicting(inflatedObstaclesForCollisionChecking, signPoint)) {
                 visibilityGraph.addVertex(signPoint);
             }
        }

        Point[] vertices = visibilityGraph.vertexSet().toArray(new Point[0]);

        for (int i=0; i < vertices.length; i++) {
            for (int j=i+1; j < vertices.length; j++) {
                if (!conflicting(inflatedObstaclesForCollisionChecking, vertices[i], vertices[j])) {

                     if (!vertices[i].equals(vertices[j]) && visibilityGraph.containsVertex(vertices[i])
                                && visibilityGraph.containsVertex(vertices[j])) {

                            visibilityGraph.addEdge(vertices[i], vertices[j]);
                        }
                }
            }
        }
        return visibilityGraph;
    }

    private static boolean conflicting(Collection<Region> inflatedObstacles, Point p1m, Point p2m) {

        for (Region region : inflatedObstacles) {
            if (region.intersectsLine(p1m, p2m)) {
                return true;
            }
        }
        return false;
    }

    private static boolean conflicting(Collection<Region> inflatedObstacles, Point p1) {
        for (Region region : inflatedObstacles) {
            if (region.isInside(p1)) {
                return true;
            }
        }
        return false;
    }

    // These method generate visibility graph by traversing the graph from the start point.
    // It doesnt require region.isInside() function, as it only works with borders of regions.

    public static DirectedGraph<Point, Line> createVisibilityGraph(Point start, Point goal, Collection<Region> obstacles,
            int agentRadius) {

        Collection<Region> inflatedObstaclesForCollisionChecking = Util.inflateRegions(obstacles, agentRadius);
        Collection<Region> inflatedObstaclesForGraph = Util.inflateRegions(obstacles, agentRadius+1);

        return createVisibilityGraph(start, goal, inflatedObstaclesForCollisionChecking, inflatedObstaclesForGraph);
    }

    public static DirectedGraph<Point, Line> createVisibilityGraph(Point start, Point goal,
            Collection<Region> inflatedObstaclesForCollisionChecking,
            Collection<Region> inflatedObstaclesForGraph) {

        @SuppressWarnings("serial")
        DirectedGraph<Point, Line> visibilityGraph = new DirectedMultigraph<Point, Line>(
        new EdgeFactory<Point, Line>() {

            @Override
            public Line createEdge(Point from, Point to) {
                return new Line(from, to);
            }

        }) {

            @Override
            public double getEdgeWeight(Line line) {
                return line.getDistance();
            }

            @Override
            @Deprecated
            public void setEdgeWeight(Line arg0, double arg1) {
                throw new NotImplementedException();
            }
        };

        Collection<Point> significantPoints = new LinkedList<Point>();
        for (Region inflatedObstacle : inflatedObstaclesForGraph) {
            Polygon polygon = (Polygon) inflatedObstacle;
            significantPoints.addAll(Arrays.asList(polygon.getPoints()));
        }
        significantPoints.add(goal);

        Set<Point> closed = new HashSet<Point>();
        Queue<Point> open = new LinkedList<Point>();

        open.add(start);
        visibilityGraph.addVertex(start);

        while (!open.isEmpty()) {
            Point current = open.poll();
            if (!closed.contains(current)) {
                closed.add(current);
                for (Point point : significantPoints) {
                    if (!current.equals(point) && Util.isVisible(current, point, inflatedObstaclesForCollisionChecking)) {
                        visibilityGraph.addVertex(point);
                        visibilityGraph.addEdge(current, point);
                        visibilityGraph.addEdge(point, current);
                        open.add(point);
                    }
                }
            }

        }

        return visibilityGraph;
    }

}
