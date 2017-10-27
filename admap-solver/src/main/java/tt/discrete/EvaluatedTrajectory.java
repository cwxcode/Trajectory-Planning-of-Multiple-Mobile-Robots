package tt.discrete;


/**
 * A trajectory having a certain cost.
 */
public interface EvaluatedTrajectory<S> extends Trajectory<S> {
    public double getCost();
}
