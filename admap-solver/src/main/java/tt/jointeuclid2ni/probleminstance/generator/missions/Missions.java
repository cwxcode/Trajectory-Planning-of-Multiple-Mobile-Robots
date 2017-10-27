package tt.jointeuclid2ni.probleminstance.generator.missions;

import tt.euclid2i.Point;

/**
 * Created by Vojtech Letal on 3/4/14.
 */

public interface Missions {

    public int nAgents();

    public int getBodyRadius();

    public float getSpeed();

    public Point[] getStarts();

    public Point[] getTargets();

    int[] getBodyRadiuses();

    float[] getSpeeds();
}
