package tt.jointeuclid2ni.probleminstance;

import java.util.Collection;
import java.util.List;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;

public interface EarliestArrivalProblem {

    public int nAgents();

    public Point getStart(int i);

    public Point getTarget(int i);

    public int getBodyRadius(int i);

    public float getMaxSpeed(int i);

    public Point[] getStarts();

    public Point[] getTargets();

    public int[] getBodyRadiuses();

    public float[] getMaxSpeeds();

    public Collection<Region> getObstacles();

    public Environment getEnvironment();

    public DirectedGraph<Point, Line> getPlanningGraph();
    
    public Point[] getDocks();

}

