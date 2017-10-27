package tt.euclidtime3i.sipp.intervals;

import tt.euclid2i.Geometry2i;
import tt.euclid2i.Line;
import tt.euclid2i.Point;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Vojtech Letal on 3/16/14.
 */
class SIKeySingleEdgeProvider<E extends Line> implements SIKeyProvider<E> {
    private E edge;

    public SIKeySingleEdgeProvider(E edge) {
        this.edge = edge;
    }

    @Override
    public Collection<E> getAffectedKeys(Point[] positionOfObstacles, int[] separation) {
        if (intersectsWithAnyObstacle(edge, positionOfObstacles, separation))
            return Collections.singleton(edge);
        else
            return Collections.emptyList();
    }

    private static boolean intersectsWithAnyObstacle(Line line, Point[] obstacles, int[] separation) {
        for (int i = 0; i < obstacles.length; i++) {
            if (Geometry2i.distance(line, obstacles[i]) < separation[i])
                return true;
        }
        return false;
    }
}