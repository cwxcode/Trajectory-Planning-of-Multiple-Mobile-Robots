package tt.jointeuclid2ni.solver.impl;


import org.jgrapht.DirectedGraph;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Line;
import tt.euclidtime3i.L1Heuristic;
import tt.euclidtime3i.L2Heuristic;
import tt.euclidtime3i.ShortestPathHeuristic;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.jointeuclid2ni.solver.HeuristicType;
import tt.jointtraj.solver.SearchResult;
import tt.jointtrajineuclidtime3i.solver.PrioritizedPlanningSolver;
import tt.util.NotImplementedException;

public class AlgorithmPP extends AbstractAlgorithm {

    public AlgorithmPP() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public SearchResult solveProblem() {
        final DirectedGraph<tt.euclid2i.Point, Line>[] spatial = new DirectedGraph[problem.nAgents()];
        HeuristicToGoal<Point>[] heuristics = new HeuristicToGoal[problem.nAgents()];
        DirectedGraph<Point, Straight>[] motions = new DirectedGraph[problem.nAgents()];

        for (int i = 0; i < problem.nAgents(); i++) {
        	
        	int edgeLengthBound;
        	if (params.timeStep == 1) {
        		edgeLengthBound = Integer.MAX_VALUE;
        	} else {
        		edgeLengthBound = (int) Math.floor(params.timeStep * problem.getMaxSpeed(i)); 
        	}
        	
            spatial[i] = preparePlanningGraphForAgent(i, edgeLengthBound, i == 0 /* show only first */);
            motions[i] = createMotionGraph(spatial[i], problem.getStart(i), problem.getTarget(i), problem.getMaxSpeed(i), params.maxTime);
            heuristics[i] = getHeuristic(params.heuristic, spatial[i], problem.getTarget(i), problem.getMaxSpeed(i), params.timeStep);
        }

        PrioritizedPlanningSolver solver = new PrioritizedPlanningSolver(problem.getStarts(), problem.getTargets(),
                motions, problem.getBodyRadiuses(), params.timeStep == 1 ? PrioritizedPlanningSolver.VARIABLE_TIMESTEP : params.timeStep, params.samplingInterval, params.maxTime);
        solver.setHeuristics(heuristics);
        
        return solver.solve(params.runtimeDeadlineMs - System.currentTimeMillis());
    }
}
