package tt.euclidtime3i;

import org.jgrapht.util.HeuristicToGoal;

public class L1Heuristic implements HeuristicToGoal<Point> {

    private tt.euclid2i.Point target;

	public L1Heuristic(tt.euclid2i.Point target) {
    	this.target = target;
    }

    @Override
    public double getCostToGoalEstimate(Point current) {
        return current.getPosition().distanceL1(target);
    }
}
