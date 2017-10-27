package tt.euclidtime3i.discretization;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import tt.euclidtime3i.Path;
import tt.euclidtime3i.Point;

public class StraightSegmentPath implements Path {

    private List<Straight> edges = null;

    private Point startWaypoint;
    private Point endWaypoint;
    private double pathTimeSpan;

    Graph<Point, Straight> graph;

    public StraightSegmentPath(GraphPath<Point,Straight> graphPath) {
        this.startWaypoint = graphPath.getStartVertex();
        this.endWaypoint = graphPath.getEndVertex();
        this.edges = graphPath.getEdgeList();
        this.graph = graphPath.getGraph();
        this.pathTimeSpan = endWaypoint.getTime()-startWaypoint.getTime();
    }

    @Override
    public Point get(double alpha) {

        assert(0 <= alpha && alpha <= 1);

        Point currentWaypoint = startWaypoint;

        double t = alpha * pathTimeSpan;

        if (t <  0 && t > pathTimeSpan) {
            return null;
        }


        if (edges != null)  {
            for (Straight maneuver: edges) {
                Point nextWaypoint = graph.getEdgeTarget(maneuver);

                if ( currentWaypoint.getTime() <= t && t <= nextWaypoint.getTime()) {
                    // linear approximation

                    double beta = (t - currentWaypoint.getTime()) / (nextWaypoint.getTime()-currentWaypoint.getTime());
                    assert(beta >= -0.00001 && beta <= 1.00001);

                    tt.euclid2d.Point pos = tt.euclid2d.Point.interpolate(new tt.euclid2d.Point(currentWaypoint.x, currentWaypoint.y), new tt.euclid2d.Point(nextWaypoint.x, nextWaypoint.y), beta);
                    return new Point((int) Math.round(pos.x), (int) Math.round(pos.y), (int) t);
                }
                currentWaypoint = nextWaypoint;
            }
        }
        if (t >= currentWaypoint.getTime()) {
                   return currentWaypoint;
        }

        return null;
    }

}
