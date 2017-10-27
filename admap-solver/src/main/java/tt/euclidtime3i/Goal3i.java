package tt.euclidtime3i;

import org.jgrapht.util.Goal;

public class Goal3i implements Goal<Point> {

    private tt.euclid2i.Point point;

    public Goal3i(tt.euclid2i.Point point) {
        this.point = point;
    }

    @Override
    public boolean isGoal(Point current) {
        return point.equals(current.getPosition());
    }
}
