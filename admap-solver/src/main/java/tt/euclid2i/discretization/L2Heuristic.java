package tt.euclid2i.discretization;

import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Point;

public class L2Heuristic implements HeuristicToGoal<Point> {

	Point goal;

	public L2Heuristic(Point goal) {
		super();
		this.goal = goal;
	}

	@Override
	public double getCostToGoalEstimate(Point current) {
		return current.distance(goal);
	}

}
