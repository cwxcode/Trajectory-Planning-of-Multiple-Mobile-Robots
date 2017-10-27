package tt.euclidtime3i.sipprrts;

import tt.euclid2i.SegmentedTrajectory;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public interface DynamicObstacles {

    public SegmentedTrajectory[] getObstacles();

    public int[] getObstacleRadiuses();

    public int getMaxTime();
}
