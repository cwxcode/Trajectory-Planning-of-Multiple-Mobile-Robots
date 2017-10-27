package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class SeparationConstraint implements PairwiseConstraint {

    private PenaltyFunction penaltyFunction;
    private int samplingInterval;

    public SeparationConstraint(PenaltyFunction penaltyFunction,
                                int samplingInterval) {
        super();
        this.penaltyFunction = penaltyFunction;
        this.samplingInterval = samplingInterval;

    }

    @Override
    public double getPenalty(Trajectory t1, Trajectory t2) {
        return PenaltyIntegrator.integratePenaltySingle(t1, penaltyFunction,t2, samplingInterval);
    }

}
