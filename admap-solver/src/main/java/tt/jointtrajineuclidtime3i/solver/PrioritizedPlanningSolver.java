package tt.jointtrajineuclidtime3i.solver;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.ConstantStepSegmentedTrajectory;
import tt.euclid2i.trajectory.StraightSegmentTrajectory;
import tt.euclidtime3i.discretization.SeparationConstraintWrapper;
import tt.euclidtime3i.discretization.Straight;
import tt.jointtraj.solver.SearchResult;

import java.util.Arrays;

public class PrioritizedPlanningSolver extends CentralCooperativePathfindingSolver {

    public static Logger LOGGER = Logger.getLogger(PrioritizedPlanningSolver.class);
    private HeuristicToGoal<tt.euclidtime3i.Point>[] heuristics;
	private int samplingInterval;
	private int timeStepOfResultingTrajectories;
	public static final int VARIABLE_TIMESTEP = (-1);
	
    public PrioritizedPlanningSolver(Point[] starts, Point[] targets,
                                     DirectedGraph<tt.euclidtime3i.Point, Straight>[] agentsMotions,
                                     int[] bodyRadiuses, int timeStepOfResultingTrajectory, int samplingInterval, int maxTime) {
        super(starts, targets, agentsMotions, bodyRadiuses, maxTime);
        this.timeStepOfResultingTrajectories = timeStepOfResultingTrajectory;
        this.samplingInterval = samplingInterval;
    }

    public void setHeuristics(HeuristicToGoal<tt.euclidtime3i.Point>[] heuristics) {
        this.heuristics = heuristics;
    }

    @Override
    public SearchResult solve(long timeoutMs) {

        long stopAtMs = System.currentTimeMillis() + timeoutMs;

        EvaluatedTrajectory[] trajs = new EvaluatedTrajectory[starts.length];

        for (int i = 0; i < nAgents(); i++) {
            final int iFinal = i;

            // Cut out the regions around the agents 0, ..., i-1

            Trajectory[] otherTrajs = Arrays.copyOf(trajs, i);

            int[] separations = new int[i];
            for (int j = 0; j < i; j++) {
                separations[j] += bodyRadiuses[j] + bodyRadiuses[i];
            }

            // cut out the regions that are occupied by the other agents
            Graph<tt.euclidtime3i.Point, Straight> allowedMotions
                    = new SeparationConstraintWrapper(agentsMotions[i],
                    otherTrajs,
                    separations, samplingInterval);

            HeuristicToGoal<tt.euclidtime3i.Point> heuristic;

            if (heuristics != null)
                heuristic = heuristics[i];
            else
                heuristic = new HeuristicToGoal<tt.euclidtime3i.Point>() {

                    @Override
                    public double getCostToGoalEstimate(
                            tt.euclidtime3i.Point current) {
                        return current.getPosition().distance(targets[iFinal]);
                    }
                };

            AStarShortestPathSimple<tt.euclidtime3i.Point, Straight> astar
                    = new AStarShortestPathSimple<tt.euclidtime3i.Point, Straight>(allowedMotions, heuristic,
                    new tt.euclidtime3i.Point(starts[iFinal], 0),
                    new Goal<tt.euclidtime3i.Point>() {

                        @Override
                        public boolean isGoal(tt.euclidtime3i.Point current) {
                            return current.getPosition().equals(targets[iFinal]) && current.getTime() == maxTime;
                        }

                    }
            );

            GraphPath<tt.euclidtime3i.Point, Straight> path = astar.findPathDeadlineLimit(Integer.MAX_VALUE, stopAtMs);

            if (path != null) {
            	if (timeStepOfResultingTrajectories != VARIABLE_TIMESTEP) {
            		trajs[i] = new ConstantStepSegmentedTrajectory(path.getEdgeList(), timeStepOfResultingTrajectories, path.getWeight());
            	} else {
            		trajs[i] = new StraightSegmentTrajectory(path, maxTime);
            	}
            } else {
            	return new SearchResult(null, true);
            }
        }

        notifyNewSolution(trajs, false);
        return new SearchResult(trajs, true);

    }

}
