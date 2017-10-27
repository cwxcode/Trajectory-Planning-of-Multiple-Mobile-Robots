package tt.euclidtime3i.discretization;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclidtime3i.Point;

@SuppressWarnings("serial")
public class FreeOnTargetWaitExtension extends GraphDelegator<Point, Straight> implements DirectedGraph<Point, Straight> {

	tt.euclid2i.Point targetVertex;

    public FreeOnTargetWaitExtension(Graph<Point, Straight> g, tt.euclid2i.Point targetVertex) {
        super(g);
        this.targetVertex = targetVertex;
    }

	@Override
    public double getEdgeWeight(Straight e) {
        if (e.getStart().getPosition().equals(targetVertex) && e.getEnd().getPosition().equals(targetVertex)) {
            return 0;
        } else {
            return super.getEdgeWeight(e);
        }
    }
}
