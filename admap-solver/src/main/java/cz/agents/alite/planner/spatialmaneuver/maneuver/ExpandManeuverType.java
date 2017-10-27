package cz.agents.alite.planner.spatialmaneuver.maneuver;

public enum ExpandManeuverType {

    STRAIGHT("Straight"),
    STRAIGHT_BACKWARDS("StraightBackwards"),
    TURN_RIGHT("TurnRight"),
    TURN_LEFT("TurnLeft"),
    PITCH_UP("PitchUp"),
    PITCH_DOWN("PitchDown"),
    SPIRAL("Spiral"),
    TURN_TO_END("TurnToEnd"),
    TURN_PITCH_TO_END("TurnPitchToEnd"),
    PITCH_TO_LEVEL("PitchToLevel"),
    TO_END_LEVEL("ToEndLevel"),
    TO_END("ToEnd");
    private final String value;

    ExpandManeuverType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExpandManeuverType fromValue(String v) {
        for (ExpandManeuverType c: ExpandManeuverType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
