package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import cz.agents.alite.planner.spatialmaneuver.GeometryUtils;


public class PrismZone implements Zone {

    private Point2d[] points;
    private double[] xs;
    private double[] ys;

    public PrismZone(Point2d[] points, double height) {
        this.points = points;

        xs = new double[points.length];
        ys = new double[points.length];
        int i = 0;
        for (Point2d point : points) {
            xs[i] = point.x;
            ys[i] = point.y;
            i++;
        }
    }

    @Override
    public boolean testPoint(final Point3d point) {
        return GeometryUtils.isPointInsidePolygon(xs, ys, point.x, point.y);
    }

    @Override
    public boolean testLine(final Point3d point1, final Point3d point2, Point3d outPoint) {
        // TODO: test if point1 or point2 are in the polygon and return true if so
        Point2d oldPoint = points[points.length - 1];
        for (Point2d point : points) {
            boolean intersects = GeometryUtils.isLineIntersectingLine(
                    point1.x, point1.y, point2.x, point2.y,
                    point.x, point.y, oldPoint.x, oldPoint.y);
            if (intersects) {
                // TODO: outPoint
                return true;
            }
            oldPoint = point;
        }

        return false;

    }

    @Override
    public boolean testLine(Point3d point1, Point3d point2) {
        // TODO: has to be changed after finishing outPoint TODO in testLine(Point3d, Point3d, Point3d)
        return testLine(point1, point2, null);
    }

    @Override
    public List<Point3d> findLineIntersections(Point3d point1, Point3d point2) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public void accept(ZoneVisitor visitor) {
        visitor.visit(this);
    }

    public Point2d[] getPoints() {
        return points;
    }

    public double[] getXs() {
        return xs;
    }

    public double[] getYs() {
        return ys;
    }

}
