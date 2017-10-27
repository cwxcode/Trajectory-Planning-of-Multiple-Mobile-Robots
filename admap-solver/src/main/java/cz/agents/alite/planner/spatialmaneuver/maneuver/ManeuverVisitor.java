package cz.agents.alite.planner.spatialmaneuver.maneuver;

public interface ManeuverVisitor {

    public void visit(PitchManeuver maneuver);

    public void visit(PitchToLevelManeuver maneuver);

    public void visit(SmoothManeuver maneuver);

    public void visit(SpiralManeuver maneuver);

    public void visit(StartManeuver maneuver);

    public void visit(StraightManeuver maneuver);

    public void visit(ToEndLevelManeuver maneuver);

    public void visit(ToEndManeuver maneuver);

    public void visit(ToManeuver maneuver);

    public void visit(TurnManeuver maneuver);

    public void visit(TurnPitchManeuver maneuver);

}
