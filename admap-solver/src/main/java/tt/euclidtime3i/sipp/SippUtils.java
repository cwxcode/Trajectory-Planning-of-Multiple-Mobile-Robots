package tt.euclidtime3i.sipp;

import com.google.common.primitives.Ints;
import org.jgrapht.GraphPath;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipp.intervals.Interval;

import java.util.ArrayList;
import java.util.List;

public class SippUtils {

    public static <E extends Line> List<Straight> traverseInGivenInterval(E line, int time, Interval interval) {
        List<Straight> straights = new ArrayList<Straight>(2);

        if (interval.start != time)
            straights.add(new Straight(line.getStart(), time, line.getStart(), interval.start));

        straights.add(new Straight(line.getStart(), interval.start, line.getEnd(), interval.end));

        return straights;
    }

    public static Interval safeIntervalToTraverse(Interval parentSI, Interval edgeSI, Interval childSI, int time, int duration) {
        int earliestDeparture = Ints.max(edgeSI.start, parentSI.start, time);
        int latestDeparture = Ints.min(edgeSI.end, parentSI.end);
        int earliestArrival = Ints.max(edgeSI.start, childSI.start);
        int latestArrival = Ints.min(edgeSI.end, childSI.end);

        if (latestDeparture < earliestDeparture || latestArrival < earliestArrival ||
                earliestArrival - latestDeparture > duration || latestArrival - earliestDeparture < duration)
            return null;

        int startTime;
        if (earliestArrival - earliestDeparture > duration) {
            startTime = earliestArrival - duration;
        } else {
            startTime = earliestDeparture;
        }

        return new Interval(startTime, startTime + duration);
    }

    public static <V extends Point> SegmentedTrajectory parseTrajectory(GraphPath<SippNode, SippEdge> path, int duration) {
        if (path == null)
            return null;

        int lastStraightEndedAt = 0;
        List<SippEdge> edges = path.getEdgeList();
        ArrayList<Straight> straights = new ArrayList<Straight>();

        for (SippEdge edge : edges) {
        	// Insert wait move if needed
            if (edge.getSource().getTime() != lastStraightEndedAt) {
            	//straights.add(new Straight(edge.getSource().getPoint(), lastStraightEndedAt, edge.getSource().getPoint(), edge.getSource().getTime()));
            }
        	straights.addAll(edge.getLines());

            lastStraightEndedAt = edge.getTarget().getTime();
        }

        return new BasicSegmentedTrajectory(straights, duration);
    }

    public static int[] radiusesToSeparations(int bodyRadius, int[] otherRadiuses) {
        int[] separations = new int[otherRadiuses.length];
        for (int i = 0; i < separations.length; i++) {
            separations[i] = otherRadiuses[i] + bodyRadius;
        }
        return separations;
    }
}
