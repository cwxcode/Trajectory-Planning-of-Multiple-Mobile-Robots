package tt.euclidtime3i.sipprrts;

import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.planner.rrtstar.EuclideanRRTStar;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.Vertex;

import java.util.List;

public class SippRRTStar extends EuclideanRRTStar<SippRRTNode, SippRRTEdge> {

    public SippRRTStar(SippRRTDomain domain, SippRRTNode initialState, double initialRadius) {
        super(domain, new SIPPNodeCoordinatesProvider(), initialState, initialRadius);
    }

    @Override
    protected void updateBranchCost(Vertex<SippRRTNode, SippRRTEdge> vertex) {
        checkBestVertex(vertex);

        for (Vertex<SippRRTNode, SippRRTEdge> child : vertex.getChildren()) {
            List<Straight> straights = child.edgeFromParent.getStraights();
            int outgoingEdgeParentTime = Trajectories.start(straights).getTime();
            int actualParentTime = vertex.getState().getTime();

            if (outgoingEdgeParentTime <= actualParentTime)
                continue;

            rewireSippEdge(vertex, child);
            updateSubtree(vertex, child);
        }
    }

    private void updateSubtree(Vertex<SippRRTNode, SippRRTEdge> vertex, Vertex<SippRRTNode, SippRRTEdge> child) {
        double newCost = vertex.getCostFromRoot() + child.getCostFromParent();
        child.setCostFromRoot(newCost);
        updateBranchCost(child);
    }

    private void rewireSippEdge(Vertex<SippRRTNode, SippRRTEdge> vertex, Vertex<SippRRTNode, SippRRTEdge> child) {
        domain.estimateExtension(vertex.getState(), child.getState());

        Extension<SippRRTNode, SippRRTEdge> extension = domain.extendTo(vertex.getState(), child.getState());
        updateEdge(vertex, extension, child);
    }

    private boolean connected(Vertex<SippRRTNode, SippRRTEdge> vertex, Vertex<SippRRTNode, SippRRTEdge> child) {
        List<Straight> toParent = vertex.edgeFromParent.getStraights();
        List<Straight> toChild = child.edgeFromParent.getStraights();
        return connected(toParent, toChild);
    }

    private boolean connected(List<Straight> toParent, List<Straight> toChild) {
        return Trajectories.end(toParent).equals(Trajectories.start(toChild));
    }

    protected void updateEdge(Vertex<SippRRTNode, SippRRTEdge> parent, Extension<SippRRTNode, SippRRTEdge> extension, Vertex<SippRRTNode, SippRRTEdge> target) {
        target.setEdgeFromParent(extension.edge);
        target.setCostFromParent(extension.cost);
        target.setCostFromRoot(parent.getCostFromRoot() + extension.cost);

        checkBestVertex(target);

        edgeSources.put(extension.edge, parent.getState());
        edgeTargets.put(extension.edge, target.getState());
        incomingEdges.put(target.getState(), extension.edge);
    }
}
