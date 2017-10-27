package tt.discrete;

import tt.euclidtime3i.discretization.Straight;

import java.util.List;

public interface SegmentedTrajectory<V> extends Trajectory<V> {
    public List<Straight> getSegments();
}
