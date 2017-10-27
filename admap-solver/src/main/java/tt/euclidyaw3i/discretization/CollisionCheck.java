package tt.euclidyaw3i.discretization;

import tt.euclid2i.region.Polygon;
import tt.euclidyaw3i.Point;

import java.util.Collection;

public class CollisionCheck {
    static boolean collisionFree(PathSegment candidate, Polygon footprint, Collection<Polygon> obstacles) {
        Point[] waypoints = candidate.getWaypoints();
        for (int i = 0; i < waypoints.length; i++) {
            Polygon transformedFootprint = footprint.getRotated(waypoints[i].getYawInRads()).getTranslated(waypoints[i].getPos());

            for (Polygon obstacle : obstacles) {
                if (transformedFootprint.intersects(obstacle)) {
                    return false;
                }
            }
        }

        return true;
    }
}
