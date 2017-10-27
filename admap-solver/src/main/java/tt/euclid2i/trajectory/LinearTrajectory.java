package tt.euclid2i.trajectory;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.Arrays;

public class LinearTrajectory extends BasicSegmentedTrajectory {

    public LinearTrajectory(int startTime, Point startWaypoint, Point endWaypoint, float speed, int duration, double cost) {

        super(Arrays.asList(new Straight(new tt.euclidtime3i.Point(startWaypoint, startTime),
                new tt.euclidtime3i.Point(endWaypoint, startTime + (int) (startWaypoint.distance(endWaypoint)/speed)))),
                duration,
                cost);
    }

    public LinearTrajectory(int startTime, Line line, int speed, int duration, double cost) {

        this(startTime, line.getStart(), line.getEnd(), speed, duration, cost);
    }
}
