package tt.planner.homotopy.hclass;

/**
 * Implementations of this interface is used in the HNode to cluster/distinguish different paths belonging into different
 * homotopy classes.
 */
public interface HClass {

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();

}
