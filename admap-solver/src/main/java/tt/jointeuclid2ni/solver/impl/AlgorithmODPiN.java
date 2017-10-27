package tt.jointeuclid2ni.solver.impl;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.jointtraj.solver.SearchResult;
import tt.jointtrajineuclidtime3i.solver.ODSolver;

import java.util.LinkedList;

public class AlgorithmODPiN extends AbstractAlgorithm {

    public AlgorithmODPiN() {
        super();
    }

    @Override
    public SearchResult solveProblem() {
        DirectedGraph<Point, Straight>[] motions = new DirectedGraph[problem.nAgents()];
        
        for (int i = 0; i < problem.nAgents(); i++) {
        	
        	int edgeLengthBound;
        	if (params.timeStep == 1) {
        		edgeLengthBound = Integer.MAX_VALUE;
        	} else {
        		edgeLengthBound = (int) Math.floor(params.timeStep * problem.getMaxSpeed(i)); 
        	}
        	
        	DirectedGraph<tt.euclid2i.Point, Line> spatialGraph = preparePlanningGraphForAgent(i, edgeLengthBound, true);
            motions[i] = createMotionGraph(spatialGraph, 
            		problem.getStart(i), problem.getTarget(i),
                    problem.getMaxSpeed(i), params.maxTime);
        }

        ODSolver solver = new ODSolver(problem.getStarts(), problem.getTargets(), motions,
                problem.getBodyRadiuses(), params.maxTime, Double.MAX_VALUE, new LinkedList<Trajectory>());

        return solver.solve(params.runtimeDeadlineMs - System.currentTimeMillis());
    }

}
