package tt.euclidtime3i;

import org.jgrapht.util.HeuristicToGoal;

public class L2Heuristic implements HeuristicToGoal<Point> {

    private tt.euclid2i.Point target;

	public L2Heuristic(tt.euclid2i.Point target) {
    	this.target = target;
    }

    @Override
    public double getCostToGoalEstimate(Point current) {
        return current.getPosition().distance(target);
    }
}
