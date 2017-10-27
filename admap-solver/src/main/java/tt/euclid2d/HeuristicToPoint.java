package tt.euclid2d;

import org.jgrapht.util.HeuristicToGoal;

public class HeuristicToPoint implements HeuristicToGoal<Point> {

    private final Point target;

    public HeuristicToPoint(Point target) {
        this.target = target;
    }

    @Override
    public double getCostToGoalEstimate(Point current) {
        return current.distance(target);
    }
}
