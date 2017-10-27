package tt.euclid2i.probleminstance;

import org.jgrapht.DirectedGraph;
import tt.euclid2i.Line;
import tt.euclid2i.Point;

/**
 * Created by Vojtech Letal on 3/4/14.
 */
public interface DiscretizedEnvironment extends Environment {

    public DirectedGraph<Point, Line> getPlanningGraph();

}
