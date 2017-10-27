package tt.jointtraj.homotopyiterative;

import tt.euclid2i.EvaluatedTrajectory;

public class TrajectoryUtils {

    public static double cost(EvaluatedTrajectory[] trajectories) {
        double cost = 0;

        for (EvaluatedTrajectory trajectory : trajectories) {
            cost += trajectory.getCost();
        }

        return cost;
    }
}
