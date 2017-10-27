package tt.jointeuclid2ni.probleminstance.generator;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.discretization.L2Heuristic;
import tt.euclid2i.probleminstance.DiscretizedEnvironment;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclid2i.util.SeparationDetector;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.jointeuclid2ni.probleminstance.generator.missions.MissionBases;
import tt.jointeuclid2ni.probleminstance.generator.missions.MissionsComposedProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProblemGenerator {

    private static final int MAX_ATTEMPTS = 4000;

    private DiscretizedEnvironment environment;
    private int nAgents;
    private Random seed;

    private MissionBases missions;
    private List<Point> vertices;

    public static EarliestArrivalProblem generateInstance(DiscretizedEnvironment environment, MissionBases bases, int nAgents, int seed) throws ProblemNotCreatedException {
        return new ProblemGenerator(environment, bases, nAgents, seed).generateInstance();
    }

    private ProblemGenerator(DiscretizedEnvironment environment, MissionBases missions, int nAgents, int seed) {
        this.environment = environment;
        this.missions = missions;
        this.nAgents = nAgents;
        this.seed = new Random(seed);

        this.vertices = new ArrayList<Point>(environment.getPlanningGraph().vertexSet());
    }

    private EarliestArrivalProblem generateInstance() throws ProblemNotCreatedException {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            createNewMission();
            if (missions.nAgents() == nAgents)
                return new MissionsComposedProblem(environment, missions);
        }
        throw new ProblemNotCreatedException();
    }

    private void createNewMission() {
        MissionBases.Mission mission = generateRandomMission();
        SegmentedTrajectory trajectory = solveMission(mission);

        if (trajectory == null)
            return;

        if (missions.nAgents() == 0 || SeparationDetector.hasAnyPairwiseConflictAnalytic(trajectory, missions.getTrajectories(), missions.getBodyRadius() * 2))
            missions.addMission(mission, trajectory);
    }

    private SegmentedTrajectory solveMission(MissionBases.Mission mission) {
        L2Heuristic heuristic = new L2Heuristic(mission.target);
        GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(environment.getPlanningGraph(), heuristic, mission.start, mission.target);

        if (path == null)
            return null;
        else
            return SegmentedTrajectoryFactory.createConstantSpeedTrajectory(path, 0, (int) missions.getSpeed(), missions.getMaxTime(), path.getWeight());
    }

    private MissionBases.Mission generateRandomMission() {
        Point start = randomNotCollidingPoint(missions.getStarts());
        Point target = randomNotCollidingPoint(missions.getTargets());

        MissionBases.Mission mission = new MissionBases.Mission(start, target);
        return mission;
    }

    private Point randomNotCollidingPoint(Point[] otherStarts) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            Point point = randomVertex();
            if (!collides(point, otherStarts))
                return point;
        }
        throw new ProblemNotCreatedException();
    }

    private boolean collides(Point point, Point[] otherPoints) {
        for (Point other : otherPoints) {
            if (other.distance(point) < missions.getBodyRadius())
                return true;
        }
        return false;
    }

    private Point randomVertex() {
        if (vertices.isEmpty())
            throw new IllegalArgumentException("Given graph has an empty vertex set");

        int random = seed.nextInt(vertices.size());
        return vertices.get(random);
    }

}
