package tt.euclid2i.trajectory;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class LineTrajectory implements Trajectory {

    private int maxTime;
    private Line line;

    public LineTrajectory(Line line, int maxTime) {
        this.line = line;
        this.maxTime = maxTime;
    }

    @Override
    public int getMinTime() {
        return 0;
    }

    @Override
    public int getMaxTime() {
        return maxTime;
    }

    @Override
    public Point get(int t) {
        return line.interpolate(((double) t) / maxTime);
    }
}
