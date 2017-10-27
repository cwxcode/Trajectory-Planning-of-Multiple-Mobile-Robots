package tt.euclidyaw3i.discretization;

import org.apache.commons.math3.util.MathUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclidyaw3i.Point;

import java.util.*;

public class ManeuverTree {

    public static DirectedGraph<Point, PathSegment> buildTree(Point init, Collection<PathSegment> maneuvers, Rectangle boundingBox, Polygon footprint, Collection<Polygon> obstacles) {
        DirectedGraph<Point, PathSegment> graph = new DirectedWeightedMultigraph<Point, PathSegment>(PathSegment.class);

        graph.addVertex(init);
        Queue<Point> open = new LinkedList<>();
        open.offer(init);

        while (!open.isEmpty()) {
            Point current = open.poll();
            for (PathSegment maneuver : maneuvers) {
                PathSegment candidate = maneuver.getRotatedAndTranslated(current.getYawInRads(), current.getPos().x, current.getPos().y);
                Point endVertex = candidate.getLastWaypoint();
                if (boundingBox.isInside(endVertex.getPos())) {
                    if (CollisionCheck.collisionFree(candidate, footprint, obstacles)) {
                        if (!graph.containsVertex(endVertex)) {
                            graph.addVertex(endVertex);
                            open.offer(endVertex);
                        }
                        graph.addEdge(current, endVertex, candidate);
                    }
                }
            }
        }

        return graph;
    }

    public static Collection<PathSegment> getConstantCurvatureArcs(double[] headingRates, float duration, float samplingInterval) {
        List<PathSegment> pathSegments = new LinkedList<>();
        for (double headingRate : headingRates) {
            List<Point> points = new LinkedList<>();
            float x = 0;
            float y = 0;
            float theta = 0;

            points.add(new Point(0, 0, 0));
            for (float t = 0; t < duration; t += samplingInterval) {
                x += Math.cos(theta) * samplingInterval;
                y += Math.sin(theta) * samplingInterval;
                theta += headingRate * samplingInterval;
                theta = (float) MathUtils.normalizeAngle(theta, 0);
                points.add(new Point(Math.round(x), Math.round(y), theta));
            }
            pathSegments.add(new PathSegment(points.toArray(new Point[0])));
        }

        return pathSegments;
    }

    public static Collection<PathSegment> getStraights(double[] headings, float duration, float samplingInterval) {
        List<PathSegment> pathSegments = new LinkedList<>();
        for (double angle : headings) {
            List<Point> points = new LinkedList<>();
            float x = 0;
            float y = 0;
            float theta = (float) angle;

            points.add(new Point(0, 0, theta));
            for (float t = 0; t < duration; t += samplingInterval) {
                x += Math.cos(theta) * samplingInterval;
                y += Math.sin(theta) * samplingInterval;
                points.add(new Point(Math.round(x), Math.round(y), theta));
            }
            pathSegments.add(new PathSegment(points.toArray(new Point[0])));
        }

        return pathSegments;
    }

    public static Collection<PathSegment> applyManeuvers(Point conf, Collection<PathSegment> maneuvers) {
        List<PathSegment> result = new LinkedList<>();
        for (PathSegment maneuver : maneuvers) {
            result.add(maneuver.getRotatedAndTranslated(conf.getYawInRads(), conf.getPos().x, conf.getPos().y));
        }
        return result;
    }
}
