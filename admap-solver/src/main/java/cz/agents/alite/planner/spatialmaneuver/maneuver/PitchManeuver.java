package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


/**
 * @author Antonin Komenda
 *
 */
public class PitchManeuver extends Maneuver {

    private double pitchRadius;
    private double angle;

    public PitchManeuver(Point3d start, Vector3d direction, double time, double pitchRadius, double angle,
            PathFindSpecification specification) {
        super(start, direction, time, specification);
        this.pitchRadius = pitchRadius;
        this.angle = angle;
    }

    @Override
    public double getLength() {
        return Math.abs(pitchRadius)*angle;
    }

    public double getRadius() {
        return pitchRadius;
    }

    public void setRadius(double pitchRadius) {
        this.pitchRadius = pitchRadius;
        invalidate();
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        invalidate();
    }

    @Override
    public Point3d getEnd() {
        Point3d end = super.getEnd();
        if (end == null) {
            end = new Point3d(start);

            Vector3d dir = new Vector3d(direction);
            if (pitchRadius < 0) {
                dir.negate();
            }

            //TODO: problem with direction = (0,0,1)
            Vector3d toCenter = new Vector3d();
            toCenter.cross(dir, new Vector3d(-dir.y, dir.x, 0.0));
            toCenter.normalize();

            double lengthXY = new Vector2d(toCenter.x, toCenter.y).length();
            if(dir.z > 0) lengthXY = -lengthXY;

            double deltaAngle = Math.atan2(-toCenter.z, -lengthXY) + angle;

            Vector3d toEndXY = new Vector3d(dir);
            toEndXY.z = 0;
            toEndXY.normalize();
            toEndXY.z = 1;
            toEndXY.scale(pitchRadius);
            toCenter.scale(pitchRadius);

            end.x = end.x + toCenter.x + toEndXY.x*Math.cos(deltaAngle);
            end.y = end.y + toCenter.y + toEndXY.y*Math.cos(deltaAngle);
            end.z = end.z + toCenter.z + toEndXY.z*Math.sin(deltaAngle);

            setEnd(end);
        }
        return end;
    }

    @Override
    public Vector3d getEndDirection() {
        Vector3d endDirection = super.getEndDirection();
        if (endDirection == null) {
            endDirection = new Vector3d();

            Vector3d dir = new Vector3d(direction);
            if (pitchRadius < 0) {
                dir.negate();
            }

            double lengthXY = new Vector2d(dir.x, dir.y).length();

            Vector3d toEndXY = new Vector3d(dir);
            toEndXY.z = 1;
            toEndXY.normalize();
            if (pitchRadius < 0) {
                toEndXY.negate();
            }

            endDirection.x = (lengthXY*Math.cos(angle) - dir.z*Math.sin(angle))*toEndXY.x;
            endDirection.y = (lengthXY*Math.cos(angle) - dir.z*Math.sin(angle))*toEndXY.y;
            endDirection.z = (lengthXY*Math.sin(angle) + dir.z*Math.cos(angle))*toEndXY.z;

            setEndDirection(endDirection);
        }
        return endDirection;
    }

    @Override
    public boolean isIntersectingFullZone() {
        final Point3d end = getEnd();
        return specification.getZone().testLine(start, end);
    }

    @Override
    public boolean isValid() {
        if (Double.isNaN(angle)) {
            return false;
        }

        Vector3d dir = getEndDirection();
        if (dir.z != 0.0
                && dir.angle(new Vector3d(dir.x, dir.y, 0.0)) > specification.getMaxPitch()) {
            return false;
        }

        return true;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
