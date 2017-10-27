package tt.euclid2d.trajectory;

public interface TrajectoryObjectiveFunctionAtPoint {
	double getCost(tt.euclid2d.Point point, int time);
}
