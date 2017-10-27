package tt.euclidtime3i.sipp.intervals;

import tt.euclid2i.Point;

import java.util.Collection;

/**
 * Created by Vojtech Letal on 3/16/14.
 * <p/>
 * This class is used by SafeIntervalBuilder in safeInterval functions. Implementations of the builder function differs
 * in the way they select affected keys in time t. This class serves as a provider of those affected keys.
 * <p/>
 * Most of the cases it could be easily replaced by Lambda expression in Java 8 as it is only a value provider.
 */
interface SIKeyProvider<K> {

    public Collection<K> getAffectedKeys(Point[] positionOfObstacles, int[] separation);

}
