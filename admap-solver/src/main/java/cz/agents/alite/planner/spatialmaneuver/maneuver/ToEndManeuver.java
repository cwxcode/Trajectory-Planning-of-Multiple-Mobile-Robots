package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


public class ToEndManeuver extends ToManeuver {

    public ToEndManeuver(Point3d start, Vector3d direction, double time, PathFindSpecification specification) {
        super(start, direction, time, specification.getTo(), specification.getToDirection(), specification);
    }

    @Override
    public boolean isEnding() {
        return true;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
