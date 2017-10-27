package tt.euclidtime3i.util;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Path;
import tt.euclidtime3i.Point;

public class TrajectoryAsPathWrapper implements Path {

    Trajectory traj;

    public TrajectoryAsPathWrapper(Trajectory traj) {
        super();
        this.traj = traj;
    }

    @Override
    public Point get(double alpha) {
        int timeSpan = traj.getMaxTime()-traj.getMinTime();
        tt.euclid2i.Point point = traj.get(traj.getMinTime() + (int) Math.round(alpha*timeSpan));
        return new Point(point.x, point.y, (int) Math.round(traj.getMinTime() + alpha*timeSpan));
    }

}
