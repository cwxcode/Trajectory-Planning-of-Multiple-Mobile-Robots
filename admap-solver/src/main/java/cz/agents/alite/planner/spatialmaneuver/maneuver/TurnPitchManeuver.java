package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


/**
 * @author Antonin Komenda
 *
 */
public class TurnPitchManeuver extends Maneuver {

    private TurnManeuver turn;
    private PitchManeuver pitch;

    /**
     * @param start
     * @param direction
     * @param zone
     */
    public TurnPitchManeuver(TurnManeuver turn, PitchManeuver pitch, PathFindSpecification specification) {
        super(turn.start, turn.direction, turn.time, specification);

        this.turn = turn;
        this.pitch = pitch;
    }

    @Override
    public Point3d getEnd() {
        return pitch.getEnd();
    }

    @Override
    public Vector3d getEndDirection() {
        return pitch.getEndDirection();
    }

    @Override
    public double getLength() {
        return turn.getLength() + pitch.getLength();
    }

    public TurnManeuver getTurn() {
        return turn;
    }

    public PitchManeuver getPitch() {
        return pitch;
    }

    @Override
    public void setPredecessor(Maneuver predecessor) {
        super.setPredecessor(predecessor);

        turn.setPredecessor(predecessor);
        predecessor = turn;
        pitch.setPredecessor(predecessor);
        predecessor = pitch;
    }

    @Override
    public boolean isIntersectingFullZone() {
        return turn.isIntersectingFullZone() || pitch.isIntersectingFullZone();
    }

    @Override
    public boolean isValid() {
        return turn.isValid() && pitch.isValid();
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
