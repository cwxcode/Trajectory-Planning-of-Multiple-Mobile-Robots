package tt.euclidtime3i.sipprrts;

import tt.euclid2i.Point;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public class RRTMissionImpl implements RRTMission {

    private Point start;
    private Point target;
    private int bodyRadius;
    private int targetRadius;
    private int speed;

    public RRTMissionImpl(Point start, Point target, int bodyRadius, int targetRadius, int speed) {
        this.start = start;
        this.target = target;
        this.bodyRadius = bodyRadius;
        this.targetRadius = targetRadius;
        this.speed = speed;
    }

    @Override
    public Point getStart() {
        return start;
    }

    @Override
    public Point getTarget() {
        return target;
    }

    @Override
    public int getBodyRadius() {
        return bodyRadius;
    }

    @Override
    public int getTargetRadius() {
        return targetRadius;
    }

    @Override
    public int getMaxSpeed() {
        return speed;
    }
}
