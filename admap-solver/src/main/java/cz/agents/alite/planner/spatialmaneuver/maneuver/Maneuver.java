package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;
import cz.agents.alite.planner.spatialmaneuver.maneuver.ManeuverSpecification.LevelConstants;


/**
 * Abstract parent of all maneuvers. Providing basic logic for maneuver connecting,
 * planning, etc.
 *
 * @author Antonin Komenda
 */
abstract public class Maneuver implements Comparable<Maneuver> {

    private static double TOLERANCE = 1e-5;

    private Point3d end;
    private Vector3d endDirection;
    private Integer hashCode;
    private double g; // distance from start;
    private double h; // estimated cost from this node to the goal node
    private Maneuver pred; // predecessor;
    private ToEndManeuver toEndManueverCache = null;
    private LevelConstants levelConstants;

    protected PathFindSpecification specification;
    protected Point3d start;
    protected Vector3d direction;
    protected double time;

    public Maneuver(Point3d start, Vector3d direction, double time, PathFindSpecification specification) {
        this.start = start;
        this.direction = direction;
        this.time = time;
        this.specification = specification;

        direction.normalize();

        g = 0;
        h = Double.POSITIVE_INFINITY;
        pred = null;

        levelConstants = specification.getManeuverSpecification().getLevelConstants(start);
    }

    public Point3d getStart() {
        return start;
    }

    public Vector3d getStartDirection() {
        return direction;
    }

    public Point3d getEnd() {
        return end;
    }

    protected void setEnd(Point3d end) {
        this.end = end;
    }

    public Vector3d getEndDirection() {
        return endDirection;
    }

    protected void setEndDirection(Vector3d endDirection) {
        this.endDirection = endDirection;
    }

    public double getEndTime() {
        return getLength() * specification.getVelocity();
    }

    //TODO: remove
    protected void setStartDirection(Point3d start, Vector3d direction) {
        this.start = start;
        this.direction = direction;

        levelConstants = specification.getManeuverSpecification().getLevelConstants(start);

        direction.normalize();

        invalidate();
    }

    // TODO: remove
    protected void setStartDirection(Maneuver maneuver) {
        setStartDirection(maneuver.getEnd(), maneuver.getEndDirection());
    }

    public Maneuver getPredecessor() {
        return pred;
    }

    // TODO: remove (take predcessor from constructor?)
    protected void setPredecessor(Maneuver predecessor) {
        this.pred = predecessor;
    }

    protected void invalidate() {
        end = null;
        endDirection = null;
        hashCode = null;
    }

    public final Maneuver[] generateNeighbours() {
        Point3d end = new Point3d(getEnd());
        Vector3d endDirection = new Vector3d(getEndDirection());
        double endTime = getEndTime();
        ManeuverSpecification maneuverSpecification = specification.getManeuverSpecification();
        Maneuver neighbors[] = new Maneuver[maneuverSpecification.getExpandManeuvers().getManeuver().size()];

        int i = 0;
        for (ExpandManeuver expandManeuver : maneuverSpecification.getExpandManeuvers().getManeuver()) {
            Maneuver newManeuver = getManeuverByType(expandManeuver.getType(), end, endDirection, endTime);
            i = addNeighbor(neighbors, newManeuver, i);
        }

        return neighbors;
    }

    private int addNeighbor(Maneuver neighbors[], Maneuver maneuver, int n) {
        if (maneuver != null && maneuver.isValid() && !maneuver.isIntersectingFullZone()) {
            maneuver.setPredecessor(this);
            neighbors[n] = maneuver;
            n++;
        }

        return n;
    }

    public void computeGh() {
        computeG();

        ToEndManeuver tem;
        if (!(this instanceof ToEndManeuver)) {
            if (getEndDirection().z == 0.0) {
                tem = new ToEndManeuver(getEnd(), getEndDirection(), getEndTime(), specification);
                toEndManueverCache = tem;
                h = tem.getLength();
                return;
            } else {
                PitchToLevelManeuver ptlm = new PitchToLevelManeuver(getEnd(), getEndDirection(),
                        getEndTime(), specification);
                tem = new ToEndManeuver(ptlm.getEnd(), ptlm.getEndDirection(), ptlm.getEndTime(), specification);
                h = tem.getLength() + ptlm.getLength();
                return;
            }
        } else {
            h = 0;
            return;
        }
    }

