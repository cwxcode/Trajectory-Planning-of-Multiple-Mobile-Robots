package tt.continous;

/**
 * A path that is assigned a cost.
 */
public interface EvaluatedPath<S> extends Path<S> {
    public double getCost();
}
