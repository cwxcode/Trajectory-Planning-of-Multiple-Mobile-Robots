package tt.jointeuclid2ni.probleminstance.generator.missions;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.DiscretizedEnvironment;
import tt.euclid2i.probleminstance.Environment;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.RelocationTask;

import java.util.Collection;
import java.util.List;

public class MissionsComposedProblem implements EarliestArrivalProblem {

    private DiscretizedEnvironment environment;
    private Missions missions;

    public MissionsComposedProblem(DiscretizedEnvironment environment, Missions missions) {
        this.environment = environment;
        this.missions = missions;
    }

    @Override
    public int nAgents() {
        return missions.nAgents();
    }

    @Override
    public Point getStart(int i) {
        return missions.getStarts()[i];
    }

    @Override
    public Point getTarget(int i) {
        return missions.getTargets()[i];
    }

    @Override
    public int getBodyRadius(int i) {
        return missions.getBodyRadius();
    }

    @Override
    public float getMaxSpeed(int i) {
        return missions.getBodyRadius();
    }

    @Override
    public Point[] getStarts() {
        return missions.getStarts();
    }

    @Override
    public Point[] getTargets() {
        return missions.getTargets();
    }

    @Override
    public int[] getBodyRadiuses() {
        return missions.getBodyRadiuses();
    }

    @Override
    public float[] getMaxSpeeds() {
        return missions.getSpeeds();
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
        return environment.getPlanningGraph();
    }

	@Override
	public Point[] getDocks() {
		return null;
	}

}
