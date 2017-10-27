package tt.jointtraj.separableflow;

public interface StraightSegmentPenaltyFunction {
	double getStraightTrajectoryCost(tt.euclidtime3i.Point from, tt.euclidtime3i.Point to);
}
