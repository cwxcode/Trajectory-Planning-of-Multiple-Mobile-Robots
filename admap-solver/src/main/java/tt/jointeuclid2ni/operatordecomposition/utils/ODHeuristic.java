package tt.jointeuclid2ni.operatordecomposition.utils;

import org.jgrapht.DirectedGraph;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.HeuristicProvider;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.jointeuclid2ni.operatordecomposition.ODNode;

public class ODHeuristic implements HeuristicToGoal<ODNode> {

    int size;
    HeuristicToGoal<Point>[] heuristics;

    public ODHeuristic(DirectedGraph<Point, Line>[] graphs, Point[] targets, HeuristicProvider<Point, Line> provider) {
        this.size = graphs.length;
        this.heuristics = getHeuristics(graphs, provider, targets);
    }
    
    @SuppressWarnings("unchecked")
	private HeuristicToGoal<Point>[] getHeuristics(DirectedGraph<Point, Line>[] graphs, HeuristicProvider<Point, Line> provider, Point[] targets) {
    	@SuppressWarnings("rawtypes")
		HeuristicToGoal[] heuristics = new HeuristicToGoal[size];
        for (int i = 0; i < size; i++) {
            heuristics[i] = provider.getHeuristicToGoal(graphs[i], targets[i]);
        }
        return heuristics;
    }

    @Override
    public double getCostToGoalEstimate(ODNode current) {
        Point[] values = current.getAgentPositions();

        double estimate = 0;
        for (int i = 0; i < size; i++) {
            estimate += heuristics[i].getCostToGoalEstimate(values[i]);
        }
        return estimate;
    }
}
