package tt.euclidtime3i.discretization.softconstraints;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

@SuppressWarnings("serial")
public class PairwiseSoftConstraintWrapper<V extends Point, E extends Straight> extends GraphDelegator<V, E> {

    private Trajectory[] otherTrajs;
    private PairwiseConstraint[] constraints;

    public PairwiseSoftConstraintWrapper(DirectedGraph<V, E> g, Trajectory[] otherTrajs, PairwiseConstraint[] constraints) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.constraints = constraints;
    }

    @Override
    public double getEdgeWeight(E e) {
        double cost = super.getEdgeWeight(e);
        return cost + calculateEdgePenalty(e);
    }

    public double calculateEdgePenalty(E e) {
        double penalty = 0;

        Trajectory edgeTrajectory = new tt.euclidtime3i.trajectory.LinearTrajectory(e.getStart(), e.getEnd(), super.getEdgeWeight(e));

        for (int i = 0; i < otherTrajs.length; i++) {
            double constraintPenalty = constraints[i].getPenalty(edgeTrajectory, otherTrajs[i]);
            if (constraintPenalty > 0) //Infinity times 0 is NaN
                penalty += constraintPenalty;
        }

        return penalty;
    }

}
