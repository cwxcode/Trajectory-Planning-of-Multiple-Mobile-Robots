package cz.agents.alite.planner.spatialmaneuver.maneuver;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;


public class PitchToLevelManeuver extends PitchManeuver {

    public PitchToLevelManeuver(Point3d start, Vector3d direction, double time, PathFindSpecification specification) {
        super(start, direction, time, specification.getPitchRadius(), 0.0, specification);

        if (direction.z > 0) {
            setRadius(-specification.getPitchRadius());
        }

        double pitchAngle = direction.angle(new Vector3d(direction.x, direction.y, 0.0));
        setAngle(pitchAngle);
    }

    @Override
    public Vector3d getEndDirection() {
        Vector3d dir = super.getEndDirection();
        dir.z = 0.0;
        return dir;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
