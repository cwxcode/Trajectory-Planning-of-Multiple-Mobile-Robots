package tt.euclid2i.discretization;

import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Point;

public class L1Heuristic implements HeuristicToGoal<Point> {

	Point goal;

	public L1Heuristic(Point goal) {
		super();
		this.goal = goal;
	}

	@Override
	public double getCostToGoalEstimate(Point current) {
		return current.distanceL1(goal);
	}

}
