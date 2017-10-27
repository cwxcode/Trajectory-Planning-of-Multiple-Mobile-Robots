package tt.euclid2i.rrtstar;

import java.util.Collection;
import java.util.Random;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.ShortestPathProblem;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.ExtensionEstimate;

public class StraightLineDomain implements Domain<Point, Line> {

    double tryGoalRatio;
    Rectangle bounds;
    Collection<Region> obstacles;
    Region target;
    Point targetPoint;
    Random random;
    final double speed = 1.0;

    public StraightLineDomain(ShortestPathProblem problem, int seed, double tryGoalRatio) {
        super();
        this.bounds = problem.getBoundary().getBoundingBox();
        this.obstacles = problem.getObstacles();
        this.target = problem.getTargetRegion();
        this.targetPoint = problem.getTargetPoint();
        this.random = new Random(seed);
        this.tryGoalRatio = tryGoalRatio;
    }

    public StraightLineDomain(Rectangle bounds, Collection<Region> obstacles,
            Region target, Point targetPoint, int seed, double tryGoalRatio) {
        super();
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.target = target;
        this.targetPoint = targetPoint;
        this.random = new Random(seed);
        this.tryGoalRatio = tryGoalRatio;
    }

    @Override
    public Point sampleState() {
        if (random.nextDouble() < tryGoalRatio) {
            return targetPoint;
        }
        return Util.sampleFreeSpace(bounds, obstacles, random);
    }

    @Override
    public Extension<Point, Line> extendTo(Point from, Point to) {
        Extension<Point, Line> result = null;
        if (Util.isVisible(from, to, obstacles)) {
            Line maneuver = new Line(from, to);
            result = new Extension<Point, Line>(from, to, maneuver, maneuver.getDistance(), true);
        }
        return result;
    }

    @Override
    public ExtensionEstimate estimateExtension(Point p1, Point p2) {
        return new ExtensionEstimate(p1.distance(p2) / speed, true);
    }

    @Override
    public double distance(Point p1, Point p2) {
        return p1.distance(p2);
    }

    @Override
    public double nDimensions() {
        return 2;
    }

    @Override
    public boolean isInTargetRegion(Point p) {
        return target.isInside(p);
    }

    @Override
    public double estimateCostToGo(Point p1) {
        return 0.0;
    }
}
