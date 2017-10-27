package tt.euclid2i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LazyStarGraph extends AbstractDirectedGraphWrapper<Point, Line> {

    private int branchingFactor;
    private int edgeLength;
    private Random random;
    private Rectangle bounds;
    private Collection<Region> obstacles;

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();

        double zero = random.nextDouble() * Math.PI;
        double step = 2 * Math.PI / branchingFactor;

        for (int i = 0; i < branchingFactor; i++) {
            double angle = zero + step * i;

            int x = (int) Math.round(vertex.x + edgeLength * Math.sin(angle));
            int y = (int) Math.round(vertex.y + edgeLength * Math.cos(angle));

            Point opposite = new Point(x, y);

            if (bounds.isInside(opposite) && Util.isVisible(vertex, opposite, obstacles))
                edges.add(new Line(vertex, opposite));
        }

        return edges;
    }


    public DirectedGraph<Point, Line> generateFullGraph(Collection<Point> initialPoints, int vertices) {
        return GraphBuilder.build(this, new DefaultDirectedGraph<Point, Line>(Line.class), initialPoints, vertices);
    }
}
