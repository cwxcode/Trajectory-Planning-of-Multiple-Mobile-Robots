package tt.jointtraj.separableflow;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

@SuppressWarnings("serial")
public class PenaltyFunctionWrapper extends GraphDelegator<Point, Straight> implements DirectedGraph<Point, Straight>  {
    private StraightSegmentPenaltyFunction penaltyFunction;

	public PenaltyFunctionWrapper(DirectedGraph<Point, Straight> g, StraightSegmentPenaltyFunction penaltyFunction) {
        super(g);
        this.penaltyFunction =  penaltyFunction;
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> allEdges = super.outgoingEdgesOf(vertex);
        Set<Straight> consistentEdges = new LinkedHashSet<Straight>();

        for (Straight edge : allEdges) {
            //if (!Double.isInfinite(getEdgeWeight(edge))) {
            //    consistentEdges.add(edge);
            //}
        	consistentEdges.add(edge);
        }

        return consistentEdges;
    }

    @Override
    public double getEdgeWeight(Straight e) {
        double cost = super.getEdgeWeight(e);
        return cost + penaltyFunction.getStraightTrajectoryCost(e.getStart(), e.getEnd());
    }
}
