package tt.jointtrajineuclidtime3i.solver;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.jointtraj.solver.SearchResult;
import tt.jointtrajineuclidtime3i.ODGraph;
import tt.jointtrajineuclidtime3i.State;
import tt.jointtrajineuclidtime3i.Transition;

public class ODSolver extends CentralCooperativePathfindingSolver {

    public static Logger LOGGER = Logger.getLogger(ODSolver.class);

    double costLimit;
    List<Trajectory> softConstrainingTrajectories;

    public ODSolver(Point[] starts, Point[] targets,
            DirectedGraph<tt.euclidtime3i.Point, Straight>[] agentsMotions,
            int bodyRadiuses[],
            int maxTime, double costLimit, List<Trajectory> softConstrainingTrajectories) {
        super(starts, targets, agentsMotions, bodyRadiuses, maxTime);

        this.costLimit = costLimit;
        this.softConstrainingTrajectories = softConstrainingTrajectories;
    }

    @Override
    public SearchResult solve(long timeoutMs) {

        long stopAtMs = System.currentTimeMillis() + timeoutMs;

        EvaluatedTrajectory[] initialSinglePointTrajs = new EvaluatedTrajectory[starts.length];
        // create single point trajectories
        for (int i=0; i<initialSinglePointTrajs.length; i++) {
            tt.euclid2i.Point pos = starts[i];
            initialSinglePointTrajs[i] = Trajectories.createSinglePointTrajectory(new tt.euclidtime3i.Point(pos.x, pos.y, 0), 0, 0);
        }

        State initialState = new State(initialSinglePointTrajs);
        ODGraph jointGraph = new ODGraph(agentsMotions, bodyRadiuses);

        AStarShortestPathSimple<State, Transition> astar = new AStarShortestPathSimple<State, Transition>(jointGraph,
                new HeuristicToGoal<State>() {

                    @Override
                    public double getCostToGoalEstimate(State current) {
                        double estimate = 0.0;
                        for (int i = 0; i < targets.length; i++) {
                            if (targets[i] != null) {
                                estimate += current.getEndPoint(i).getPosition().distance(targets[i]);
                            }
                        }
                        return estimate;
                    }
                }, initialState, new Goal<State>() {
            @Override
            public boolean isGoal(State current) {

//            	if (!current.trajectoriesEndInTheSameTime()) {
//                    return false;
//                }

                for (int i = 0; i < targets.length; i++) {
                    if (targets[i] != null) {
                        if (!current.getEndPoint(i).getPosition().equals(targets[i])) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        );

        GraphPath<State, Transition> path = astar.findPathDeadlineLimit(Integer.MAX_VALUE, stopAtMs);

        if (path != null) {
            LOGGER.info("Found solution! Cost: " + path.getWeight() + "; A* exp. states: " + astar.getIterationCount() + "; A* runtime: NOT IMPLEMENTED");
            tt.euclid2i.EvaluatedTrajectory[] trajs = new tt.euclid2i.EvaluatedTrajectory[starts.length];
            for (int i = 0; i < starts.length; i++) {
                trajs[i] = Trajectories.convertToEuclid2iTrajectory(path.getEndVertex().get(i));
            }
            return new SearchResult(trajs, true);
        } else {
            return new SearchResult(null, true);
        }
    }

}
