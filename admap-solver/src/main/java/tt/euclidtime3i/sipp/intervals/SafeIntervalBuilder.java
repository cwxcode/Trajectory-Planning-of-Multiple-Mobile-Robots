package tt.euclidtime3i.sipp.intervals;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.sipprrts.DynamicObstacles;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class SafeIntervalBuilder<T> {

    public static <V extends Point> SafeIntervalBuilder<V> safeIntervalsOfVertices(Set<V> vertices, DynamicObstacles environment, int bodyRadius, int step, int maxTime) {
        return safeIntervals(new SIKeyMultiplePointProvider<V>(vertices), environment, bodyRadius, step, maxTime);
    }

    public static <E extends Line> SafeIntervalBuilder<E> safeIntervalsOfEdges(Set<E> edges, DynamicObstacles environment, int bodyRadius, int step, int maxTime) {
        return safeIntervals(new SIKeyMultipleEdgeProvider<E>(edges), environment, bodyRadius, step, maxTime);
    }

    public static <E extends Line> SafeIntervalList safeIntervalsForSingleEdge(E edge, DynamicObstacles environment, int bodyRadius, int step, int maxTime) {
        SafeIntervalBuilder<E> builder = safeIntervals(new SIKeySingleEdgeProvider<E>(edge), environment, bodyRadius, step, maxTime);
        return builder.getSafeIntervals(edge);
    }

    public static <V extends Point> SafeIntervalList safeIntervalsForSinglePoint(V point, DynamicObstacles environment, int bodyRadius, int step, int maxTime) {
        SafeIntervalBuilder<V> builder = safeIntervals(new SIKeySinglePointProvider<V>(point), environment, bodyRadius, step, maxTime);
        return builder.getSafeIntervals(point);
    }

    public static <K> SafeIntervalBuilder<K> safeIntervals(SIKeyProvider<K> provider, DynamicObstacles environment, int bodyRadius, int step, int maxTime) {
        SafeIntervalBuilder<K> safeIntervalBuilder = new SafeIntervalBuilder<K>(maxTime);

        Trajectory[] obstacles = environment.getObstacles();
        int[] radiuses = environment.getObstacleRadiuses();
        int[] separations = new int[radiuses.length]; //TODO precalculate separations
        Point[] positionsOfObstacles = new Point[obstacles.length];

        for (int time = 0; time <= maxTime; time += step) {
            int i = time / step;

            for (int a = 0; a < obstacles.length; a++) {
                separations[a] = radiuses[a] + bodyRadius;
                positionsOfObstacles[a] = obstacles[a].get(time);
            }

            Collection<K> affectedKeys = provider.getAffectedKeys(positionsOfObstacles, separations);
            for (K key : affectedKeys) {
                safeIntervalBuilder.markCollision(i, time, key);
            }
        }

        return safeIntervalBuilder;
    }

    // ------------ CLASS implementation

    private final HashMap<T, SafeIntervalList> safeIntervalMap;
    private final int maxTime;

    public SafeIntervalBuilder(int maxTime) {
        this.maxTime = maxTime;
        this.safeIntervalMap = new HashMap<T, SafeIntervalList>();
    }

    public SafeIntervalList getSafeIntervals(T element) {
        SafeIntervalList intervals = safeIntervalMap.get(element);

        if (intervals == null) {
            intervals = new SafeIntervalList(maxTime);
            safeIntervalMap.put(element, intervals);
        }

        return intervals;
    }

    public void markCollision(int indexOfSample, int time, T element) {
        SafeIntervalList collection = getSafeIntervals(element);
        collection.markCollisionInTime(indexOfSample, time);
    }

}

