package tt.continous;

/**
 * This class represents the general concept of path in some space X.
 * Here, we consider it as a mapping from a [0,1] to X.
 *
 */
public interface Path<S> {
    public S get(double alpha);
}
