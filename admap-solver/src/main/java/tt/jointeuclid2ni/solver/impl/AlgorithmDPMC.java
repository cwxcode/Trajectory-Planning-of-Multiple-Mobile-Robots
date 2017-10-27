package tt.jointeuclid2ni.solver.impl;


import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.HeuristicToGoal;

import cz.agents.alite.vis.VisManager;

import tt.discrete.util.Trajectories;
import tt.euclid2d.trajectory.DesiredTrajectoryDifferenceObjective;
import tt.euclid2d.trajectory.GoalArrivalObjective;
import tt.euclid2d.trajectory.SoftGoalArrivalObjective;
import tt.euclid2d.trajectory.TrajectoryObjectiveFunctionAtPoint;
import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.VisibilityGraph;
import tt.euclid2i.trajectory.LineSegmentsConstantSpeedTrajectory;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.discretization.softconstraints.BumpSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.LinearSeparationPenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.jointeuclid2ni.solver.ObjectiveType;
import tt.jointtraj.separableflow.GradientOptimizer;
import tt.jointtraj.separableflow.PenaltyOptimizer;
import tt.jointtraj.separableflow.TrajectoryOptimizer;
import tt.jointtraj.solver.SearchResult;
import tt.util.AgentColors;
import tt.util.Args;
import tt.util.Verbose;
import tt.vis.FastTrajectoriesLayer;
import tt.vis.FastTrajectoriesLayer.ColorProvider;
import tt.vis.FastTrajectoriesLayer.TrajectoriesProvider;

public class AlgorithmDPMC extends AbstractAlgorithm {

    private static final double MAX_PENALTY = 1;
    private static final int DESCENT_MAX_ITER = 100;
    private static final double DESCENT_STEP = 1;
    private static final double COST_EPS = 0.01;

    private int k = 50;

    public AlgorithmDPMC(String[] args) {
        k = Integer.parseInt(Args.getArgumentValue(args, "-k", true));
    }

    @Override
    public SearchResult solveProblem() {
        Verbose.setVerbose(params.verbose);

        final Trajectory[] desiredTrajectories = findDesiredTrajectories(problem.getStarts(), problem.getTargets(), problem.getBodyRadiuses(), problem.getObstacles(),
        		params.maxSpeed, params.maxTime);

        VisManager.registerLayer(FastTrajectoriesLayer.create(new TrajectoriesProvider() {
			@Override
			public Trajectory[] getTrajectories() {
				return desiredTrajectories;
			}
		}, new ColorProvider() {

			@Override
			public Color getColor(int i) {
				return AgentColors.getColorForAgent(i).brighter().brighter();
			}
		}, 1, 50));


        // Create trajectory optimizers
        TrajectoryOptimizer[] trajectoryOptimizers = new TrajectoryOptimizer[problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {

        	TrajectoryObjectiveFunctionAtPoint objective = null;
        	if (params.objective == ObjectiveType.TRAJECTORY) {
        		objective = new DesiredTrajectoryDifferenceObjective(desiredTrajectories[i], 0.01 * 1/params.gridStep);
        	}else if (params.objective == ObjectiveType.ARRIVAL) {
        		objective = new SoftGoalArrivalObjective(problem.getTarget(i), params.gridStep/2, 0);
        	} else {
        		assert false;
        	}

            trajectoryOptimizers[i] = new GradientOptimizer(problem.getStart(i), problem.getTarget(i), objective, problem.getMaxSpeeds()[i], params.maxTime,
            		params.gridStep, DESCENT_STEP, COST_EPS, DESCENT_MAX_ITER, false);
        }

        // Create constraints
        PenaltyFunction[][] softSeparationFunctions = new PenaltyFunction[problem.nAgents()][problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
            for (int j = 0; j < problem.nAgents(); j++) {
                if (i != j) {
                    softSeparationFunctions[i][j] = new LinearSeparationPenaltyFunction(MAX_PENALTY, problem.getBodyRadius(i) + problem.getBodyRadius(j));
                }
            }
        }

        EvaluatedTrajectory[] trajs = PenaltyOptimizer.solve(
                trajectoryOptimizers, softSeparationFunctions, k, Double.POSITIVE_INFINITY,
                params.samplingInterval, params.runtimeDeadlineMs, params.verbose, false);

        return new SearchResult(trajs, true);
    }

	private Trajectory[] findDesiredTrajectories(Point[] starts,
			Point[] targets, int[] bodyRadiuses, Collection<Region> obstacles, float speed, int maxtime) {

		Trajectory[] optimalTrajs = new Trajectory[starts.length];
		for (int i=0; i<starts.length; i++) {
			optimalTrajs[i] = findShortestPath(starts[i], targets[i], bodyRadiuses[i], obstacles, speed, maxtime);
		}

		return optimalTrajs;
	}

	private Trajectory findShortestPath(Point start, final Point target, int radius,
			Collection<Region> obstacles, float speed, int maxTime) {

		Collection<Point> significantPoints = Arrays.asList(new Point[] {start, target});
		WeightedGraph<Point, Line> visGraph = VisibilityGraph.createVisibilityGraph(obstacles, radius, significantPoints);

		GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(visGraph, new HeuristicToGoal<Point>() {

			@Override
			public double getCostToGoalEstimate(Point current) {
				return current.distance(target);
			}
		}, start, target);

		if (path != null) {
			Trajectory traj = new LineSegmentsConstantSpeedTrajectory<Point, Line>(0, path, speed, maxTime);
			return traj;
		} else {
			throw new RuntimeException("No unconstrained trajectory found on the visibility graph! ");
		}


	}




}
