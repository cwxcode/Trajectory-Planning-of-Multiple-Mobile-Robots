package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.GraphDelegator;
import org.jgrapht.util.GraphBuilder;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.util.Util;

@SuppressWarnings("serial")
public class ObstacleWrapper<V extends tt.euclid2i.Point, E extends Line>
        extends GraphDelegator<V, E> implements DirectedGraph<V,E> {

	Collection<Region> obstacles;

    public ObstacleWrapper(Graph<V, E> arg0, Collection<Region> obstacles) {
		super(arg0);
		this.obstacles = obstacles;
	}

	@Override
	public Set<E> outgoingEdgesOf(V arg0) {
		Set<E> edges = super.outgoingEdgesOf(arg0);
		Set<E> conflictFreeEdges = new HashSet<E>();

		for (E edge : edges) {
			if (Util.isVisible(edge.getStart(), edge.getEnd(), obstacles)) {
				conflictFreeEdges.add(edge);
			}
		}

		return conflictFreeEdges;
	}

	@Override
	public Set<E> incomingEdgesOf(V arg0) {
		Set<E> edges = super.incomingEdgesOf(arg0);
		Set<E> conflictFreeEdges = new HashSet<E>();

		for (E edge : edges) {
			if (Util.isVisible(edge.getStart(), edge.getEnd(), obstacles)) {
				conflictFreeEdges.add(edge);
			}
		}

		return conflictFreeEdges;
	}

	public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        //FIXME if the underlying graph is lazygrid, it may happen that this method causes infinite loop
        DirectedGraph<Point, Line> fullGraph
                = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build((DirectedGraph<Point, Line>)this, fullGraph, initialPoint);
    }

}
