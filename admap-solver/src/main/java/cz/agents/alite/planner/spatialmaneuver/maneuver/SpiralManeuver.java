package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


public class SpiralManeuver extends Maneuver {

    private int loops;

    private double loopHeight;

    public SpiralManeuver(Point3d start, Vector3d direction, double time, int loops,
            PathFindSpecification specification) {
        super(start, direction, time, specification);
        this.loops = loops;
    }

    @Override
    public double getLength() {
        double radius = specification.getRadius();
        double pitchAngle = direction.angle(new Vector3d(direction.x, direction.y, 0.0));
        return loops*2.0*Math.PI*radius/Math.cos(pitchAngle);
    }

    @Override
    public Point3d getEnd() {
        Point3d end = super.getEnd();
        if (end == null) {
            double radius = specification.getRadius();

            end = new Point3d(start);
            double pitchAngle = direction.angle(new Vector3d(direction.x, direction.y, 0.0));
            if (direction.z < 0) {
                pitchAngle = -pitchAngle;
            }

            loopHeight = 2.0*Math.PI*radius*Math.tan(pitchAngle);
            end.z += loops*loopHeight;

            setEnd(end);
        }
        return end;
    }

    @Override
    public Vector3d getEndDirection() {
        Vector3d endDirection = super.getEndDirection();
        if (endDirection == null) {
            endDirection = new Vector3d(direction);
            setEndDirection(endDirection);
        }
        return endDirection;
    }

    @Override
    public boolean isIntersectingFullZone() {
        double radius = specification.getRadius();
        getEnd();

        double alphaShift = Math.atan2(direction.y, direction.x) + Math.PI/2;
        Vector3d centerShift = new Vector3d(radius*direction.y, -radius*direction.x, 0);
        Point3d prev = new Point3d(start);
        Point3d actual = new Point3d();

        for (int loop = 0; loop < loops; ++loop) {
            for (double alpha = loop*2*Math.PI; alpha <= (loop+1)*2*Math.PI+1e-5; alpha += Math.PI/4) {
                double alphaRotated = -alpha + alphaShift;
                actual.set(radius*Math.cos(alphaRotated), radius*Math.sin(alphaRotated),
                        loopHeight*alpha/Math.PI/2);
                actual.add(start);
                actual.add(centerShift);
                if (specification.getZone().testLine(prev, actual)) {
                    return true;
                }
                prev.set(actual);
            }
        }

        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
