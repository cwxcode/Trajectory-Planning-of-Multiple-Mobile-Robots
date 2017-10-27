package tt.jointtraj.separableflow;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;

public interface TrajectoryOptimizer {

	PenalizedEvaluatedTrajectory getOptimalTrajectoryUnconstrained(double maxAllowedCost, long runtimeDeadlineMs);
	PenalizedEvaluatedTrajectory getOptimalTrajectoryConstrained(PenaltyFunction[] penaltyFunctions,
			Trajectory[] otherTrajectories, Trajectory initialTrajectory, double maxAllowedCost, long runtimeDeadlineMs);

}
