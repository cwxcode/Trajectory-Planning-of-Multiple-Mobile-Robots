package tt.jointtraj.separableflow;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.ConstantStepSegmentedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.euclidtime3i.discretization.softconstraints.SeparationConstraint;


public class AStarTrajectoryOptimizer implements TrajectoryOptimizer {

    final tt.euclidtime3i.Point start;
    final tt.euclidtime3i.Point target;
    final private HeuristicToGoal<tt.euclidtime3i.Point> heuristic;
    final DirectedGraph<tt.euclidtime3i.Point, Straight> agentMotions;
    private int constraintSamplingInterval;
	private int timeStep;

    public final static int TIMESTEP_VARIABLE = (-1);
    
    public AStarTrajectoryOptimizer(
    		DirectedGraph<tt.euclidtime3i.Point, Straight> agentMotions,
            Point start,
            Point target,
            HeuristicToGoal<tt.euclidtime3i.Point> heuristic,
            int timeStep,
            int constraintSamplingInterval
            ) {

        super();
        this.start = start;
        this.target = target;
        this.heuristic = heuristic;
        this.agentMotions = agentMotions;
        this.timeStep = timeStep;
        this.constraintSamplingInterval = constraintSamplingInterval;
    }

    @Override
    public PenalizedEvaluatedTrajectory getOptimalTrajectoryUnconstrained(double maxAllowedCost, long runtimeDeadlineMs) {
        return getOptimalTrajectoryConstrained(new PenaltyFunction[0], new Trajectory[0],  null, maxAllowedCost, runtimeDeadlineMs);
    }

    @Override
    public PenalizedEvaluatedTrajectory getOptimalTrajectoryConstrained(
            PenaltyFunction[] penaltyFunctions,
            Trajectory[] otherTrajectories,
            Trajectory initialTraj,
            double maxAllowedCost, long runtimeDeadlineMs) {

        PairwiseConstraint[] constraints = new PairwiseConstraint[otherTrajectories.length];
        for (int i = 0; i < constraints.length; i++) {
            constraints[i] = new SeparationConstraint(penaltyFunctions[i], constraintSamplingInterval);
        }

        StraightSegmentPenaltyFunction penalty = new PairwiseConstraintStraightSegmentPenalty(constraints, otherTrajectories);

        Graph<tt.euclidtime3i.Point, Straight> graphWithPenalty
            = new PenaltyFunctionWrapper(agentMotions, penalty);

        AStarShortestPathSimple<Point, Straight> alg
            = new AStarShortestPathSimple<Point, Straight>(graphWithPenalty, heuristic, start, new Goal<Point>() {
                    @Override
                    public boolean isGoal(Point current) {
                        if (current.getPosition().equals(target.getPosition())) {
                            if (current.getTime() == target.getTime()) {
                                return true;
                            }
                        }
                        return false;

                    }
                });

        GraphPath<Point, Straight> path = alg.findPathCostAndDeadlineLimit(maxAllowedCost, runtimeDeadlineMs);

        if (path == null)
            return null;
        SegmentedTrajectory traj;
        if (timeStep == TIMESTEP_VARIABLE) {
        	traj = new BasicSegmentedTrajectory(path.getEdgeList(), target.getTime(), path.getWeight());
        } else {
        	traj = new ConstantStepSegmentedTrajectory(path.getEdgeList(),timeStep, path.getWeight());
        }
        double penaltyOfTrajectory = computePenaltyForTrajectory(traj, penalty); 
        return new PenalizedEvaluatedTrajectory(traj, penaltyOfTrajectory);
    }
    
    public static double computePenaltyForTrajectory(SegmentedTrajectory traj, PenaltyFunction[] penaltyFunctions, Trajectory[] otherTrajectories, int constraintSamplingInterval) {
        PairwiseConstraint[] constraints = new PairwiseConstraint[otherTrajectories.length];
        for (int i = 0; i < constraints.length; i++) {
            constraints[i] = new SeparationConstraint(penaltyFunctions[i], constraintSamplingInterval);
        }

        StraightSegmentPenaltyFunction penalty = new PairwiseConstraintStraightSegmentPenalty(constraints, otherTrajectories);
    	return computePenaltyForTrajectory(traj, penalty);
    }
    
    public static double computePenaltyForTrajectory(SegmentedTrajectory traj, StraightSegmentPenaltyFunction penalty) {
        double sumPenalty = 0;
        
        for (Straight edge : traj.getSegments()) {
        	sumPenalty += penalty.getStraightTrajectoryCost(edge.getStart(), edge.getEnd());
        }
        return sumPenalty;
    }
    
}
