package tt.jointtrajineuclidtime3i.solver;

import tt.euclid2i.EvaluatedTrajectory;

public interface Listener {
    public abstract void notifyNewSolution(EvaluatedTrajectory[] trajectories, boolean provedOptimal);
}
