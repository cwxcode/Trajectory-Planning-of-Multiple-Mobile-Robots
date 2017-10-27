package tt.jointeuclid2ni.probleminstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;

public class TrajectoryCoordinationProblemImpl implements TrajectoryCoordinationProblem {

    @SuppressWarnings("serial")
    public static class CannotPlaceAgentsException extends RuntimeException {
    }

    final protected int targetRegionSide = 30;

    protected int nAgents;
    protected Environment environment;
    protected Point[] starts;
    protected Point[] targets;
    protected int[] bodyRadiuses;
    protected float[] maxSpeeds;

    private DirectedGraph<Point, Line> planningGraph;
    protected Point[] docks;
    private List<RelocationTask> relocationTasks;
    
    public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, int[] bodyRadiuses, float[] maxSpeeds, List<RelocationTask> tasks, DirectedGraph<Point, Line> planningGraph) {
    	this(environment, starts, null, bodyRadiuses, maxSpeeds, planningGraph, null, tasks);
    }

    public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, Point[] targets, int[] bodyRadiuses, int[] maxSpeeds, DirectedGraph<Point, Line> planningGraph) {
    	this(environment, starts, targets, bodyRadiuses, maxSpeeds, planningGraph, null, new LinkedList<RelocationTask>());
    }
    
    public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, Point[] targets, int[] bodyRadiuses, DirectedGraph<Point, Line> planningGraph) {
        this(environment, starts, targets, bodyRadiuses, getSpeedArray(starts.length, 1), planningGraph);
    }

    private static int[] getSpeedArray(int nAgents, int speed) {
        int[] speeds = new int[nAgents];
        Arrays.fill(speeds, speed);
        return speeds;
    }
    
    public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, Point[] targets, int[] bodyRadiuses, int[] maxSpeeds, 
    		DirectedGraph<Point, Line> planningGraph, Point[] docks, List<RelocationTask> relocationTasks) {
    	this(environment,starts, targets, bodyRadiuses, toFloatArr(maxSpeeds), planningGraph, docks, relocationTasks);
    }
    
    public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, int[] bodyRadiuses, float[] maxSpeeds, 
    		DirectedGraph<Point, Line> planningGraph, Point[] docks) {
    	this(environment,starts, null, bodyRadiuses, maxSpeeds, planningGraph, docks, null);
    }

    private static float[] toFloatArr(int[] intArr) {
		float[] floatArr = new float[intArr.length];
    	for (int i = 0; i < intArr.length; i++) {
			floatArr[i] = intArr[i]; 
		}
		return floatArr;
	}

	public TrajectoryCoordinationProblemImpl(Environment environment, Point[] starts, Point[] targets, int[] bodyRadiuses, float[] maxSpeeds, 
    		DirectedGraph<Point, Line> planningGraph, Point[] docks, List<RelocationTask> relocationTasks) {
        super();
        this.environment = environment;
        this.starts = starts;
        this.targets = targets;
        this.bodyRadiuses = bodyRadiuses;
        this.maxSpeeds = maxSpeeds;
        this.planningGraph = planningGraph;
        this.docks = docks;
        this.relocationTasks = relocationTasks;
        
        
        if (starts.length == bodyRadiuses.length && (targets == null || targets.length == starts.length))
            nAgents = starts.length;
        else
            throw new RuntimeException("Lengths of arrays differ");
    }

    public TrajectoryCoordinationProblemImpl(Environment environment, int nAgents) {
        this.nAgents = nAgents;
        this.environment = environment;
        this.maxSpeeds = new float[nAgents];
        Arrays.fill(maxSpeeds, 1);

        starts = new Point[nAgents];
        targets = new Point[nAgents];
        bodyRadiuses = new int[nAgents];
    }

    public boolean isUniqueStart(Point point, int bodyRadius) {
        for (int i = 0; i < nAgents; i++) {
            if (starts[i] != null) {
                if (starts[i].distance(point) < bodyRadiuses[i] + bodyRadius) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUniqueTarget(Point point, int bodyRadius) {
        for (int i = 0; i < nAgents; i++) {
            if (targets[i] != null) {
                if (targets[i].distance(point) < bodyRadiuses[i] + bodyRadius) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int nAgents() {
        return starts.length;
    }

    @Override
    public Point getStart(int i) {
        return starts[i];
    }

    @Override
    public Point[] getStarts() {
        return starts;
    }

    @Override
    public Point getTarget(int i) {
    	if (targets != null)
    		return targets[i];
    	else
    		return null;
    }

    @Override
    public tt.euclid2i.Point[] getTargets() {
        return targets;
    }

    @Override
    public int getBodyRadius(int i) {
        return bodyRadiuses[i];
    }

    @Override
    public int[] getBodyRadiuses() {
        return bodyRadiuses;
    }

    @Override
    public float getMaxSpeed(int i) {
        return maxSpeeds[i];
    }

    @Override
    public float[] getMaxSpeeds() {
        return maxSpeeds;
    }

    @Override
    public Collection<Region> getObstacles() {
        return environment.getObstacles();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public DirectedGraph<Point, Line> getPlanningGraph() {
        return planningGraph;
    }

	@Override
	public Point[] getDocks() {
		return docks;
	}

	@Override
	public List<RelocationTask> getRelocationTasks() {
		return relocationTasks;
	}

	@Override
	public List<RelocationTask> getRelocationTasks(int i) {
		List<RelocationTask> agentsTasks = new LinkedList<RelocationTask>();
		for (RelocationTask task : relocationTasks) {
			if (task.getAgentId() == i) {
				agentsTasks.add(task);
			}
		}
		
		return agentsTasks;
	}
}