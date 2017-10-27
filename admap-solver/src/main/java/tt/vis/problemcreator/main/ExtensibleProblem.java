package tt.vis.problemcreator.main;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.Environment;
import tt.jointeuclid2ni.probleminstance.AgentMission;
import tt.jointeuclid2ni.probleminstance.AgentMissionImpl;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.RelocationTask;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExtensibleProblem implements TrajectoryCoordinationProblem {

    private List<Region> obstacles;
    private Region boundary;

    private List<AgentMission> agentMissions;
    private DirectedGraph<Point, Line> planningGraph;
	private ArrayList<Point> docks;

    public ExtensibleProblem() {
        agentMissions = new ArrayList<AgentMission>();
        obstacles = new ArrayList<Region>();
        docks = new ArrayList<Point>();
    }
    
    public ExtensibleProblem(EarliestArrivalProblem problem) {
    	obstacles = new LinkedList<Region>(problem.getObstacles());
    	boundary = problem.getEnvironment().getBoundary();
    	agentMissions = new ArrayList<AgentMission>();
    	for(int i=0; i < problem.nAgents(); i++) {
    		agentMissions.add(new AgentMissionImpl(problem, i));
    	}
    	planningGraph = problem.getPlanningGraph();
    	
    	docks = new ArrayList<Point>();
    	if (problem.getDocks() != null) {
    		docks.addAll(Arrays.asList(problem.getDocks()));
    	}
    }

    @Override
    public int nAgents() {
        return agentMissions.size();
    }

    public int addAgent(Point start, Point target, int radius, int maxSpeed) {
        return addAgent(new AgentMissionImpl(start, target, radius, maxSpeed));
    }

    public int addAgent(AgentMission mission) {
        agentMissions.add(mission);
        return nAgents();
    }

    public void removeAgent(int ithAgent) {
        agentMissions.remove(ithAgent);
    }

    public void removeAgent(AgentMission mission) {
        agentMissions.remove(mission);
    }

    public void clearAgentMissions() {
        agentMissions.clear();
    }

    public void addObstacle(Region obstacle) {
        obstacles.add(obstacle);
    }
    
    public void addDock(Point e) {
    	docks.add(e);
    }

    public void setObstacles(List<Region> obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public Collection<Region> getObstacles() {
        return obstacles;
    }

    public Region getBoundary() {
        return boundary;
    }

    public void setBoundary(Region boundary) {
        this.boundary = boundary;
    }

    public void setEnvironment(Environment environment) {
        this.boundary = environment.getBoundary();
        this.obstacles = new ArrayList<Region>(environment.getObstacles());
    }

    @Override
    public Point getStart(int i) {
        return getAgent(i).getStart();
    }

    @Override
    public Point getTarget(int i) {
        return getAgent(i).getTarget();
    }

    private AgentMission getAgent(int i) {
        return agentMissions.get(i);
    }

    @Override
    public int getBodyRadius(int i) {
        return getAgent(i).getBodyRadius();
    }

    @Override
    public float getMaxSpeed(int i) {
        return getAgent(i).getMaxSpeed();
    }

    @Override
    public Point[] getStarts() {
        Point[] starts = new Point[nAgents()];
        for (int i = 0; i < nAgents(); i++) {
            starts[i] = getAgent(i).getStart();
        }
        return starts;
    }

    @Override
    public Point[] getTargets() {
        Point[] targets = new Point[nAgents()];
        for (int i = 0; i < nAgents(); i++) {
            targets[i] = getAgent(i).getTarget();
        }
        return targets;
    }

    @Override
    public int[] getBodyRadiuses() {
        int[] radiuses = new int[nAgents()];
        for (int i = 0; i < nAgents(); i++) {
            radiuses[i] = getAgent(i).getBodyRadius();
        }
        return radiuses;
    }

    @Override
    public float[] getMaxSpeeds() {
        float[] speeds = new float[nAgents()];
        for (int i = 0; i < nAgents(); i++) {
            speeds[i] = getAgent(i).getMaxSpeed();
        }
        return speeds;
    }


    @Override
    public Environment getEnvironment() {
        return new Environment() {

            @Override
            public Collection<Region> getObstacles() {
                return obstacles;
            }

            @Override
            public Region getBoundary() {
                return boundary;
            }
        };
    }

    @Override
    public DirectedGraph<Point, Line> getPlanningGraph() {
        return planningGraph;
    }

    public void setPlanningGraph(DirectedGraph<Point, Line> planningGraph) {
        this.planningGraph = planningGraph;
    }

	@Override
	public Point[] getDocks() {
		return docks.toArray(new Point[0]);
	}
	
	public AgentMission[] getAgentMissions() {
		return agentMissions.toArray(new AgentMission[0]);
	}

	@Override
	public List<RelocationTask> getRelocationTasks(int i) {
		return null;
	}

	@Override
	public List<RelocationTask> getRelocationTasks() {
		return null;
	}
}