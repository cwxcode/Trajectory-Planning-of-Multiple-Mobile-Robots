package tt.jointtraj.util;

import tt.euclid2i.EvaluatedTrajectory;

import java.io.PrintWriter;

public class Util {
    public static void exportTrajectories(EvaluatedTrajectory[] trajectories, PrintWriter writer, int samplingInterval, int maxTime) {

        for (int i = 0; i < trajectories.length; i++) {
            tt.euclid2i.util.Util.exportTrajectory(trajectories[i], writer, samplingInterval, maxTime);
            writer.write('\n');
        }
        writer.flush();
    }

    public static void exportSummary(EvaluatedTrajectory[] trajectories, PrintWriter writer) {
        writer.format("%.2f\n", getSumCost(trajectories));
        writer.flush();
    }


    public static double getSumCost(EvaluatedTrajectory[] trajectories) {
        double cost = 0;
        for (int i = 0; i < trajectories.length; i++) {
            cost += trajectories[i].getCost();
        }
        return cost;
    }

    public static int[] radiusesToSeparations(int bodyRadius, int[] otherRadiuses) {
        int[] separations = new int[otherRadiuses.length];
        for (int i = 0; i < separations.length; i++) {
            separations[i] = otherRadiuses[i] + bodyRadius;
        }
        return separations;
    }
}
