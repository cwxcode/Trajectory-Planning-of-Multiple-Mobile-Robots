package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class BoxZone implements Zone {

    private Vector3d axes;

    public BoxZone(Vector3d axes) {
        this.axes = axes;
    }

    @Override
    public boolean testPoint(final Point3d point) {
        if (point.x > 0 && point.x < axes.x && point.y > 0 && point.y < axes.y && point.z > 0
                && point.z < axes.z) {
            return true;
        }
        return false;
    }

    @Override
    public boolean testLine(final Point3d point1, final Point3d point2, Point3d outPoint) {
        if (outPoint == null) {
            outPoint = new Point3d();
        }
        return checkLineBox(new Point3d(0, 0, 0), new Point3d(axes), point1, point2, outPoint);
    }

    @Override
    public boolean testLine(Point3d point1, Point3d point2) {
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

    private boolean getIntersection(double fDst1, double fDst2, Point3d P1, Point3d P2, Point3d hit) {
        if ((fDst1 * fDst2) >= 0.0)
            return false;
        if (fDst1 == fDst2)
            return false;

        hit.set(P2);
        hit.sub(P1);
        hit.scale(-fDst1 / (fDst2 - fDst1));
        hit.add(P1);
        return true;
    }

    private boolean isInBox(Point3d Hit, Point3d B1, Point3d B2, final int Axis) {
        if (Axis == 1 && Hit.z > B1.z && Hit.z < B2.z && Hit.y > B1.y && Hit.y < B2.y)
            return true;
        if (Axis == 2 && Hit.z > B1.z && Hit.z < B2.z && Hit.x > B1.x && Hit.x < B2.x)
            return true;
        if (Axis == 3 && Hit.x > B1.x && Hit.x < B2.x && Hit.y > B1.y && Hit.y < B2.y)
            return true;
        return false;
    }

    // returns true if line (L1, L2) intersects with the box (B1, B2)
    // returns intersection point in Hit
    private boolean checkLineBox(Point3d B1, Point3d B2, Point3d L1, Point3d L2, Point3d Hit) {
        if (L2.x < B1.x && L1.x < B1.x)
            return false;
        if (L2.x > B2.x && L1.x > B2.x)
            return false;
        if (L2.y < B1.y && L1.y < B1.y)
            return false;
        if (L2.y > B2.y && L1.y > B2.y)
            return false;
        if (L2.z < B1.z && L1.z < B1.z)
            return false;
        if (L2.z > B2.z && L1.z > B2.z)
            return false;
        if (L1.x > B1.x && L1.x < B2.x && L1.y > B1.y && L1.y < B2.y && L1.z > B1.z && L1.z < B2.z) {
            Hit.set(L1);
            return true;
        }
        if ((getIntersection(L1.x - B1.x, L2.x - B1.x, L1, L2, Hit) && isInBox(Hit, B1, B2, 1))
                || (getIntersection(L1.y - B1.y, L2.y - B1.y, L1, L2, Hit) && isInBox(Hit, B1, B2, 2))
                || (getIntersection(L1.z - B1.z, L2.z - B1.z, L1, L2, Hit) && isInBox(Hit, B1, B2, 3))
                || (getIntersection(L1.x - B2.x, L2.x - B2.x, L1, L2, Hit) && isInBox(Hit, B1, B2, 1))
                || (getIntersection(L1.y - B2.y, L2.y - B2.y, L1, L2, Hit) && isInBox(Hit, B1, B2, 2))
                || (getIntersection(L1.z - B2.z, L2.z - B2.z, L1, L2, Hit) && isInBox(Hit, B1, B2, 3)))
            return true;

        return false;
    }

}
