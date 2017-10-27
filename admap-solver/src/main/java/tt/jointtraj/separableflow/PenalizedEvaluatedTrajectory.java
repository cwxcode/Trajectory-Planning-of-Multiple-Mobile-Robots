package tt.jointtraj.separableflow;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;

public class PenalizedEvaluatedTrajectory implements EvaluatedTrajectory {
	
	EvaluatedTrajectory trajectory;
	double penalty;

	public PenalizedEvaluatedTrajectory(EvaluatedTrajectory trajectory,
			double penalty) {
		super();
		this.trajectory = trajectory;
		this.penalty = penalty;
	}

	@Override
	public double getCost() {
		return trajectory.getCost();
	}

	@Override
	public int getMinTime() {
		return trajectory.getMinTime();
	}

	@Override
	public int getMaxTime() {
		return trajectory.getMaxTime();
	}

	@Override
	public Point get(int t) {
		return trajectory.get(t);
	}
	
	public double getPenalty() {
		return penalty;
	}
	
	public EvaluatedTrajectory getTrajectory() {
		return trajectory;
	}
}
