package tt.euclid2i.trajectory;

import java.text.DecimalFormat;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;


/**
 * A wrapper that interprets a graph path as a trajectory.
 * Start time must be be given. Then, the trajectory parameters should
 * be interpreted as follows:
 *
 *                      duration
 *       | --------------------------------> |
 *  start time                             max time
 *
 */

public class LineSegmentsConstantSpeedTrajectory<V extends Point, E extends Line> implements EvaluatedTrajectory {

    private List<E> maneuvers = null;

    private Point startWaypoint;
    private Point endWaypoint;

    Graph<V,E> graph;

    private int startTime;
    private int duration = Integer.MAX_VALUE;
    private double cost;
    private float speed;

    public LineSegmentsConstantSpeedTrajectory(int startTime, GraphPath<V,E> graphPath, float speed, int duration) {
        this.startWaypoint = graphPath.getStartVertex();
        this.endWaypoint = graphPath.getEndVertex();
        this.maneuvers = graphPath.getEdgeList();

        this.startTime = startTime;
        this.duration = duration;
        this.speed = speed;
        this.graph = graphPath.getGraph();
        this.cost = graphPath.getWeight();
    }

    @Override
    public Point get(int t) {
        Point currentWaypoint = startWaypoint;
        int currentWaypointTime = startTime;

        if (t < startTime || t > startTime + duration) {
            return null;
        }


        if (maneuvers != null)  {
            for (E maneuver: maneuvers) {
                Point nextWaypoint = graph.getEdgeTarget(maneuver);
                int maneuverDuration  = (int) Math.round(maneuver.getDistance() / speed);
                int nextWaypointTime = currentWaypointTime + maneuverDuration;

                if ( currentWaypointTime <= t && t <= nextWaypointTime) {
                    // linear approximation

                    double alpha = (double) (t - currentWaypointTime) / (double) maneuverDuration;
                    assert(alpha >= -0.00001 && alpha <= 1.00001);

                    tt.euclid2d.Point pos = tt.euclid2d.Point.interpolate(new tt.euclid2d.Point(currentWaypoint.x, currentWaypoint.y), new tt.euclid2d.Point(nextWaypoint.x, nextWaypoint.y), alpha);
                    return new Point((int) Math.round(pos.x), (int) Math.round(pos.y));
                }
                currentWaypoint = nextWaypoint;
                currentWaypointTime = nextWaypointTime;
            }
        }
        if (t >= currentWaypointTime) {
                   return currentWaypoint;
        }

        return null;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((maneuvers == null) ? 0 : maneuvers.hashCode());
        long temp;
        temp = Double.doubleToLongBits(speed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(startTime);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LineSegmentsConstantSpeedTrajectory other = (LineSegmentsConstantSpeedTrajectory) obj;
        if (maneuvers == null) {
            if (other.maneuvers != null)
                return false;
        } else if (!maneuvers.equals(other.maneuvers))
            return false;
        if (Double.doubleToLongBits(speed) != Double
                .doubleToLongBits(other.speed))
            return false;
        if (Double.doubleToLongBits(startTime) != Double
                .doubleToLongBits(other.startTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LSCST(@");
        DecimalFormat f = new DecimalFormat("#0.00");
        sb.append(f.format(startTime));

        if (!maneuvers.isEmpty()) {
            sb.append(" " + graph.getEdgeSource(maneuvers.get(0)));
        }

        for (E maneuver: maneuvers) {
            sb.append(" " + graph.getEdgeTarget(maneuver));
        }
        sb.append(" )");
        return sb.toString();
    }

    @Override
    public int getMinTime() {
        return startTime;
    }

    @Override
    public int getMaxTime() {
        return startTime + duration;
    }

    @Override
    public double getCost() {
        return cost;
    }


}
