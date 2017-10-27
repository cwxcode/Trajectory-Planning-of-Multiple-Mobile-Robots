package tt.euclid2d.trajectory;

import javax.vecmath.Point2i;
import tt.euclid2d.Point;

public class GoalArrivalObjective implements TrajectoryObjectiveFunctionAtPoint {

	Point2i goal;

	public GoalArrivalObjective(Point2i goal) {
		super();
		this.goal = goal;
	}

	@Override
	public double getCost(Point point, int time) {
		if (goal.equals(new tt.euclid2i.Point((int) Math.round(point.x), (int) Math.round(point.y)) )) {
			return 0;
		} else {
			return 1;
		}
	}

}
