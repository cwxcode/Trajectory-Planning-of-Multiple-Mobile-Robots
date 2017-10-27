package tt.jointeuclid2ni.solver.impl;


import java.util.List;
import java.util.Random;

import org.jgrapht.DirectedGraph;
import org.jgrapht.util.HeuristicToGoal;
import org.jscience.mathematics.number.Complex;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.ShortestPathHeuristic;
import tt.euclidtime3i.discretization.softconstraints.BumpSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.LinearSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.SeparationConstraint;
import tt.jointeuclid2ni.probleminstance.AgentMission;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblems;
import tt.jointtraj.homotopyiterative.AStarTrajectoryOptimizerWithHomotopyConstriants;
import tt.jointtraj.homotopyiterative.IterativeHomotopyPlanner;
import tt.jointtraj.homotopyiterative.NewSolutionListener;
import tt.jointtraj.homotopyiterative.PlannerStrategy;
import tt.jointtraj.homotopyiterative.StochasticStrategy;
import tt.jointtraj.homotopyiterative.TrajectoryOptimizerWithHomotopyConstraints;
import tt.jointtraj.solver.SearchResult;
import tt.planner.homotopy.HomotopyUtils;
import tt.planner.homotopy.hvalue.HValueAnalyticIntegrator;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.util.Verbose;


public class AlgorithmIIHP extends AbstractAlgorithm {

    private static final double IIHP_MAX_PENALTY = 10;

    public AlgorithmIIHP() {
        super();
    }

    @Override
    public SearchResult solveProblem() {
        Verbose.setVerbose(params.verbose);
        Random random = new Random(1000);

        DirectedGraph<Point, Line> graph[] = new DirectedGraph[problem.nAgents()];
        
        for (int i = 0; i < problem.nAgents(); i++) {
        	
        	int edgeLengthBound;
        	if (params.timeStep == 1) {
        		edgeLengthBound = Integer.MAX_VALUE;
        	} else {
        		edgeLengthBound = (int) Math.floor(params.timeStep * problem.getMaxSpeed(i)); 
        	}
        	
            graph[i] = preparePlanningGraphForAgent(i, edgeLengthBound, true);
        }

        List<Complex> freeSpaceSamples = HomotopyUtils.sampleFreeSpace(problem.getObstacles(), problem.getEnvironment().getBoundary().getBoundingBox(), random);
        List<Complex> obstacleSamples = HomotopyUtils.sampleObstacles(problem.getObstacles(), random);
        HValueIntegrator integrator = new HValueAnalyticIntegrator(freeSpaceSamples, obstacleSamples);

        // Compute heuristic based on optimal policies on the spatial graph
        HeuristicToGoal<tt.euclidtime3i.Point>[] heuristics = new HeuristicToGoal[problem.nAgents()];

        for (int i = 0; i < problem.nAgents(); i++) {
            heuristics[i] = new ShortestPathHeuristic(graph[i], problem.getTarget(i));
        }

        TrajectoryOptimizerWithHomotopyConstraints[] trajectoryOptimizers = new TrajectoryOptimizerWithHomotopyConstraints[problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
            trajectoryOptimizers[i] = new AStarTrajectoryOptimizerWithHomotopyConstriants(graph[i],
                    new tt.euclidtime3i.Point(problem.getStart(i), 0), new tt.euclidtime3i.Point(problem.getTarget(i), params.maxTime),
                    params.maxSpeed, params.waitMoveDuration, heuristics[i], integrator);
        }


        // Create constraints
        PairwiseConstraint[][] constraints = new PairwiseConstraint[problem.nAgents()][problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
            for (int j = 0; j < problem.nAgents(); j++) {
                if (i != j) {
                    constraints[i][j] = new SeparationConstraint(new BumpSeparationPenaltyFunction(IIHP_MAX_PENALTY, problem.getBodyRadius(i) + problem.getBodyRadius(j), 1.0),
                            10);
                }

            }
        }

        AgentMission[] missions = EarliestArrivalProblems.agentMissions(problem);

        // If there are no obstacles in the env., then there is only one homotopy class and branch would search for the next homotopy class that does not exist
        // TODO: It may happen that there is only a single homotopy class even if there are obstacles -- consider agent locked-in a box...
        boolean branchAllowed = !problem.getObstacles().isEmpty();
        PlannerStrategy strategy = new StochasticStrategy(3, (int) (params.runtimeDeadlineMs - System.currentTimeMillis()), 1, branchAllowed);

        final IterativeHomotopyPlanner planner = new IterativeHomotopyPlanner(trajectoryOptimizers, constraints,
                strategy, new NewSolutionListener() {
            @Override
            public void notifyNewSolution(EvaluatedTrajectory[] trajs) {
                if (params.printSummary) {
                    printSummary(trajs);
                }
            }
        });

        planner.solve();

        return new SearchResult(planner.getBestSolution(), true);
    }
}
