package tt.euclidtime3i.sipprrts;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipp.SippUtils;
import tt.euclidtime3i.sipp.intervals.Interval;
import tt.euclidtime3i.sipp.intervals.SafeIntervalBuilder;
import tt.euclidtime3i.sipp.intervals.SafeIntervalList;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.ExtensionEstimate;

import java.util.*;

public class SippRRTDomain implements Domain<SippRRTNode, SippRRTEdge> {

    private final Region boundary;
    private final Collection<Region> inflatedBoundary;
    private final Collection<Region> inflatedObstacles;
    private final DynamicObstacles dynEnv;
    private final int[] separations;
    private final RRTMission mission;

    // Buffered calculation safe intervals for edges
    private final Map<Line, SafeIntervalList> edgeSafeIntervals;

    private Random random;
    private final int step;
    private int maxTime;

    public SippRRTDomain(Environment statEnv, DynamicObstacles dynEnv, RRTMission mission, int samplingStep, int seed, int maxTime) {
        this.inflatedObstacles = Util.inflateRegions(statEnv.getObstacles(), mission.getBodyRadius());
        this.boundary = statEnv.getBoundary();
        this.inflatedBoundary = Util.inflateRegions(Collections.singletonList(boundary), mission.getBodyRadius());
        this.dynEnv = dynEnv;
        this.mission = mission;
        this.step = samplingStep;
        this.random = new Random(seed);
        this.separations = SippUtils.radiusesToSeparations(mission.getBodyRadius(), dynEnv.getObstacleRadiuses());
        this.maxTime = maxTime;
        this.edgeSafeIntervals = new HashMap<Line, SafeIntervalList>();
    }

    public SippRRTNode createRoot(Point point) {
        SafeIntervalList intervals = SafeIntervalBuilder.safeIntervalsForSinglePoint(point, dynEnv, mission.getBodyRadius(), step, maxTime);

        if (intervals.isEmpty()) {
            throw new IllegalArgumentException("No safe interval in the root point");
        } else {
            return new SippRRTNode(point, intervals, 0, 0);
        }
    }

    @Override
    public SippRRTNode sampleState() {
        Point point = sampleSpace();
        SafeIntervalList intervals = SafeIntervalBuilder.safeIntervalsForSinglePoint(point, dynEnv, mission.getBodyRadius(), step, maxTime);
        int sampledInterval = random.nextInt(intervals.size());

        return new SippRRTNode(point, intervals, sampledInterval);
    }

    public Point sampleSpace() {
        if (random.nextDouble() > 0.02)
            return Util.sampleFreeSpace(boundary, inflatedObstacles, random);
        else
            return mission.getTarget();
    }

    @Override
    public Extension<SippRRTNode, SippRRTEdge> extendTo(SippRRTNode from, SippRRTNode to) {
        List<Straight> straights = planTraversalTroughEdge(from, to);

        if (straights == null)
            return null;

        int duration = Trajectories.duration(straights);
        int endTime = Trajectories.end(straights).getTime();

        to.setTime(endTime);
        SippRRTEdge edge = new SippRRTEdge(from, to, straights);

        return new Extension<SippRRTNode, SippRRTEdge>(from, to, edge, duration, true);
    }

    @Override
    public ExtensionEstimate estimateExtension(SippRRTNode from, SippRRTNode to) {
        //TODO: the plan of traversal is computed twice! it should be also buffered ...
        List<Straight> straights = planTraversalTroughEdge(from, to);

        if (straights == null)
            return null;

        return new ExtensionEstimate(Trajectories.duration(straights), true);
    }

    private List<Straight> planTraversalTroughEdge(SippRRTNode from, SippRRTNode to) {
        Line edge = new Line(from.getPoint(), to.getPoint());
        int duration = (int) Math.round(edge.getDistance() / mission.getMaxSpeed());

        SafeIntervalList edgeSIs = getEdgeSafeInterval(edge);
        Interval nodeSI = from.getSafeInterval();
        Interval childSI = to.getSafeInterval();

        for (Interval edgeSI : edgeSIs) {
            Interval interval = SippUtils.safeIntervalToTraverse(nodeSI, edgeSI, childSI, from.getTime(), duration);

            if (interval == null)
                continue;

            List<Straight> straights = SippUtils.traverseInGivenInterval(edge, from.getTime(), interval);

            if (!collide(straights))
                return straights;
        }

        return null;
    }

    private SafeIntervalList getEdgeSafeInterval(Line edge) {
        SafeIntervalList intervals = edgeSafeIntervals.get(edge);

        if (intervals == null) {
            intervals = SafeIntervalBuilder.safeIntervalsForSingleEdge(edge, dynEnv, mission.getBodyRadius(), step, maxTime);
            edgeSafeIntervals.put(edge, intervals);
        }

        return intervals;
    }

    private boolean collide(List<Straight> straights) {
        int duration = Trajectories.duration(straights);
        SegmentedTrajectory trajectory = new BasicSegmentedTrajectory(straights, duration);

        return collideWithAnyDynamicObstacle(trajectory) || collideWithStaticEnvironment(straights);
    }

    private boolean collideWithStaticEnvironment(List<Straight> straights) {
        Point source = Trajectories.start(straights).getPosition();
        Point target = Trajectories.end(straights).getPosition();

        return !Util.isVisible(source, target, inflatedBoundary) || !Util.isVisible(source, target, inflatedObstacles);
    }

    private boolean collideWithAnyDynamicObstacle(SegmentedTrajectory trajectory) {
        return SeparationDetector.hasAnyPairwiseConflict(trajectory, dynEnv.getObstacles(), separations, step);
    }

    @Override
    public double distance(SippRRTNode s1, SippRRTNode s2) {
        Point point1 = s1.getPoint();
        Point point2 = s2.getPoint();

        return point1.distance(point2);
    }

    @Override
    public double nDimensions() {
        return 3;
    }

    @Override
    public boolean isInTargetRegion(SippRRTNode node) {
        Point point = node.getPoint();
        return node.isInLastSafeInterval() && point.equals(mission.getTarget());
    }

    @Override
    public double estimateCostToGo(SippRRTNode node) {
        return 0;
    }

}
