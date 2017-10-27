package cz.agents.alite.planner.spatialmaneuver.maneuver;


public class ExpandManeuver {

    private ExpandManeuverType type;

    public ExpandManeuver(ExpandManeuverType type) {
        this.type = type;
    }

    public ExpandManeuverType getType() {
        return type;
    }

    public void setType(ExpandManeuverType value) {
        this.type = value;
    }

}
