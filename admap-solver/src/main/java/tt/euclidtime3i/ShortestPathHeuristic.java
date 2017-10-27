package tt.euclidtime3i;

import org.jgrapht.Graph;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.PerfectHeuristic;
import tt.euclid2i.Line;

public class ShortestPathHeuristic implements HeuristicToGoal<Point> {

    private HeuristicToGoal<tt.euclid2i.Point> heuristics;

    public ShortestPathHeuristic(Graph<tt.euclid2i.Point, Line> graph, tt.euclid2i.Point target) {
        this.heuristics = new PerfectHeuristic<tt.euclid2i.Point, Line>(graph, target);
    }

    @Override
    public double getCostToGoalEstimate(Point current) {
        return heuristics.getCostToGoalEstimate(current.getPosition());
    }
}
