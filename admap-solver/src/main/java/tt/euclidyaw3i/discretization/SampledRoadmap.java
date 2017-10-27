package tt.euclidyaw3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclidyaw3i.Point;

import java.util.Collection;

public class SampledRoadmap {

    public static DirectedGraph<Point, PathSegment>
    buildPRM(Rectangle boundingBox, int n,
                 Collection<Point> specialPoints, Distance distance, double connectionDistance,
                 Steering steering, Polygon footprint, Collection<Polygon> obstacles) {

        DirectedWeightedMultigraph<Point, PathSegment> graph = new DirectedWeightedMultigraph<Point, PathSegment>(PathSegment.class) {
            @Override
            public double getEdgeWeight(PathSegment pathSegment) {
                return pathSegment.getLength();
            }
        };

        generateUniformSamples(graph, boundingBox, n);


        for(Point point : specialPoints) {
            graph.addVertex(point);
        }
        connectVertices(graph, distance, connectionDistance, steering, footprint, obstacles);

        return graph;
    }

    public static DirectedGraph<Point, PathSegment>
    buildLattice(Rectangle boundingBox, int cols, int rows, int angles,
                 Collection<Point> specialPoints, Distance distance, double connectionDistance,
                 Steering steering, Polygon footprint, Collection<Polygon> obstacles) {

        DirectedWeightedMultigraph<Point, PathSegment> graph = new DirectedWeightedMultigraph<Point, PathSegment>(PathSegment.class) {
            @Override
            public double getEdgeWeight(PathSegment pathSegment) {
                return pathSegment.getLength();
            }
        };

        generateSukharevGrid(graph, boundingBox, cols, rows, angles);


        for(Point point : specialPoints) {
            graph.addVertex(point);
        }
        connectVertices(graph, distance, connectionDistance, steering, footprint, obstacles);

        return graph;
    }

    private static void connectVertices(DirectedWeightedMultigraph<Point, PathSegment> graph, Distance distance, double connectionDistance, Steering steering, Polygon footprint, Collection<Polygon> obstacles) {
        // generate edges
        for (Point vertex : graph.vertexSet()) {
            for (Point other : graph.vertexSet()) {
                double d = distance.getDistance(vertex, other);
                if (d < connectionDistance && !vertex.equals(other)) {
                    PathSegment path = steering.getSteering(vertex, other);
                    if (path != null && CollisionCheck.collisionFree(path, footprint, obstacles)) {
                        graph.addEdge(vertex, other, path);
                    }
                }
            }

        }
    }

    private static void generateSukharevGrid(DirectedWeightedMultigraph<Point, PathSegment> graph, Rectangle boundingBox, int cols, int rows, int angles) {
        // generate vertices
        double hSpacing = (boundingBox.getCorner2().x - boundingBox.getCorner1().x) / (cols);
        double vSpacing = (boundingBox.getCorner2().y - boundingBox.getCorner1().y) / (rows);

        for (int row=0; row < rows; row++) {
            for (int col=0; col < cols; col++) {
                int x = boundingBox.getCorner1().x + (int) Math.round(hSpacing/2 + row * hSpacing);
                int y = boundingBox.getCorner1().y + (int) Math.round(vSpacing/2 + col * vSpacing);

                double angleStep = (2 * Math.PI) / (angles);
                for (double angle= -Math.PI; angle < 0.9999*Math.PI; angle += angleStep) {
                    graph.addVertex(new Point(x, y, (float) angle));
                }
            }
        }
    }

    private static void generateUniformSamples(DirectedWeightedMultigraph<Point, PathSegment> graph, Rectangle boundingBox, int n) {
        for (int i=0; i < n; i++) {
                int x = boundingBox.getCorner1().x + (int) Math.random() * (boundingBox.getCorner2().x - boundingBox.getCorner1().x);
                int y = boundingBox.getCorner1().y + (int) Math.random() * (boundingBox.getCorner2().y - boundingBox.getCorner1().y);
                double angle = -Math.PI + (2 * Math.PI) * Math.random();
                graph.addVertex(new Point(x, y, (float) angle));
        }
    }
}
