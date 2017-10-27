package tt.jointtraj.homotopyiterative;

import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.util.Goal;
import org.jscience.mathematics.number.Complex;

import tt.euclid2i.Line;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.FreeOnTargetWaitExtension;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.softconstraints.PairwiseConstraint;
import tt.euclidtime3i.discretization.softconstraints.PairwiseSoftConstraintWrapper;
import tt.jointtraj.separableflow.PenaltyFunctionWrapper;
import tt.jointtraj.separableflow.StraightSegmentPenaltyFunction;
import tt.planner.homotopy.HEdge;
import tt.planner.homotopy.HNode;
import tt.planner.homotopy.HomotopyGraphWrapper;
import tt.planner.homotopy.ProjectionToComplexPlane;
import tt.planner.homotopy.hclass.HClassDiscretized;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.planner.homotopy.hvalue.HValuePolicy;

public class HomotopyWeightedTimeGraph extends AbstractDirectedGraphWrapper<HNode<Point>, HEdge<Point, Straight>> {

    private static final double precision = 0.02;
    PenaltyFunctionWrapper penaltyWrapper;
    private HomotopyGraphWrapper<Point, Straight> homotopyWrapper;

    @SuppressWarnings("unchecked")
	public HomotopyWeightedTimeGraph(DirectedGraph<tt.euclid2i.Point, Line> spatialGraph,
                                     final tt.euclid2i.Point target,
                                     HValueIntegrator integrator,
                                     StraightSegmentPenaltyFunction penaltyFunction,
                                     float speed,
                                     int maxTime,
                                     int waitMoveDuration) {


		DirectedGraph<Point, Straight> spaceTimeGraph = new FreeOnTargetWaitExtension(
				new ConstantSpeedTimeExtension(spatialGraph, maxTime,
						new float[] { speed }, waitMoveDuration), target);

        penaltyWrapper = new PenaltyFunctionWrapper(spaceTimeGraph, penaltyFunction);

        Goal<HNode<Point>> goal = new Goal<HNode<Point>>() {
            @Override
            public boolean isGoal(HNode<Point> current) {
                return current.getNode().getPosition().equals(target);
            }
        };

        ProjectionToComplexPlane<Point> projection = new ProjectionToComplexPlane<Point>() {
            @Override
            public Complex complexValue(Point state) {
                return Complex.valueOf(state.x, state.y);
            }
        };

        HClassDiscretized.Provider<Point> hValueProvider = new HClassDiscretized.Provider<Point>();

        homotopyWrapper = new HomotopyGraphWrapper<Point, Straight>(penaltyWrapper, goal, projection,
                integrator, hValueProvider, precision);
    }

    public HNode<Point> wrapNode(Point node, Complex hValue) {
        return homotopyWrapper.wrapNode(node, hValue);
    }


    public HValuePolicy getPolicy() {
        return homotopyWrapper.getPolicy();
    }

    public void setPolicy(HValuePolicy policy) {
        homotopyWrapper.setPolicy(policy);
    }

    @Override
    public Set<HEdge<Point, Straight>> edgesOf(HNode<Point> vertex) {
        return homotopyWrapper.edgesOf(vertex);
    }

    @Override
    public Set<HEdge<Point, Straight>> incomingEdgesOf(HNode<Point> target) {
        return homotopyWrapper.incomingEdgesOf(target);
    }

    @Override
    public Set<HEdge<Point, Straight>> outgoingEdgesOf(HNode<Point> source) {
        return homotopyWrapper.outgoingEdgesOf(source);
    }

    @Override
    public HNode<Point> getEdgeSource(HEdge<Point, Straight> hEdge) {
        return homotopyWrapper.getEdgeSource(hEdge);
    }

    @Override
    public HNode<Point> getEdgeTarget(HEdge<Point, Straight> hEdge) {
        return homotopyWrapper.getEdgeTarget(hEdge);
    }

    @Override
    public double getEdgeWeight(HEdge<Point, Straight> hEdge) {
        return homotopyWrapper.getEdgeWeight(hEdge);
    }

}
