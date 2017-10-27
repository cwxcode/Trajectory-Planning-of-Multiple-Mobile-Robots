package tt.euclidtime3i.sipp.intervals;

import tt.euclid2i.Point;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Vojtech Letal on 3/16/14.
 */
class SIKeySinglePointProvider<V extends Point> implements SIKeyProvider<V> {

    private V point;

    public SIKeySinglePointProvider(V point) {
        this.point = point;
    }

    @Override
    public Collection<V> getAffectedKeys(Point[] positionOfObstacles, int[] separation) {
        if (intersectsWithAnyObstacle(point, positionOfObstacles, separation))
            return Collections.singleton(point);
        else
            return Collections.emptyList();
    }

    private static boolean intersectsWithAnyObstacle(Point point, Point[] obstacles, int[] separation) {
        for (int i = 0; i < obstacles.length; i++) {
            if (point.distance(obstacles[i]) < separation[i])
                return true;
        }
        return false;
    }
}
