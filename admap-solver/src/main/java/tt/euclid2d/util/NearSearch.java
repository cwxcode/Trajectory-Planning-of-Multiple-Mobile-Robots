package tt.euclid2d.util;

import java.util.Collection;
import java.util.HashSet;

import tt.euclid2d.Point;

public class NearSearch {

    /**
     * Finds all points in a collection of points that are near given point.
     * @return
     *
     */
    public static Collection<Point> findNear(Point p, Collection<Point> candidates, double nearRadius) {
        Collection<Point> near = new HashSet<Point>();

        for (Point q : candidates) {
            if (p.distance(q) < nearRadius) {
                near.add(q);
            }
        }

        return near;
    }


}