    private void computeG() {
        if (pred.g == 0 && !(pred instanceof StartManeuver)) {
            pred.computeG();
        }
        g = pred.g + getLength();
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            Point3d end = getEnd();

            int bitSize = levelConstants.hashBitSize;
            hashCode = specification.getPositionHashCode(end, bitSize);
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this instanceof ToEndManeuver) {
            return false;
        }

        boolean position = this.getEnd().distanceSquared(((Maneuver)obj).getEnd()) < levelConstants.epsilonEqualsPosition;

        Point3d vec1 = new Point3d(this.getEndDirection());
        Point3d vec2 = new Point3d(((Maneuver)obj).getEndDirection());
        boolean direction = vec1.distanceSquared(vec2) < levelConstants.epsilonEqualsDirection;

        return position && direction;
    }

    public boolean isEnding() {
        return false;
    }

    public double getG() {
        return g;
    }

    public int compareTo(Maneuver o) {
        Maneuver other = o;
        if(other == this) {
            return 0;
        } else if (Math.abs((g + h) - (other.g + other.h)) < TOLERANCE) {
            return (hashCode() > other.hashCode())? 1 : -1;
        } else if(g + h < other.g + other.h) {
            return -1;
        } else {
            return 1;
        }
    }

    abstract public double getLength();

    abstract public boolean isIntersectingFullZone();

    abstract public boolean isValid();

    abstract public void accept(ManeuverVisitor visitor);

    private Maneuver getManeuverByType(ExpandManeuverType type, Point3d end, Vector3d endDirection, double endTime) {
        LevelConstants endLevelConstants = specification.getManeuverSpecification().getLevelConstants(end);

        switch (type) {
        case STRAIGHT:
            double length = endLevelConstants.straightLength;
            return new StraightManeuver(end, endDirection, endTime, length, specification);

        case STRAIGHT_BACKWARDS:
            length = endLevelConstants.straightLength;
            return new StraightManeuver(end, endDirection, endTime, -length, specification);

        case TURN_LEFT:
        case TURN_RIGHT:
            double radius = specification.getRadius();
            double steps = endLevelConstants.turnSteps;
            double stepAngle = 2.0*Math.PI/steps;

            switch(type) {
            case TURN_LEFT:
                return new TurnManeuver(end, endDirection, endTime, -radius, stepAngle, specification);
            case TURN_RIGHT:
                return new TurnManeuver(end, endDirection, endTime, radius, stepAngle, specification);
            default:
            }

        case PITCH_UP:
        case PITCH_DOWN:
            double pitchRadius = specification.getPitchRadius();
            double pitchSteps = 1;
            double stepPitchAngle = specification.getMaxPitch()/pitchSteps;

            switch(type) {
                case PITCH_UP:
                    return new PitchManeuver(end, endDirection, endTime, pitchRadius, stepPitchAngle, specification);
                case PITCH_DOWN:
                    return new PitchManeuver(end, endDirection, endTime, -pitchRadius, stepPitchAngle, specification);
                default:
            }

        case SPIRAL:
            return new SpiralManeuver(end, endDirection, endTime, 1, specification);

        case PITCH_TO_LEVEL:
            return new PitchToLevelManeuver(end, endDirection, endTime, specification);

        case TO_END_LEVEL:
            return new ToEndLevelManeuver(end, endDirection, endTime, specification);

        case TURN_TO_END:
            if (toEndManueverCache != null) {
                return toEndManueverCache.getTurnToEndManuever();
            }
            break;

        case TURN_PITCH_TO_END:
            if (toEndManueverCache != null) {
                return toEndManueverCache.getTurnPitchToEndManuever();
            }
            break;

        case TO_END:
            return toEndManueverCache;

        default:
            throw new RuntimeException("Not implemented ManeuverType type: " + type);
        }

        return null;
    }

}
