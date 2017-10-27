package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Trajectory;

public interface PairwiseConstraint {
    double getPenalty(Trajectory t1, Trajectory t2);
}
