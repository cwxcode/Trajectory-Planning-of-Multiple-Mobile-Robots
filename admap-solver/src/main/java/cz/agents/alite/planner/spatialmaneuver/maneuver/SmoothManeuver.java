package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


public class SmoothManeuver extends Maneuver {

    private PitchManeuver pitch, endPitch;
    private ToManeuver toManeuver;
    private boolean verticalStart;
    private boolean verticalEnd;

    public SmoothManeuver(Maneuver smoothFrom, Maneuver smoothTo, PathFindSpecification specification) {
        this(smoothFrom.start, smoothFrom.direction, smoothFrom.time, smoothTo.getEnd(), smoothTo.getEndDirection(),
                specification);
    }

    public SmoothManeuver(Point3d start, Vector3d direction, double time, Point3d end, Vector3d endDirection,
            PathFindSpecification specification) {
        super(start, direction, time, specification);

        PitchManeuver tmpPitch = null;
        Vector3d pitchDirection;

        Point3d toStart = start;
        Vector3d toDirection = direction;
        double toTime = time;
        Point3d toEnd = end;
        Vector3d toEndDirection = endDirection;

        if (direction.z != 0) {
            verticalStart = true;

            pitch = new PitchToLevelManeuver(start, direction, time, specification);
            toStart = pitch.getEnd();
            toDirection = pitch.getEndDirection();
            toTime = pitch.getEndTime();
        }

        if (endDirection.z != 0) {
            verticalEnd = true;

            pitchDirection = new Vector3d(endDirection);
            pitchDirection.negate();

            tmpPitch = new PitchToLevelManeuver(end, pitchDirection, time, specification);
            pitchDirection = tmpPitch.getEndDirection();
            pitchDirection.negate();

            toEnd = tmpPitch.getEnd();
            toEndDirection = pitchDirection;
            toTime = tmpPitch.getEndTime();
        }

        toManeuver = new ToManeuver(toStart, toDirection, toTime, toEnd, toEndDirection, specification);

        if (endDirection.z != 0) {
            endPitch = new PitchManeuver(toManeuver.getEnd(), toManeuver.getEndDirection(), toManeuver.getEndTime(),
                    tmpPitch.getRadius(), tmpPitch.getAngle(), specification);
        }
    }

    @Override
    public Point3d getEnd() {
        return getLastSubManeuver().getEnd();
    }

    @Override
    public Vector3d getEndDirection() {
        return getLastSubManeuver().getEndDirection();
    }

    @Override
    public double getLength() {
        double length = toManeuver.getLength();
        if (isVerticalStart()) {
            length += pitch.getLength();
        }
        if (isVerticalEnd()) {
            length += getEndPitch().getLength();
        }

        return length;
    }

    public boolean isVerticalStart() {
        return verticalStart;
    }

    public PitchManeuver getPitch() {
        return pitch;
    }

    public ToManeuver getToManeuver() {
        return toManeuver;
    }

    public boolean isVerticalEnd() {
        return verticalEnd;
    }

    public PitchManeuver getEndPitch() {
        return endPitch;
    }

    private Maneuver getLastSubManeuver() {
        if (isVerticalEnd()) {
            return getEndPitch();
        }
        return toManeuver;
    }

    @Override
    public void setPredecessor(Maneuver predecessor) {
        super.setPredecessor(predecessor);

        if (isVerticalStart()) {
            pitch.setPredecessor(predecessor);
            predecessor = pitch;
        }
        toManeuver.setPredecessor(predecessor);
        predecessor = toManeuver;
        if (isVerticalEnd()) {
            getEndPitch().setPredecessor(predecessor);
            predecessor = getEndPitch();
        }
    }

    @Override
    public boolean isIntersectingFullZone() {
        boolean isIntersecting = false;
        if (isVerticalStart()) {
            isIntersecting = pitch.isIntersectingFullZone();
        }
        if (!isIntersecting && isVerticalEnd()) {
            isIntersecting = getEndPitch().isIntersectingFullZone();
        }
        isIntersecting = isIntersecting || toManeuver.isIntersectingFullZone();

        return isIntersecting;
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
