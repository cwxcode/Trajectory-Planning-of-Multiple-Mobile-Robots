package tt.euclidtime3i.sipp.intervals;

import tt.euclid2i.Geometry2i;
import tt.euclid2i.Line;
import tt.euclid2i.Point;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Vojtech Letal on 3/16/14.
 */
class SIKeyMultipleEdgeProvider<E extends Line> implements SIKeyProvider<E> {

    private Collection<E> edges;

    public SIKeyMultipleEdgeProvider(Collection<E> edges) {
        this.edges = edges;
    }

    @Override
    public Collection<E> getAffectedKeys(Point[] positionOfObstacles, int[] separation) {
        ArrayList<E> lines = new ArrayList<E>();

        for (E line : edges) {
            if (intersectsWithAnyObstacle(line, positionOfObstacles, separation))
                lines.add(line);
        }

        return lines;
    }

    private static boolean intersectsWithAnyObstacle(Line line, Point[] obstacles, int[] separation) {
        for (int i = 0; i < obstacles.length; i++) {
            if (Geometry2i.distance(line, obstacles[i]) < separation[i])
                return true;
        }
        return false;
    }
}
