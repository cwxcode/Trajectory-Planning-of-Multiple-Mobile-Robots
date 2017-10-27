package tt.euclidtime3i.trajectory;

import tt.discrete.SegmentedTrajectory;
import tt.discrete.Trajectory;
import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.List;

public class Trajectories {

    private Trajectories() {
    }

    public static int duration(List<Straight> straights) {
        int start = start(straights).getTime();
        int end = end(straights).getTime();
        return end - start;
    }

    public static void checkNonEmpty(SegmentedTrajectory segmentedTrajectory) {
        if (segmentedTrajectory == null)
            throw new IllegalArgumentException("null trajectory");

        List<Straight> segments = segmentedTrajectory.getSegments();
        if (segments == null || segments.isEmpty())
            throw new IllegalArgumentException("empty trajectory");
    }

    public static Point start(SegmentedTrajectory trajectory) {
        return start(trajectory.getSegments());
    }

    public static Point end(SegmentedTrajectory trajectory) {
        return end(trajectory.getSegments());

    }

    public static Point start(List<Straight> straights) {
        return straights.get(0).getStart();
    }

    public static Point end(List<Straight> straights) {
        int size = straights.size();
        return straights.get(size - 1).getEnd();
    }

    public static boolean overlapInTime(Trajectory a, Trajectory b) {
        int start = Math.max(a.getMinTime(), b.getMinTime());
        int end = Math.min(a.getMaxTime(), b.getMaxTime());

        return start <= end;
    }

    public static EvaluatedTrajectory concatenate(final EvaluatedTrajectory traj1, final EvaluatedTrajectory traj2) {

        if (traj1.getMaxTime() != traj2.getMinTime()) {
            throw new RuntimeException("The trajectories are not aligned.");
        }

        return new ConcatenatedTrajectory(traj1, traj2);
    }

    public static EvaluatedTrajectory createSinglePointTrajectory(final Point point, final int time, final double cost) {
        return new SinglePointTrajectory(point, time, cost);
    }

    public static tt.euclid2i.EvaluatedTrajectory convertToEuclid2iTrajectory(final EvaluatedTrajectory traj) {
        return new tt.euclid2i.EvaluatedTrajectory() {

            @Override
            public double getCost() {
                return traj.getCost();
            }

            @Override
            public int getMinTime() {
                return traj.getMinTime();
            }

            @Override
            public int getMaxTime() {
                return traj.getMaxTime();
            }

            @Override
            public tt.euclid2i.Point get(int t) {
                Point timePoint = traj.get(t);
                if (timePoint != null) {
                    return new tt.euclid2i.Point(timePoint.x, timePoint.y);
                } else {
                    return null;
                }
            }

            @Override
            public int hashCode() {
                return traj.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return traj.equals(obj);
            }

            @Override
            public String toString() {
                return traj.toString();
            }

        };
    }

    public static EvaluatedTrajectory convertFromEuclid2iTrajectory(final tt.euclid2i.EvaluatedTrajectory traj) {
        return new EvaluatedTrajectory() {

            @Override
            public double getCost() {
                return traj.getCost();
            }

            @Override
            public int getMinTime() {
                return traj.getMinTime();
            }

            @Override
            public int getMaxTime() {
                return traj.getMaxTime();
            }

            @Override
            public Point get(int t) {
                tt.euclid2i.Point pos = traj.get(t);
                return new Point(pos.x, pos.y, t);
            }

            @Override
            public int hashCode() {
                return traj.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return traj.equals(obj);
            }

            @Override
            public String toString() {
                return traj.toString();
            }

        };
    }
}
