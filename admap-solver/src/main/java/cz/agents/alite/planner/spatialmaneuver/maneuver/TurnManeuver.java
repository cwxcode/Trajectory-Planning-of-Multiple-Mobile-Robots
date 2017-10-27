package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


public class TurnManeuver extends Maneuver {

    private double radius;

    private double angle;

    public TurnManeuver(Point3d start, Vector3d direction, double time, double radius,
            double angle, PathFindSpecification specification) {
        super(start, direction, time, specification);
        this.radius = radius;
        this.angle = angle;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getLength() {
        return Math.abs(radius) * angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        invalidate();
    }

    public double getAngle() {
        return angle;
    }

    @Override
    public Point3d getEnd() {
        Point3d end = super.getEnd();
        if (end == null) {

            Point3d center = new Point3d();
            double radiusAbs;
            double angleDirected;
            if (radius < 0) {
                center.x = -direction.y;
                center.y = direction.x;
                radiusAbs = -radius;
                angleDirected = -angle;
            } else {
                center.x = direction.y;
                center.y = -direction.x;
                radiusAbs = radius;
                angleDirected = angle;
            }

            center.x = center.x * radiusAbs + start.x;
            center.y = center.y * radiusAbs + start.y;

            end = new Point3d();
            double deltaAngle = Math.atan2(start.y - center.y, start.x
                    - center.x)
                    - angleDirected;
            end.x = center.x + radiusAbs * Math.cos(deltaAngle);
            end.y = center.y + radiusAbs * Math.sin(deltaAngle);
            end.z = start.z;

            setEnd(end);
        }
        return end;
    }

    @Override
    public Vector3d getEndDirection() {
        Vector3d endDirection = super.getEndDirection();
        if (endDirection == null) {
            double fx, fy;
            if (radius < 0) {
                fx = -1;
                fy = +1;
            } else {
                fx = +1;
                fy = -1;
            }

            endDirection = new Vector3d(direction);
            endDirection.x = direction.x * Math.cos(angle) + fx * direction.y
                    * Math.sin(angle);
            endDirection.y = fy * direction.x * Math.sin(angle) + direction.y
                    * Math.cos(angle);

            setEndDirection(endDirection);
        }
        return endDirection;
    }

    @Override
    public boolean isIntersectingFullZone() {
        if (angle <= Math.PI / 32.0 + 1e-5) {
            final Point3d end = getEnd();
            return specification.getZone().testLine(start, end);
        } else {
            int steps = (int) (angle / Math.PI * 32.0);
            Point3d lastPoint = new Point3d(start);
            double oldAngle = angle;
            double stepAngle = angle / steps;

            angle = stepAngle;
            invalidate();
            for (int i = 1; i <= steps; i++, angle += stepAngle) {
                final Point3d end = getEnd();
                if (specification.getZone().testLine(lastPoint, end)) {
                    angle = oldAngle;
                    invalidate();

                    return true;
                }
                lastPoint = end;
                invalidate();
            }
            angle = oldAngle;
            invalidate();

            return false;
        }
    }

    @Override
    public boolean isValid() {
        return !Double.isNaN(angle);
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
