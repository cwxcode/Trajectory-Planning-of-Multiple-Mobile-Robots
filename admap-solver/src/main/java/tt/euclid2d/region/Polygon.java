package tt.euclid2d.region;

import java.util.*;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.convhull.ConvexHull2D;
import math.geom2d.polygon.convhull.JarvisMarch2D;
import org.apache.commons.lang3.ArrayUtils;

import tt.euclid2d.Point;
import tt.euclid2d.util.Intersection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Polygon implements Region {

    private Point[] polygonPoints;

    public Polygon(Point[] points) {
        super();
        this.polygonPoints = points;
    }

    public Polygon(tt.euclid2i.Point[] points) {
        super();
        this.polygonPoints = new Point[points.length];

        for (int i = 0; i < points.length; i++) {
            this.polygonPoints[i] = new Point(points[i].x, points[i].y);
        }
    }

    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < polygonPoints.length - 1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, polygonPoints[i].x, polygonPoints[i].y, polygonPoints[i + 1].x, polygonPoints[i + 1].y, true)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInside(Point p) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = polygonPoints.length - 1; i < polygonPoints.length; j = i++) {
            if ((polygonPoints[i].y > p.y) != (polygonPoints[j].y > p.y) &&
                    (p.x < (polygonPoints[j].x - polygonPoints[i].x) * (p.y - polygonPoints[i].y) / (polygonPoints[j].y - polygonPoints[i].y) + polygonPoints[i].x)) {
                result = !result;
            }
        }

        if (isFilledInside()) {
            return result;
        } else {
            return !result;
        }

    }

    public Point[] getPoints() {
        return polygonPoints;
    }

    public List<Polygon> inflate(double inflateBy, int pointsAtCorner) {
    	return inflate(inflateBy, pointsAtCorner, isFilledInside());
    }

    public List<Polygon> inflate(double inflateBy, int pointsAtCorner, boolean inflatedFilledInside) {
        int size = polygonPoints.length;

        Coordinate[] coordinates = new Coordinate[size + 1];
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(polygonPoints[i].getX(), polygonPoints[i].getY());
        }
        coordinates[size] = coordinates[0];

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);

        if (!isFilledInside()) {
            // filled outside, deflate
            inflateBy = -inflateBy;
        }

        Geometry jtsPolygon = new com.vividsolutions.jts.geom.Polygon(ring, new LinearRing[]{}, geometryFactory);
        Geometry buffered = jtsPolygon.buffer(inflateBy, pointsAtCorner);
        
        Coordinate[] bufferedCoordinates = buffered.getCoordinates();
        // if we have a an ouside filled polygon, then we can get more polygons as a result of inflation
        // the array will consist of multiple closed rings that need to be split apart
        List<Polygon> polygons = new LinkedList<Polygon>();

        if (bufferedCoordinates.length > 0) {
            int bufferedSize = bufferedCoordinates.length;
            List<Point> polygonPoints = null;
            for (int i=0; i < bufferedSize; i++) {
                if (polygonPoints == null) {
                    // start a new polygon
                    polygonPoints = new LinkedList<Point>();
                    polygonPoints.add(new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y));
                } else {
                    // Add points to current polygon
                    Point currentPoint = new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y);
                    polygonPoints.add(currentPoint);
                    if (currentPoint.equals(polygonPoints.get(0))) {
                        // We found the last (closing) point of the polygon -- create a new polygon from the sequence
                        Point[] inflatedPolygonPoints = polygonPoints.toArray(new Point[polygonPoints.size()]);

                        if (inflatedFilledInside != isClockwise(inflatedPolygonPoints)) {
                        	ArrayUtils.reverse(inflatedPolygonPoints);
                        }

                        polygons.add(new Polygon(inflatedPolygonPoints));
                        polygonPoints = null;
                    }
                }
            }


            return polygons;
        } else {
            return new LinkedList<Polygon>();
        }
    }

    public boolean isFilledInside() {
        return isClockwise(polygonPoints);
    }

    /**
     * Determines if the ring of points is defined in a clockwise direction
     * @param points the array of points constituting the border of the polygon
     * @return true if the ring is defined clockwise
     */
    public static boolean isClockwise(Point[] points){

        double sumOverEdges = 0;

        for (int i = 0; i < points.length; i++) {
            if(i < points.length - 1){
                sumOverEdges += ((points[i + 1].x - points[i].x) * (points[i + 1].y + points[i].y));
            }
            else{
                sumOverEdges += ((points[0].x - points[i].x) * (points[0].y + points[i].y));
            }
        }

        return (sumOverEdges < 0);
    }

    /**
     * Computes Minokowski sum of this polygon with given polygon.
     * Both polygons must be convex.
     * @param q the other polygon
     */
    public Polygon minkowskiSum(Polygon q) {
        LinkedList<Point2D> resultPoints = new LinkedList<>();
        for (Point pp: polygonPoints) {
            for (Point pq: q.getPoints()) {
                resultPoints.add(new Point2D(pp.x - pq.x, pp.y - pq.y));
            }
        }

        JarvisMarch2D ch = new JarvisMarch2D();
        Polygon2D theirPolygon = ch.convexHull(resultPoints);

        Point[] ourPoints = new Point[theirPolygon.vertexNumber()];
        for (int i=0; i<theirPolygon.vertexNumber(); i++) {
            Point2D v = theirPolygon.vertex(i);
            ourPoints[i] = new Point(v.x(),v.y());
        }

        return new Polygon(ourPoints);
    }


}
