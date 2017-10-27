package tt.jointeuclid2ni.solver.impl;


import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedGraphDelegator;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.L1Heuristic;
import tt.euclidtime3i.L2Heuristic;
import tt.euclidtime3i.ShortestPathHeuristic;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.softconstraints.BumpSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.LinearSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.jointeuclid2ni.solver.HeuristicType;
import tt.jointtraj.separableflow.AStarTrajectoryOptimizer;
import tt.jointtraj.separableflow.TrajectoryOptimizer;
import tt.jointtraj.solver.SearchResult;
import tt.util.Args;
import tt.util.NotImplementedException;
import tt.util.Verbose;


public abstract class AbstractDPMBasedAlgorithm extends AbstractAlgorithm {
	
	enum PenaltyType {BUMP, LINEAR}

    private static final double MAX_PENALTY = 1;
    private static final double MIN_PENALTY = 0.1;
    private PenaltyType penaltyType;

    public AbstractDPMBasedAlgorithm() {
        this(new String[0]);
    }

    public AbstractDPMBasedAlgorithm(String[] args) {
        super();
        penaltyType = PenaltyType.valueOf(Args.getArgumentValue(args, "-penaltytype", false, "BUMP"));
    }

    @Override
    public SearchResult solveProblem() {
        Verbose.setVerbose(params.verbose);

        // Compute heuristic based on optimal policies on the spatial graph
        HeuristicToGoal<tt.euclidtime3i.Point>[] heuristics = new HeuristicToGoal[problem.nAgents()];
        // Create trajectory optimizers
        TrajectoryOptimizer[] trajectoryOptimizers = new TrajectoryOptimizer[problem.nAgents()];
        
        for (int i = 0; i < problem.nAgents(); i++) {
        	
        	int edgeLengthBound;
        	if (params.timeStep == 1) {
        		edgeLengthBound = Integer.MAX_VALUE;
        	} else {
        		edgeLengthBound = (int) Math.floor(params.timeStep * problem.getMaxSpeed(i)); 
        	}
        	
        	DirectedGraph<Point, Line> spatialGraph = preparePlanningGraphForAgent(i, edgeLengthBound, i == 0 /* show only first graph*/);
        	
        	heuristics[i] = getHeuristic(params.heuristic, spatialGraph, problem.getTarget(i), problem.getMaxSpeed(i), params.timeStep);
        	
        	DirectedGraph<tt.euclidtime3i.Point, Straight>  motions 
        		= createMotionGraph(spatialGraph, problem.getStart(i), problem.getTarget(i), params.maxSpeed, params.maxTime);
        	
        	trajectoryOptimizers[i] = new AStarTrajectoryOptimizer(motions,
		        new tt.euclidtime3i.Point(problem.getStart(i),0),
		        new tt.euclidtime3i.Point(problem.getTarget(i),  params.maxTime),		        
		        heuristics[i],
		        params.timeStep == 1 ? AStarTrajectoryOptimizer.TIMESTEP_VARIABLE : params.timeStep,
		        params.samplingInterval);
        }

        // Create constraints
        PenaltyFunction[][] softSeparationFunctions = new PenaltyFunction[problem.nAgents()][problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
            for (int j = 0; j < problem.nAgents(); j++) {
                if (i != j) {
                	if (penaltyType == PenaltyType.BUMP) {
                		softSeparationFunctions[i][j] = new BumpSeparationPenaltyFunction(MAX_PENALTY, problem.getBodyRadius(i) + problem.getBodyRadius(j)+1, 1, MIN_PENALTY);
                	} else if (penaltyType == PenaltyType.LINEAR) {
                		softSeparationFunctions[i][j] = new LinearSeparationPenaltyFunction(MAX_PENALTY, problem.getBodyRadius(i) + problem.getBodyRadius(j)+1, MIN_PENALTY);
                	}
                }
            }
        }

        EvaluatedTrajectory[] trajs = solveCore(trajectoryOptimizers, softSeparationFunctions);        
        boolean finished = System.currentTimeMillis() < params.runtimeDeadlineMs;        
        return new SearchResult(trajs, finished);

    }

	abstract protected EvaluatedTrajectory[] solveCore(
            TrajectoryOptimizer[] trajectoryOptimizers,
            PenaltyFunction[][] penaltyFunctions);
}
