package tt.euclidtime3i.sipprrts;

import tt.euclid2i.SegmentedTrajectory;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public class DynamicObstaclesImpl implements DynamicObstacles {

    private SegmentedTrajectory[] obstacles;
    private int[] radiuses;
    private int maxTime;

    public DynamicObstaclesImpl(SegmentedTrajectory[] obstacles, int[] radiuses, int maxTime) {
        this.obstacles = obstacles;
        this.radiuses = radiuses;
        this.maxTime = maxTime;
    }

    @Override
    public SegmentedTrajectory[] getObstacles() {
        return obstacles;
    }

    @Override
    public int[] getObstacleRadiuses() {
        return radiuses;
    }

    @Override
    public int getMaxTime() {
        return maxTime;
    }

}
