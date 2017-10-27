package tt.euclid2d.trajectory;

import javax.vecmath.Point2d;

import tt.euclid2d.Point;
import tt.euclid2d.trajectory.TrajectoryObjectiveFunctionAtPoint;
import tt.euclid2i.Trajectory;

public class DesiredTrajectoryDifferenceObjective implements TrajectoryObjectiveFunctionAtPoint {

	Trajectory desiredTraj;
	double w;

	public DesiredTrajectoryDifferenceObjective(Trajectory traj, double w) {
		super();
		this.desiredTraj = traj;
		this.w = w;
	}

	@Override
	public double getCost(Point currentPoint, int time) {
		tt.euclid2i.Point trajPoint = desiredTraj.get(time);
		double distance = currentPoint.distance(new Point2d(trajPoint.x, trajPoint.y));;
		return w * distance;
	}

	public Trajectory getDesiredTrajectory() {
		return desiredTraj;
	}

}
