package tt.euclidtime3i.rrt;

import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipp.SippUtils;
import tt.euclidtime3i.sipprrts.DynamicObstacles;
import tt.euclidtime3i.sipprrts.RRTMission;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.ExtensionEstimate;

import java.util.*;

import static java.lang.Math.round;

/**
 * Created by Vojtech Letal on 3/7/14.
 */
public class EuclidTime3iDomain implements Domain<Point, List<Straight>> {

    private final Rectangle boundary;
    private final Collection<Region> inflatedBoundary;
    private final Collection<Region> inflatedObstacles;

    private final DynamicObstacles dynEnv;
    private final RRTMission mission;
    private final int[] separations;

    private Random random;
    private final int step;

    public EuclidTime3iDomain(Environment statEnv, DynamicObstacles dynEnv, RRTMission mission, int samplingStep, int seed) {
        this.inflatedObstacles = Util.inflateRegions(statEnv.getObstacles(), mission.getBodyRadius());
        this.boundary = statEnv.getBoundary().getBoundingBox();
        this.inflatedBoundary = Util.inflateRegions(Collections.singletonList(boundary), mission.getBodyRadius());
        this.dynEnv = dynEnv;
        this.mission = mission;
        this.step = samplingStep;
        this.random = new Random(seed);
        this.separations = SippUtils.radiusesToSeparations(mission.getBodyRadius(), dynEnv.getObstacleRadiuses());
    }

    @Override
    public Point sampleState() {
        Point point;
        do {
            tt.euclid2i.Point freeSpace = getRandomSample();
            int time = random.nextInt(dynEnv.getMaxTime());
            point = new Point(freeSpace, time);
        } while (collideWithDynamicObstacle(point));
        return point;
    }

    public tt.euclid2i.Point getRandomSample() {
        if (random.nextDouble() > 0.02)
            return Util.sampleFreeSpace(boundary, inflatedObstacles, random);
        else
            return mission.getTarget();
    }

    private boolean collideWithDynamicObstacle(Point point) {
        Trajectory[] obstacles = dynEnv.getObstacles();
        int[] obstacleRadiuses = dynEnv.getObstacleRadiuses();
        for (int i = 0; i < obstacles.length; i++) {
            if (collidesWithObstacle(point, obstacles[i], obstacleRadiuses[i])) return true;
        }
        return false;
    }

    private boolean collidesWithObstacle(Point point, Trajectory obstacle, int obstacleRadius) {
        int separation = obstacleRadius + mission.getBodyRadius();
        int time = point.getTime();

        tt.euclid2i.Point obstacleSpatial = obstacle.get(time);
        tt.euclid2i.Point pointSpatial = point.getPosition();

        if (obstacleSpatial.distance(pointSpatial) < separation)
            return true;
        return false;
    }

    @Override
    public Extension<Point, List<Straight>> extendTo(Point from, Point to) {
        if (from.getTime() > to.getTime())
            return null;

        tt.euclid2i.Point fromSpatial = from.getPosition();
        tt.euclid2i.Point toSpatial = to.getPosition();

        double distance = fromSpatial.distance(toSpatial);
        double minDuration = distance / mission.getMaxSpeed();

        int duration = to.getTime() - from.getTime();
        int departureTime = (int) round(to.getTime() - minDuration);

        List<Straight> straights;
        if (departureTime < from.getTime()) {
            return null;
        } else if (departureTime > from.getTime()) {
            Point departure = new Point(fromSpatial, departureTime);
            Straight wait = new Straight(from, departure);
            Straight traverse = new Straight(departure, to);

            straights = Arrays.asList(wait, traverse);
        } else {
            Straight traverse = new Straight(from, to);
            straights = Collections.singletonList(traverse);
        }

        BasicSegmentedTrajectory trajectory = new BasicSegmentedTrajectory(straights, duration);

            if (!SeparationDetector.hasAnyPairwiseConflict(trajectory, dynEnv.getObstacles(), separations, step)
                && Util.isVisible(fromSpatial, toSpatial, inflatedObstacles) && Util.isVisible(fromSpatial,toSpatial,inflatedBoundary)) {
            return null;
        } else {
            return new Extension<Point, List<Straight>>(from, to, straights, duration, true);
        }
    }

    @Override
    public ExtensionEstimate estimateExtension(Point from, Point to) {
        if (reachable(from, to)) {
            return new ExtensionEstimate(to.getTime() - from.getTime(), true);
        } else {
            return null;
        }
    }

    private boolean reachable(Point from, Point to) {
        double averageSpeed = distance(from, to) / (to.getTime() - from.getTime());
        return to.getTime() > from.getTime() && averageSpeed < mission.getMaxSpeed();
    }

    @Override
    public double estimateCostToGo(Point point) {
        return 0;
    }

    @Override
    public double distance(Point from, Point to) {
        tt.euclid2i.Point fromSpatial = from.getPosition();
        tt.euclid2i.Point toSpatial = to.getPosition();
        return toSpatial.distance(fromSpatial);
    }

    @Override
    public double nDimensions() {
        return 3;
    }

    @Override
    public boolean isInTargetRegion(Point point) {
        return point.getPosition().distance(mission.getTarget()) < mission.getTargetRadius();
    }
}
