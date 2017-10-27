package tt.jointeuclid2ni.probleminstance;

import tt.euclid2i.Point;

public interface AgentMission {

    public Point getStart();

    public Point getTarget();

    public int getBodyRadius();

    public float getMaxSpeed();

}

