package tt.jointtraj.separableflow;

import tt.euclidtime3i.Point;

public class NoPenalty implements StraightSegmentPenaltyFunction {

	@Override
	public double getStraightTrajectoryCost(Point from, Point to) {
		return 0;
	}

}
