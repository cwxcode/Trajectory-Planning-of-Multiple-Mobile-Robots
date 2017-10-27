package tt.jointeuclid2ni.probleminstance.generator;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.discretization.L2Heuristic;
import tt.euclid2i.probleminstance.DiscretizedEnvironment;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipprrts.DynamicObstacles;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.jointeuclid2ni.probleminstance.generator.missions.MissionBases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DynamicEnvironmentGenerator {

    private static final int MAX_ATTEMPTS = 4000;

    private final SegmentedTrajectory agentsTrajectory;
    private DiscretizedEnvironment environment;
    private int nAgents;
    private Random seed;

    private MissionBases missions;
    private List<Point> vertices;

    public static DynamicObstacles generateEnvironment(SegmentedTrajectory agentsTrajectory, DiscretizedEnvironment environment, MissionBases bases, int nAgents, int seed) throws ProblemNotCreatedException {
        return new DynamicEnvironmentGenerator(agentsTrajectory, environment, bases, nAgents, seed).generateInstance();
    }

    private DynamicEnvironmentGenerator(SegmentedTrajectory agentsTrajectory, DiscretizedEnvironment environment, MissionBases obstacleBases, int nObstacles, int seed) {
        this.agentsTrajectory = agentsTrajectory;
        this.environment = environment;
        this.missions = obstacleBases;
        this.nAgents = nObstacles;
        this.seed = new Random(seed);

        this.vertices = new ArrayList<Point>(environment.getPlanningGraph().vertexSet());
    }

    private DynamicObstacles generateInstance() throws ProblemNotCreatedException {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            createNewMission();
            if (missions.nAgents() == nAgents)
                return new CreatedEnvironment(missions);
        }
        throw new ProblemNotCreatedException();
    }

    private void createNewMission() {
        MissionBases.Mission mission = generateRandomMission();
        SegmentedTrajectory trajectory = solveMission(mission);

        if (trajectory == null)
            return;

        if (collideDuringObstaclesMovement(trajectory))
            missions.addMission(mission, trajectory);
    }

    private boolean collideDuringObstaclesMovement(SegmentedTrajectory trajectory) {
        return hasConflictWithAgent(trajectory) && !hasConflictAfterObstacleStops(trajectory);
    }

    private boolean hasConflictAfterObstacleStops(SegmentedTrajectory trajectory) {
        tt.euclidtime3i.Point target = Trajectories.end(trajectory);
        tt.euclidtime3i.Point targetMaxTime = new tt.euclidtime3i.Point(target.getPosition(), trajectory.getMaxTime());
        int duration = targetMaxTime.getTime() - target.getTime();

        Straight inTarget = new Straight(target, targetMaxTime);
        BasicSegmentedTrajectory targetArrival = new BasicSegmentedTrajectory(Collections.singletonList(inTarget), duration);

        return hasConflictWithAgent(targetArrival);
    }

    private boolean hasConflictWithAgent(SegmentedTrajectory trajectory) {
        return SeparationDetector.hasConflictAnalytic(trajectory, agentsTrajectory, missions.getBodyRadius() * 2);
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

    private static class CreatedEnvironment implements DynamicObstacles {

        MissionBases missions;

        private CreatedEnvironment(MissionBases missions) {
            this.missions = missions;
        }

        @Override
        public SegmentedTrajectory[] getObstacles() {
            return missions.getTrajectories();
        }

        @Override
        public int[] getObstacleRadiuses() {
            return missions.getBodyRadiuses();
        }

        @Override
        public int getMaxTime() {
            return missions.getMaxTime();
        }
    }
}
