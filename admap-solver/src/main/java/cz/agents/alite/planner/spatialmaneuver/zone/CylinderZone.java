package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

public class CylinderZone implements Zone {

    private Vector2d semiAxes;
    private double halfHeight;

    /**
     * point <0,0,0> (without translation) is in the center of cylinder, thus it
     * is from <-height/2 ; height/2>
     */
    public CylinderZone(Vector2d semiAxes, double height) {
        this.semiAxes = semiAxes;
        this.halfHeight = height / 2;
    }

    @Override
    public boolean testPoint(final Point3d point) {
        final double expandedSemiAxesx = semiAxes.x;
        final double expandedSemiAxesy = semiAxes.y;
        final double expandedHalfHeight = halfHeight;
        if ((expandedSemiAxesx <= 0) || (expandedSemiAxesy <= 0) || (expandedHalfHeight <= 0)) {
            return false;
        }

        final double px = point.x;
        final double py = point.y;
        final double pz = point.z;

        final double length = px * px / (expandedSemiAxesx * expandedSemiAxesx) + py * py
                / (expandedSemiAxesy * expandedSemiAxesy);
        if (length <= 1 && Math.abs(pz) <= expandedHalfHeight) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean testLine(final Point3d point1, final Point3d point2, final Point3d outPoint) {
        final double ret = testLineForFirstPoint(point1.x, point1.y, point1.z, point2.x, point2.y,
                point2.z);
        if (ret >= 0) {
            if (outPoint != null) {
                final double dx = point2.x - point1.x;
                final double dy = point2.y - point1.y;
                final double dz = point2.z - point1.z;
                final double out1x = point1.x + dx * ret;
                final double out1y = point1.y + dy * ret;
                final double out1z = point1.z + dz * ret;
                outPoint.set(out1x, out1y, out1z);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean testLine(Point3d point1, Point3d point2) {
        return testLine(point1, point2, null);
    }

    @Override
    public List<Point3d> findLineIntersections(Point3d point1, Point3d point2) {
        final double ret1 = testLineForFirstPoint(point1.x, point1.y, point1.z, point2.x, point2.y,point2.z);
        final double ret2 = testLineForFirstPoint(point2.x, point2.y,point2.z, point1.x, point1.y, point1.z);

        List<Point3d> out = new LinkedList<Point3d>();
        double dx, dy, dz;
        double outx, outy, outz;
        
        if(ret1 >= 0 && ret1 < 1){
        	dx = point2.x - point1.x;
        	dy = point2.y - point1.y;
        	dz = point2.z - point1.z;
        	outx = point1.x + dx * ret1;
        	outy = point1.y + dy * ret1;
        	outz = point1.z + dz * ret1;
        	out.add(new Point3d(outx, outy, outz));
        }
        if(ret2 >= 0 && ret2 < 1){
        	dx = point1.x - point2.x;
        	dy = point1.y - point2.y;
        	dz = point1.z - point2.z;
        	outx = point2.x + dx * ret2;
        	outy = point2.y + dy * ret2;
        	outz = point2.z + dz * ret2;
        	out.add(new Point3d(outx, outy, outz));
        }

        return out;
    }

    private double testLineForFirstPoint(final double point1x, final double point1y,
            final double point1z, final double point2x, final double point2y, final double point2z) {
        final double expandedSemiAxesx = semiAxes.x;
        final double expandedSemiAxesy = semiAxes.y;
        final double expandedHalfHeight = halfHeight;
        if ((expandedSemiAxesx <= 0) || (expandedSemiAxesy <= 0) || (expandedHalfHeight <= 0)) {
            return -1;
        }

        final double dx = point2x - point1x;
        final double dy = point2y - point1y;
        final double dz = point2z - point1z;

        double zResultx = 0, zResulty = 0;
        final double pz = point1z;
        if (Math.abs(dz) < 1e-5) {
            if (Math.abs(pz) < expandedHalfHeight - 1e-5) {
                zResultx = 0;
                zResulty = 1;
            } else { // under or above cylinder
                return -1;
            }
        } else if (dz > 0) {
            zResultx = (-expandedHalfHeight - pz) / dz;
            zResulty = (expandedHalfHeight - pz) / dz;
        } else {
            zResultx = (expandedHalfHeight - pz) / dz;
            zResulty = (-expandedHalfHeight - pz) / dz;
        }
        if (Math.abs(dx) < 1e-5 && Math.abs(dy) < 1e-5 && Math.abs(dz) < 1e-5) {
            final double px = point1x;
            final double py = point1y;
            final double length = px * px / (expandedSemiAxesx * expandedSemiAxesx) + py * py
                    / (expandedSemiAxesy * expandedSemiAxesy);
            if (length <= 1 - 1e-5) {
                return 0;
            } else {
                return -1;
            }
        }

        // inlined test centered points
        final double x = point1x;
        final double y = point1y;
        final double a2 = expandedSemiAxesx * expandedSemiAxesx;
        final double b2 = expandedSemiAxesy * expandedSemiAxesy;
        final double A = b2 * dx * dx + a2 * dy * dy;
        final double B = 2 * (b2 * x * dx + a2 * y * dy);
        final double C = b2 * x * x + a2 * y * y - a2 * b2;
        final double D = B * B - 4 * A * C;
        if (D < 0) {
            // not intersecting
            return -1;
        }
        double result1 = (-B - Math.sqrt(D)) / (2 * A);
        double result2 = (-B + Math.sqrt(D)) / (2 * A);

        result1 = Math.max(result1, zResultx);
        result2 = Math.min(result2, zResulty);
        if ((result2 - result1 < 1e-5) || (Math.max(result1, result2) < 0)
                || (Math.min(result1, result2) > 1)) {
            return -1;
        }
        if (result1 < 1e-5) {
            result1 = 0.0;
        }
        return result1;
    }

    public Vector2d getSemiAxes() {
        return semiAxes;
    }

    public double getHeight() {
        return halfHeight * 2.0;
    }

    @Override
    public void accept(ZoneVisitor visitor) {
        visitor.visit(this);
    }

}
