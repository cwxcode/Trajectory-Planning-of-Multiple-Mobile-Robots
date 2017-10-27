package tt.jointeuclid2ni.operatordecomposition;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.specifics.Specifics;
import org.jgrapht.alg.specifics.SpecificsFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;

import tt.euclid2i.Line;
import tt.euclid2i.Point;

import java.util.Set;

public class ODWrapper extends AbstractDirectedGraphWrapper<ODNode, ODEdge> {

    //TODO make the class generic

    private Specifics<Point, Line>[] specifics;
    private double edgeWeight;
    private boolean agentsCanWait;
	private Point[] goals;

    public ODWrapper(DirectedGraph<Point, Line>[] graphs, Point[] goals, double edgeWeight, boolean agentsCanWait) {
        this.edgeWeight = edgeWeight;
        this.agentsCanWait = agentsCanWait;
        this.specifics = SpecificsFactory.create(graphs);
        this.goals = goals;
    }

    @Override
    public Set<ODEdge> outgoingEdgesOf(ODNode source) {
        int i = source.agentToExpand();
        Point position = source.agentPositionsToExpand();

        Set<Line> underlyingEdges = specifics[i].outgoingEdgesOf(position);
        return source.expand(underlyingEdges, agentsCanWait);
    }

    @Override
    public ODNode getEdgeSource(ODEdge edge) {
        return edge.getSource();
    }

    @Override
    public ODNode getEdgeTarget(ODEdge edge) {
        return edge.getTarget();
    }

    @Override
    public double getEdgeWeight(ODEdge edge) {
        if (agentWaitsAtGoal(edge, goals))
            return 0;
        else
            return edgeWeight;
    }

	private boolean agentWaitsAtGoal(ODEdge edge, Point[] goals2) {
		ODNode source = edge.getSource();
		ODNode target = edge.getTarget();
		int agentId = source.agentToExpand();
		return source.getAgentPositions()[agentId].equals(goals[agentId]) && target.getAgentPositions()[agentId].equals(goals[agentId]);
	}
}
