package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class TransformZone implements Zone {

    private Zone zone;

    private Vector3d translation;
    private Vector2d size;
    private double angleRotation;

    private double sinCache;
    private double cosCache;
    private double sinCacheInv;
    private double cosCacheInv;

    /**
     *
     * @param zone
     *            Zone
     * @param translation
     *            Vector3d
     * @param size
     *            Vector2d - first part for both horizontal sizes (x and y) and
     *            second part for z size
     * @param angleRotation
     *            double - rotation around Z axis of the sub zone
     */
    public TransformZone(Zone zone, Vector3d translation, Vector2d size, double angleRotation) {
        this.zone = zone;
        setTransformation(translation, size, angleRotation);
    }

    public void setTransformation(Vector3d translation, Vector2d size, double angleRotation) {
        this.translation = new Vector3d(translation);
        this.size = new Vector2d(size);
        this.angleRotation = angleRotation;

        sinCache = Math.sin(-angleRotation);
        cosCache = Math.cos(-angleRotation);
        sinCacheInv = Math.sin(angleRotation);
        cosCacheInv = Math.cos(angleRotation);
    }

    public Zone getZone() {
        return zone;
    }

    @Override
    public boolean testPoint(final Point3d point) {
        final double newPointx = transformPointX(point.x, point.y);
        final double newPointy = transformPointY(point.x, point.y);
        final double newPointz = transformPointZ(point.z);
        return zone.testPoint(new Point3d(newPointx, newPointy, newPointz));
    }

    @Deprecated
    @Override
    public boolean testLine(final Point3d point1, final Point3d point2, final Point3d outPoint) {
        final double newPoint1x = transformPointX(point1.x, point1.y);
        final double newPoint1y = transformPointY(point1.x, point1.y);
        final double newPoint1z = transformPointZ(point1.z);

        final double newPoint2x = transformPointX(point2.x, point2.y);
        final double newPoint2y = transformPointY(point2.x, point2.y);
        final double newPoint2z = transformPointZ(point2.z);

        boolean test = zone.testLine(new Point3d(newPoint1x, newPoint1y, newPoint1z), new Point3d(
                newPoint2x, newPoint2y, newPoint2z), outPoint);

        if (test && outPoint != null) {
            invTransformPoint(outPoint);
        }
        return test;
    }

    @Override
    public boolean testLine(Point3d point1, Point3d point2) {
        final double newPoint1x = transformPointX(point1.x, point1.y);
        final double newPoint1y = transformPointY(point1.x, point1.y);
        final double newPoint1z = transformPointZ(point1.z);

        final double newPoint2x = transformPointX(point2.x, point2.y);
        final double newPoint2y = transformPointY(point2.x, point2.y);
        final double newPoint2z = transformPointZ(point2.z);

        return zone.testLine(new Point3d(newPoint1x, newPoint1y, newPoint1z), new Point3d(
                newPoint2x, newPoint2y, newPoint2z));
    }

    @Override
    public List<Point3d> findLineIntersections(Point3d point1, Point3d point2) {
        final double newPoint1x = transformPointX(point1.x, point1.y);
        final double newPoint1y = transformPointY(point1.x, point1.y);
        final double newPoint1z = transformPointZ(point1.z);

        final double newPoint2x = transformPointX(point2.x, point2.y);
        final double newPoint2y = transformPointY(point2.x, point2.y);
        final double newPoint2z = transformPointZ(point2.z);

        List<Point3d> intersections = zone.findLineIntersections(new Point3d(newPoint1x, newPoint1y, newPoint1z), new Point3d(
                newPoint2x, newPoint2y, newPoint2z));

        for(Point3d p : intersections){
            invTransformPoint(p);
        }

        return intersections;


    }

    public Vector3d getTranslation() {
        return translation;
    }

    public Vector2d getSize() {
        return size;
    }

    public double getAngleRotation() {
        return angleRotation;
    }

    private double transformPointX(final double pointx, final double pointy) {
        return (cosCache * (pointx - translation.x) - sinCache * (pointy - translation.y)) / size.x;
    }

    private double transformPointY(final double pointx, final double pointy) {
        return (sinCache * (pointx - translation.x) + cosCache * (pointy - translation.y)) / size.x;
    }

    private double transformPointZ(final double pointz) {
        return (pointz - translation.z) / size.y;
    }

    private void invTransformPoint(final Point3d point) {
        double tx = cosCacheInv * point.x * size.x - sinCacheInv * point.y * size.x + translation.x;
        double ty = sinCacheInv * point.x * size.x + cosCacheInv * point.y * size.x + translation.y;
        double tz = point.z * size.y + translation.z;

        point.set(tx, ty, tz);
    }

    @Override
    public void accept(ZoneVisitor visitor) {
        visitor.visit(this);
    }

}
