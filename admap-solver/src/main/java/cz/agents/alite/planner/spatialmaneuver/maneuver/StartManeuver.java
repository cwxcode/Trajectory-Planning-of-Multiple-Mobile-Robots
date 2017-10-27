package cz.agents.alite.planner.spatialmaneuver.maneuver;

import cz.agents.alite.planner.spatialmaneuver.PathFindSpecification;

public class StartManeuver extends StraightManeuver {

    private boolean isEnding = false;

    public StartManeuver(PathFindSpecification specification) {
        super(specification.getFrom(), specification.getFromDirection(), 0, 0, specification);

        setPredecessor(this);
        computeGh();

        if (specification.getTo().epsilonEquals(start, 1)
                && specification.getToDirection().epsilonEquals(direction, 1)) {
            isEnding = true;
        }
    }

    @Override
    public boolean isEnding() {
        return isEnding;
    }

    @Override
    public void accept(ManeuverVisitor visitor) {
        visitor.visit(this);
    }

}
