package tt.euclid2d.trajectory;

import tt.euclid2d.Point;

public class SoftGoalArrivalObjective implements TrajectoryObjectiveFunctionAtPoint {

	tt.euclid2i.Point goal;
	double bumpRadius;
	double slope;

	public SoftGoalArrivalObjective(tt.euclid2i.Point goal, double bumpRadius,
			double slope) {
		super();
		this.goal = goal;
		this.bumpRadius = bumpRadius;
		this.slope = slope;
	}

	@Override
	public double getCost(Point point, int time) {
		double goalDistance = new Point(goal.x, goal.y).distance(point);
		return softEquality(goalDistance, bumpRadius, slope);
	}

	static double softEquality(double x, double eps, double slope) {
		return (slope * Math.abs(x)) + 1 - bump(x, eps, 1, 1);
	}

	static double bump(double x, double dmax, double pmax, double s) {
		if (Math.abs(x) < dmax) {
			return (pmax / Math.exp(-s))
					* Math.exp((-s) / (1 - Math.pow(x / dmax, 2)));
		} else {
			return 0;
		}
	}

}
