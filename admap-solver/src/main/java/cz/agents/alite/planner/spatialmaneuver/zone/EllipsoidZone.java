package cz.agents.alite.planner.spatialmaneuver.zone;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class EllipsoidZone implements Zone {

    private Vector3d semiAxes;

    public EllipsoidZone(Vector3d semiAxes) {
        this.semiAxes = semiAxes;
    }

    @Override
    public boolean testPoint(final Point3d point) {
        throw new RuntimeException("Not implemented yet!");
//		return testEllipsoidPoint(point.x, point.y, point.z, semiAxes.x, semiAxes.y, semiAxes.z);
    }

    @Override
    public boolean testLine(final Point3d point1, final Point3d point2, final Point3d outPoint) {
          throw new RuntimeException("Not implemented yet!");
//        final double ret = testLineForFirstPoint(point1.x, point1.y, point1.z, point1.x, point1.y, point1.z);
//        if (ret>=0) {
//            //VisualDebug.drawPoint(ret.start, Color.RED);
//            if (outPoint != null) {
//                final double dx = point2.x - point1.x;
//                final double dy = point2.y - point1.y;
//                final double dz = point2.z - point1.z;
//                final double out1x=point1.x+dx*ret;
//                final double out1y=point1.y+dy*ret;
//                final double out1z=point1.z+dz*ret;
//                outPoint.set(out1x,out1y,out1z);
//            }
//            return true;
//        }
//        return false;
    }
    
    
    @Override
	public boolean testLine(Point3d point1, Point3d point2) {
		throw new RuntimeException("Not implemented yet!");
	}

	@Override
	public List<Point3d> findLineIntersections(Point3d point1, Point3d point2) {
		throw new RuntimeException("Not implemented yet!");
	}


    @SuppressWarnings("unused")
    private double testLineForFirstPoint(final double point1x, final double point1y, final double point1z, final double point2x, final double point2y, final double point2z) {
        //TODO only approximate test
        final double expandedSemiAxesx = semiAxes.x;
        final double expandedSemiAxesy = semiAxes.y;
        final double expandedSemiAxesz = semiAxes.z;
        if ((expandedSemiAxesx <= 0) || (expandedSemiAxesy <= 0) || (expandedSemiAxesz <= 0)) {
            return -1;
        }

        final double dx = point2x - point1x;
        final double dy = point2y - point1y;
        final double dz = point2z - point1z;

        //inlined testCenteredEllipsoidIntersect
        final double x = point1x;
        final double y = point1y;
        final double z = point1z;
        final double a2 = expandedSemiAxesx*expandedSemiAxesx;
        final double b2 = expandedSemiAxesy*expandedSemiAxesy;
        final double c2 = expandedSemiAxesz*expandedSemiAxesz;

        final double A = b2*c2*dx*dx + a2*c2*dy*dy + a2*b2*dz*dz;
        final double B = 2*(b2*c2*x*dx + a2*c2*y*dy + a2*b2*z*dz);
        final double C = b2*c2*x*x + a2*c2*y*y + a2*b2*z*z - a2*b2*c2;
        final double D = B*B - 4*A*C;
        if (D<0) {
            // not intersecting
            return -1;
        }
        double result1 = (-B-Math.sqrt(D))/(2*A);
        double result2 = (-B+Math.sqrt(D))/(2*A);


        if ((result2 < 0) || (result1 > 1) || (result2-result1<1e-5)) {
            // no intersection
            return -1;
        }
        if (result1 < 1e-5) {
            result1 = 0.0;
        }
        return result1;
    }

    @SuppressWarnings("unused")
    private boolean testEllipsoidPoint(final double pointx, final double pointy, final double pointz, final double rx, final double ry, final double rz) {
        final double length = (pointx)*(pointx) / (rx*rx)
                        + (pointy)*(pointy) / (ry*ry)
                        + (pointz)*(pointz) / (rz*rz);
        return length <= 1;
    }

    @Override
    public void accept(ZoneVisitor visitor) {
        visitor.visit(this);
    }

    public Vector3d getSemiAxes() {
        return semiAxes;
    }

}

// centered sphere
//-(y0*yd+x0*xd+z0*zd-(2*y0*yd*x0*xd+2*y0*yd*z0*zd+2*x0*xd*z0*zd+xd^2*r^2-xd^2*z0^2-xd^2*y0^2-yd^2*x0^2+yd^2*r^2-yd^2*z0^2-zd^2*x0^2+zd^2*r^2-zd^2*y0^2)^(1/2))/(xd^2+yd^2+zd^2)
//-(y0*yd+x0*xd+z0*zd+(2*y0*yd*x0*xd+2*y0*yd*z0*zd+2*x0*xd*z0*zd+xd^2*r^2-xd^2*z0^2-xd^2*y0^2-yd^2*x0^2+yd^2*r^2-yd^2*z0^2-zd^2*x0^2+zd^2*r^2-zd^2*y0^2)^(1/2))/(xd^2+yd^2+zd^2)

// centered ellipsoid
//-(a^2*c^2*y0*yd+b^2*c^2*x0*xd+a^2*b^2*z0*zd-(a^2*b^2*c^2*(2*c^2*y0*yd*x0*xd+2*a^2*y0*yd*z0*zd+2*b^2*x0*xd*z0*zd+b^2*c^2*xd^2-b^2*xd^2*z0^2-c^2*xd^2*y0^2-b^2*zd^2*x0^2+a^2*b^2*zd^2-a^2*zd^2*y0^2-c^2*yd^2*x0^2+a^2*c^2*yd^2-a^2*yd^2*z0^2))^(1/2))/(b^2*c^2*xd^2+a^2*b^2*zd^2+a^2*c^2*yd^2)
//-(a^2*c^2*y0*yd+b^2*c^2*x0*xd+a^2*b^2*z0*zd+(a^2*b^2*c^2*(2*c^2*y0*yd*x0*xd+2*a^2*y0*yd*z0*zd+2*b^2*x0*xd*z0*zd+b^2*c^2*xd^2-b^2*xd^2*z0^2-c^2*xd^2*y0^2-b^2*zd^2*x0^2+a^2*b^2*zd^2-a^2*zd^2*y0^2-c^2*yd^2*x0^2+a^2*c^2*yd^2-a^2*yd^2*z0^2))^(1/2))/(b^2*c^2*xd^2+a^2*b^2*zd^2+a^2*c^2*yd^2)

// translated ellipsoid
// ?


