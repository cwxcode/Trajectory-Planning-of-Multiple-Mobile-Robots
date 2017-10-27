package tt.euclid2i.trajectory;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class EvaluatedTrajectoryWrapper implements EvaluatedTrajectory {

	Trajectory traj;
	double cost;

	public EvaluatedTrajectoryWrapper(Trajectory traj, double cost) {
		super();
		this.traj = traj;
		this.cost = cost;
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public int getMinTime() {
		return traj.getMinTime();
	}

	@Override
	public int getMaxTime() {
		return traj.getMaxTime();
	}

	@Override
	public Point get(int t) {
		return traj.get(t);
	}

}
