package tt.jointtraj.homotopyiterative;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.jscience.mathematics.number.Complex;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.FreeOnTargetWaitExtension;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.softconstraints.PenaltyFunction;
import tt.jointtraj.separableflow.NoPenalty;
import tt.jointtraj.separableflow.PenalizedEvaluatedTrajectory;
import tt.jointtraj.separableflow.PenaltyFunctionWrapper;
import tt.jointtraj.separableflow.StraightSegmentPenaltyFunction;
import tt.planner.homotopy.HEdge;
import tt.planner.homotopy.HNode;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.planner.homotopy.hvalue.HValuePolicy;

public class AStarTrajectoryOptimizerWithHomotopyConstriants implements TrajectoryOptimizerWithHomotopyConstraints {

    final tt.euclidtime3i.Point start;
    final tt.euclidtime3i.Point target;
    final float vmax;
    private HeuristicToGoal<tt.euclidtime3i.Point> heuristic;
    final DirectedGraph<tt.euclidtime3i.Point, Straight> agentMotions;
	private HValuePolicy hPolicy;
	private DirectedGraph<tt.euclid2i.Point, Line> spatialGraph;
	private HValueIntegrator integrator;
	private int waitMoveDuration;
	private Complex lastHValue = null;



    public AStarTrajectoryOptimizerWithHomotopyConstriants(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph,
            Point start,
            Point target,
            float speed,
            int waitDuration,
            HeuristicToGoal<tt.euclidtime3i.Point> heuristic,
            HValueIntegrator integrator
            ) {

        super();
        this.start = start;
        this.target = target;
        this.vmax = speed;
        this.waitMoveDuration = waitDuration;
        this.integrator = integrator;
        this.heuristic = heuristic;
        this.spatialGraph = spatialGraph;

        this.agentMotions = new FreeOnTargetWaitExtension(
        		new ConstantSpeedTimeExtension(spatialGraph, target.getTime(), new float[]{vmax},  new LinkedList<Region>(), waitMoveDuration), target.getPosition());
    }

    @Override
   	public PenalizedEvaluatedTrajectory getOptimalTrajectoryUnconstrained (double maxAllowedCost, long runtimeDeadlineMs) {
    	return getOptimalTrajectoryConstrained(new NoPenalty(), null, maxAllowedCost, runtimeDeadlineMs);
    }


	public PenalizedEvaluatedTrajectory getOptimalTrajectoryConstrained (
			StraightSegmentPenaltyFunction penalty, Trajectory previous, double maxAllowedCost, long runtimeDeadlineMs) {

    	HomotopyWeightedTimeGraph homotopyGraph = new HomotopyWeightedTimeGraph(spatialGraph, target.getPosition(), integrator, penalty, vmax, target.getTime(), waitMoveDuration);

        Graph<tt.euclidtime3i.Point, Straight> graphWithPenalty
        	= new PenaltyFunctionWrapper(agentMotions, penalty);


        HNode<tt.euclidtime3i.Point> startHNode = homotopyGraph.wrapNode(start, Complex.ZERO);


        Goal<HNode<tt.euclidtime3i.Point>> goal = new Goal<HNode<tt.euclidtime3i.Point>>() {
            @Override
            public boolean isGoal(HNode<tt.euclidtime3i.Point> current) {
                return current.getNode().getPosition().equals(target.getPosition()) && current.getNode().getTime() >= target.getTime();
            }
        };


         final HeuristicToGoal<HNode<Point>> hHeuristic = new HeuristicToGoal<HNode<Point>>() {

            @Override
            public double getCostToGoalEstimate(HNode<Point> current) {
                return heuristic.getCostToGoalEstimate(current.getNode());
            }
        };


        AStarShortestPathSimple<HNode<Point>, HEdge<Point, Straight>> alg =
                new AStarShortestPathSimple<HNode<Point>, HEdge<Point, Straight>>(homotopyGraph, hHeuristic, startHNode, goal);

        GraphPath<HNode<Point>, HEdge<Point, Straight>> path = alg.findPathCostAndDeadlineLimit(maxAllowedCost, runtimeDeadlineMs);

        if (path == null)
            return null;

        lastHValue = getHValue(path);
        EvaluatedTrajectory traj = toTrajectory(path);
		return new PenalizedEvaluatedTrajectory(traj, Double.NaN);
	}


	@Override
	public void setHomotopyPolicy(HValuePolicy hPolicy) {
		this.hPolicy = hPolicy;

	}

    private EvaluatedTrajectory toTrajectory(GraphPath<HNode<Point>, HEdge<Point, Straight>> path) {
        List<HEdge<Point, Straight>> hEdgeList = path.getEdgeList();
        List<Straight> segments = new ArrayList<Straight>();

        for (HEdge<tt.euclidtime3i.Point, Straight> hEdge : hEdgeList) {
            segments.add(hEdge.getEdge());
        }

        return new BasicSegmentedTrajectory(segments, path.getEndVertex().getNode().getTime(), path.getWeight());
    }

    private Complex getHValue(GraphPath<HNode<Point>, HEdge<Point, Straight>> path) {
        List<HEdge<Point, Straight>> edgeList = path.getEdgeList();
        int last = edgeList.size() - 1;

        return edgeList.get(last).getTarget().getHValue();
    }


	@Override
	public Complex getHValueOfLastPath() {
		return lastHValue;
	}

	@Override
	public PenalizedEvaluatedTrajectory getOptimalTrajectoryConstrained(
			PenaltyFunction[] penaltyFunctions, Trajectory[] otherTrajectories,
			Trajectory initialTrajectory, double maxAllowedCost,
			long runtimeDeadlineMs) {
		// TODO This has been added just to silence the Eclipse.
		return null;
	}




}
