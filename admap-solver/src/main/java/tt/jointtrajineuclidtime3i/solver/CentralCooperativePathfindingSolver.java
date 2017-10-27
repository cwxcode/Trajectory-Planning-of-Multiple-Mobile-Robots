package tt.jointtrajineuclidtime3i.solver;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.jointtraj.solver.SearchResult;

public abstract class CentralCooperativePathfindingSolver {

    protected Point[] starts;
    protected tt.euclid2i.Point[] targets;
    protected int[] bodyRadiuses;
    protected DirectedGraph<tt.euclidtime3i.Point, Straight>[] agentsMotions;
    protected int maxTime;

    List<Listener> listeners = new LinkedList<Listener>();

    @SuppressWarnings("unchecked")
    protected CentralCooperativePathfindingSolver(
            tt.euclid2i.Point[] starts,
            tt.euclid2i.Point[] targets,
            DirectedGraph<tt.euclidtime3i.Point, Straight>[] agentsMotions,
            int[] bodyRadiuses,
            int maxTime) {

        super();
        this.starts = starts;
        this.targets = targets;
        this.agentsMotions = Arrays.copyOf(agentsMotions, agentsMotions.length);
        this.bodyRadiuses = bodyRadiuses;
        this.maxTime = maxTime;
    }

    abstract public SearchResult solve(long timeoutMs);

    protected int nAgents() {
        return agentsMotions.length;
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    protected void notifyNewSolution(EvaluatedTrajectory[] trajectories, boolean provedOptimal) {
        for (Listener listener : listeners) {
            EvaluatedTrajectory[] trajectoriesCopy = Arrays.copyOf(trajectories, trajectories.length);
            listener.notifyNewSolution(trajectoriesCopy, provedOptimal);
        }
    }
}
