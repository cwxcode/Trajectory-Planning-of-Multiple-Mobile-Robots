package tt.jointtraj.separableflow;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;

public class PairwiseConstraintStraightSegmentPenalty implements StraightSegmentPenaltyFunction {

	private Trajectory[] otherTrajs;
	private PairwiseConstraint[] constraints;

	public PairwiseConstraintStraightSegmentPenalty(PairwiseConstraint[] constraints,
			Trajectory[] otherTrajs) {
		super();
		this.constraints = constraints;
		this.otherTrajs = otherTrajs;
	}

	@Override
	public double getStraightTrajectoryCost(Point from, Point to) {
		 double penalty = 0;

	        Trajectory edgeTrajectory = new tt.euclidtime3i.trajectory.LinearTrajectory(from, to, 0);

	        for (int i = 0; i < otherTrajs.length; i++) {
	            double constraintPenalty = constraints[i].getPenalty(edgeTrajectory, otherTrajs[i]) ;
	            if (constraintPenalty > 0) //Infinity times 0 is NaN
	            {
	                penalty += constraintPenalty;
	            }
	        }

	        return penalty;
	}

}
