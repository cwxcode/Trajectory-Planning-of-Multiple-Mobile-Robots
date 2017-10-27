package tt.euclid2d.util;

import java.util.Collection;

import tt.euclid2d.Point;
import tt.euclid2d.region.Region;


public class Util {
    public static boolean isVisible(Point start, Point end, Collection<? extends Region> obstacles) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }
}
