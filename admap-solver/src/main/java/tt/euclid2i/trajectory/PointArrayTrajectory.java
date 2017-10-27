package tt.euclid2i.trajectory;

import javax.vecmath.Point2d;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;

public class PointArrayTrajectory implements EvaluatedTrajectory {

	Point2d[] points;
	int samplingInterval;
	double cost;

	public PointArrayTrajectory(Point2d[] points, int samplingInterval,
			double cost) {
		super();
		this.points = points;
		this.samplingInterval = samplingInterval;
		this.cost = cost;
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public int getMinTime() {
		return 0;
	}

	@Override
	public int getMaxTime() {
		return (points.length-1)*samplingInterval;
	}

	@Override
	public Point get(int t) {
		int i = t/samplingInterval;
		return new Point((int) Math.round(points[i].x), (int) Math.round(points[i].y));
	}

}
