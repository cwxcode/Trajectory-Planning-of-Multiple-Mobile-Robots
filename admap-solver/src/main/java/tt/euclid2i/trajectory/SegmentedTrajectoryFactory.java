package tt.euclid2i.trajectory;

import org.jgrapht.GraphPath;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SegmentedTrajectoryFactory {

    @SuppressWarnings("unchecked")
    public static <V extends tt.euclidtime3i.Point, E extends Straight> BasicSegmentedTrajectory createTrajectory(GraphPath<V, E> graphPath, int duration, double cost) {
        List<Straight> edgeList = (List<Straight>) graphPath.getEdgeList();

        if (edgeList.isEmpty())
            edgeList = Collections.singletonList(new Straight(graphPath.getStartVertex(), graphPath.getEndVertex()));

        return new BasicSegmentedTrajectory(edgeList, duration, cost);
    }

    public static BasicSegmentedTrajectory createConstantSpeedTrajectory(GraphPath<Point, Line> graphPath, int startTime, float speed, int duration, double cost) {
        List<Line> edgeList = graphPath.getEdgeList();

        if (edgeList.isEmpty())
            edgeList = Collections.singletonList(new Line(graphPath.getStartVertex(), graphPath.getEndVertex()));

        return createConstantSpeedTrajectory(edgeList, startTime, speed, duration, cost);
    }

    public static BasicSegmentedTrajectory createStationaryTrajectory(Point point, int startTime, int duration, double cost) {
        return createSingleLineTrajectory(point, point, startTime, duration, 0, cost);
    }

    public static BasicSegmentedTrajectory createSingleLineTrajectory(Point start, Point end, int startTime, int duration, int speed, double cost) {
        int endTime = startTime + (int) Math.round(start.distance(end) / speed);

        List<Straight> segments = new ArrayList<Straight>();
        segments.add(new Straight(new tt.euclidtime3i.Point(start, startTime), new tt.euclidtime3i.Point(end, endTime)));

        return new BasicSegmentedTrajectory(segments, duration, cost);
    }

    public static BasicSegmentedTrajectory createConstantSpeedTrajectory(List<Line> edgeList, int startTime, float speed, int duration, double cost) {
        List<Straight> segments = new ArrayList<Straight>();
        double oppositeTime, currentTime = startTime;

        for (Line edge : edgeList) {
            Point start = edge.getStart();
            Point end = edge.getEnd();

            oppositeTime = currentTime + start.distance(end) / speed;
            segments.add(new Straight(new tt.euclidtime3i.Point(start, (int) currentTime), new tt.euclidtime3i.Point(end, (int) oppositeTime)));

            currentTime = oppositeTime;
        }

        return new BasicSegmentedTrajectory(segments, duration, cost);
    }

}
