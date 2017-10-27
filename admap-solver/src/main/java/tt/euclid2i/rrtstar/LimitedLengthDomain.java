package tt.euclid2i.rrtstar;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.ShortestPathProblem;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.planner.rrtstar.util.Extension;

import javax.vecmath.Vector2d;
import java.util.Collection;

public class LimitedLengthDomain extends StraightLineDomain {

    double maxLength;

    public LimitedLengthDomain(ShortestPathProblem problem, double maxLength,
                               int seed, double tryGoalRatio) {
        super(problem, seed, tryGoalRatio);
        this.maxLength = maxLength;
    }

    public LimitedLengthDomain(Rectangle bounds,
                               Collection<Region> obstacles,
                               Region target, Point targetPoint, double maxLength,
                               int seed, double tryGoalRatio) {
        super(bounds, obstacles, target, targetPoint, seed, tryGoalRatio);
        this.maxLength = maxLength;
    }

    @Override
    public Extension<Point, Line> extendTo(Point from, Point to) {
        Extension<Point, Line> result = null;

        Vector2d direction = new Vector2d(to.x - from.x, to.y - from.y);

        if (direction.length() > maxLength) {
            direction.normalize();
            direction.scale(maxLength);
        }

        Point actualEnd = new Point((int) Math.round(from.x + direction.x), (int) Math.round(from.y + direction.y));

        if (Util.isVisible(from, actualEnd, obstacles)) {
            Line maneuver = new Line(from, actualEnd);
            result = new Extension<Point, Line>(from, actualEnd, maneuver, maneuver.getDistance(), to.equals(actualEnd));
        }
        return result;
    }
}
