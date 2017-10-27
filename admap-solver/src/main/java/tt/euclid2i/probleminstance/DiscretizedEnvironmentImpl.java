package tt.euclid2i.probleminstance;

import org.jgrapht.DirectedGraph;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;

import java.util.Collection;

/**
 * Created by Vojtech Letal on 3/4/14.
 */
public class DiscretizedEnvironmentImpl extends EnvironmentImpl implements DiscretizedEnvironment {

    private final DirectedGraph<Point, Line> graph;

    public DiscretizedEnvironmentImpl(Environment environment, DirectedGraph<Point, Line> graph) {
        this(environment.getObstacles(), environment.getBoundary(), graph);
    }

    public DiscretizedEnvironmentImpl(Collection<Region> obstacles, Region boundary, DirectedGraph<Point, Line> graph) {
        super(obstacles, boundary);
        this.graph = graph;
    }

    @Override
    public DirectedGraph<Point, Line> getPlanningGraph() {
        return graph;
    }
}
