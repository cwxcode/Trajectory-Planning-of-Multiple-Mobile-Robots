package tt.euclidtime3i.sipp;

import org.jgrapht.util.HeuristicToGoal;
import tt.euclid2i.Point;

public class SippHeuristic implements HeuristicToGoal<SippNode> {

    private HeuristicToGoal<Point> heuristic;
    private double speed;

    public SippHeuristic(HeuristicToGoal<Point> heuristic, double speed) {
        this.heuristic = heuristic;
        this.speed = speed;
    }

    @Override
    public double getCostToGoalEstimate(SippNode current) {
        double distanceToGoal = heuristic.getCostToGoalEstimate(current.getPoint());
        return distanceToGoal/speed;
    }

}
