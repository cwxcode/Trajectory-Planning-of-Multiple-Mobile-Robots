package tt.euclidtime3i.sipprrts;

import tt.euclid2i.Point;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public interface RRTMission {

    Point getStart();

    Point getTarget();

    int getBodyRadius();

    int getTargetRadius();

    int getMaxSpeed();
}
