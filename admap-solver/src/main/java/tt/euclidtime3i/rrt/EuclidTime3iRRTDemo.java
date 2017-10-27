package tt.euclidtime3i.rrt;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.heuristics.ZeroHeuristic;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.probleminstance.RandomEnvironment;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.sipprrts.DynamicObstacles;
import tt.euclidtime3i.sipprrts.DynamicObstaclesImpl;
import tt.euclidtime3i.sipprrts.RRTMission;
import tt.euclidtime3i.sipprrts.RRTMissionImpl;
import tt.euclidtime3i.sipprrts.vis.RRTSearchTreeLayer;
import tt.euclidtime3i.vis.TimeParameter;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.generator.ConflictGenerator;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.jointtraj.util.Discretization;
import tt.planner.rrtstar.EuclideanRRTStar;
import tt.planner.rrtstar.RRTStar;
import tt.planner.rrtstar.RRTStarListener;
import tt.planner.rrtstar.util.Vertex;
import tt.util.AgentColors;
import tt.vis.*;

import javax.vecmath.Point2d;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Vojtech Letal on 3/7/14.
 */
public class EuclidTime3iRRTDemo {
    private static final int EXPANDED_STATES = 10000;
    private static final int ITERATION_DELAY = 25;

    private static final int MAX_TIME = 300;
    private static final int GRID_STEP = 20;
    private static final int SPEED = 4;
    private static final int SEED = 331;
    private static final int ENV_SIZE = 1000;
    private static final int OBSTACLE_COUNT = 10;
    private static final int OBSTACLES_SIZE = 200;
    private static final int MAX_RADIUS = ENV_SIZE;
    private static final int MIN_RADIUS = 25;
    private static final int[][] GRID = LazyGrid.PATTERN_8_WAY;
    private static final int BODY_RADIUS = 40;

    protected Map<tt.euclidtime3i.Point, Vertex<tt.euclidtime3i.Point, List<Straight>>> rrtstarVertices;

    private final Environment staticEnv;
    private final DynamicObstacles dynamicEnv;
    private final RRTMission mission;
    private final TimeParameter time = new TimeParameter(5);

    private RRTStar<tt.euclidtime3i.Point, List<Straight>> rrtStar;

    public EuclidTime3iRRTDemo(Environment staticEnv, DynamicObstacles dynamicEnv, RRTMission mission) {
        this.staticEnv = staticEnv;
        this.dynamicEnv = dynamicEnv;
        this.mission = mission;
        this.rrtstarVertices = new HashMap<tt.euclidtime3i.Point, Vertex<tt.euclidtime3i.Point, List<Straight>>>();
        this.rrtStar = createRRTStar(staticEnv, dynamicEnv, mission);
    }

    public RRTStar<tt.euclidtime3i.Point, List<Straight>> createRRTStar(Environment staticEnv, DynamicObstacles dynamicEnv, RRTMission mission) {
        EuclidTime3iDomain domain = new EuclidTime3iDomain(staticEnv, dynamicEnv, mission, GRID_STEP, SEED);
        tt.euclidtime3i.Point start = new tt.euclidtime3i.Point(mission.getStart(), 0);
        EuclidTime3iCoordinateProvider provider = new EuclidTime3iCoordinateProvider();

        return new EuclideanRRTStar<tt.euclidtime3i.Point, List<Straight>>(domain, provider, start, MAX_RADIUS, MIN_RADIUS, MAX_RADIUS);
    }

    private void run() {
        rrtStar.registerListener(new RRTStarListener<tt.euclidtime3i.Point, List<Straight>>() {
            @Override
            public void notifyNewVertexInTree(
                    Vertex<tt.euclidtime3i.Point, List<Straight>> v) {
                rrtstarVertices.put(v.getState(), v);
            }
        });

        initVisualization();
        showStaticEnvironment();
        showSearchTreeLayer();
        showDynamicEnvironment();
        showNearBallRadius();
        showGraphPathLayer();

        showMission();
        showVisInfoLayer();

        do {
            rrtStar.iterateAlt();
            try {
                Thread.sleep(ITERATION_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (rrtStar.ext_counter < EXPANDED_STATES);
    }

    private void showVisInfoLayer() {
        VisManager.registerLayer(VisInfoLayer.create());
    }

    private void showGraphPathLayer() {
        VisManager.registerLayer(
                GraphPathLayer.create(
                        new GraphPathLayer.PathProvider<tt.euclidtime3i.Point, List<Straight>>() {
                            @Override
                            public GraphPath<tt.euclidtime3i.Point, List<Straight>> getPath() {
                                return (rrtStar.getBestVertex() != null) ? rrtStar.getBestPath() : null;
                            }
                        }, new ProjectionTo2d<tt.euclidtime3i.Point>() {
                            @Override
                            public Point2d project(tt.euclidtime3i.Point point) {
                                return point.getPosition().toPoint2d();
                            }
                        }, Color.RED, Color.RED.darker(), 2, 2
                ));
    }

    private void initVisualization() {
        VisManager.setInitParam("EuclidTime3iRRT*Demo", 1024, 768);
        VisManager.setSceneParam(new VisManager.SceneParams() {
            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500, 500);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            }
        });
        VisManager.init();
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));
        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<Region> getRegions() {
                return Collections.singleton(staticEnv.getBoundary());
            }
        }, Color.BLACK, Color.WHITE));

    }

    private void showStaticEnvironment() {

        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<Region> getRegions() {
                return staticEnv.getObstacles();
            }
        }, Color.GRAY, Color.GRAY));
    }

    private void showMission() {
        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<tt.euclid2i.Point>() {
            @Override
            public Collection<LabeledPointLayer.LabeledPoint<tt.euclid2i.Point>> getLabeledPoints() {
                return Collections.singleton(new LabeledPointLayer.LabeledPoint<tt.euclid2i.Point>(mission.getStart(), "start"));
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));

        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<tt.euclid2i.Point>() {
            @Override
            public Collection<LabeledPointLayer.LabeledPoint<tt.euclid2i.Point>> getLabeledPoints() {
                return Collections.singleton(new LabeledPointLayer.LabeledPoint<tt.euclid2i.Point>(mission.getTarget(), "target"));
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));
    }

    protected void showSearchTreeLayer() {
        final double maxFValueForVis = 3 * mission.getStart().distance(mission.getTarget());

        VisManager.registerLayer(
                RRTSearchTreeLayer.create(
                        new RRTSearchTreeLayer.RRTSearchTreeProvider<tt.euclidtime3i.Point>() {

                            @Override
                            public Collection<tt.euclidtime3i.Point> getOpen() {
                                return rrtstarVertices.keySet();
                            }

                            @Override
                            public Collection<tt.euclidtime3i.Point> getClosed() {
                                return new HashSet<tt.euclidtime3i.Point>();
                            }

                            @Override
                            public tt.euclidtime3i.Point getParent(tt.euclidtime3i.Point vertex) {
                                return rrtstarVertices.get(vertex).getParent().getState();
                            }

                            @Override
                            public double getValue(tt.euclidtime3i.Point vertex) {
                                if (rrtstarVertices.containsKey(vertex)) {
                                    return rrtstarVertices.get(vertex).getCostFromRoot() / maxFValueForVis;
                                } else {
                                    return Double.NaN;
                                }
                            }

                            @Override
                            public tt.euclidtime3i.Point getCurrent() {
                                return rrtStar.getLastSample();
                            }

                            @Override
                            public double getBallRadius() {
                                return rrtStar.getNearBallRadius();
                            }

                        }, new ProjectionTo2d<tt.euclidtime3i.Point>() {
                            @Override
                            public Point2d project(tt.euclidtime3i.Point point) {
                                return new Point2d(point.x, point.y);
                            }
                        }, 4
                ));

        VisManager.registerLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public Trajectory[] getTrajectories() {
                        GraphPath<tt.euclidtime3i.Point, List<Straight>> path = rrtStar.getBestPath();
                        if (path == null)
                            return new Trajectory[]{};
                        else
                            return new Trajectory[]{EuclidTime3iUtils.toTrajectory(path, MAX_TIME)};
                    }

                    @Override
                    public int[] getBodyRadiuses() {
                        return new int[]{BODY_RADIUS};
                    }

                }, new FastAgentsLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return Color.red;
                    }
                }, time
        ));
    }

    private void showDynamicEnvironment() {
        VisManager.registerLayer(ParameterControlLayer.create(time));
        VisManager.registerLayer(ColoredTrajectoriesLayer.create(
                new ColoredTrajectoriesLayer.TrajectoriesProvider<tt.euclid2i.Point>() {
                    @Override
                    public tt.discrete.Trajectory<tt.euclid2i.Point>[] getTrajectories() {
                        return dynamicEnv.getObstacles();
                    }
                }, new ColoredTrajectoriesLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), 5, MAX_TIME, 6, 's'
        ));
        VisManager.registerLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public tt.discrete.Trajectory<tt.euclid2i.Point>[] getTrajectories() {
                        return dynamicEnv.getObstacles();
                    }

                    @Override
                    public int[] getBodyRadiuses() {
                        return new int[]{BODY_RADIUS};
                    }

                }, new FastAgentsLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, time
        ));
    }

    protected void showNearBallRadius() {
        VisManager.registerLayer(LineHUDLayer.create(new LineHUDLayer.TextProvider() {
            @Override
            public String getText() {
                return "Ball radius: " + rrtStar.getNearBallRadius();
            }
        }, Color.BLUE));
    }

    // ---------- main

    public static void main(String[] args) throws ProblemNotCreatedException {
        RandomEnvironment staticEnv = new RandomEnvironment(ENV_SIZE, ENV_SIZE, OBSTACLE_COUNT, OBSTACLES_SIZE, SEED);
        DirectedGraph<Point, Line> grid = Discretization.createGrid(staticEnv, BODY_RADIUS, GRID, GRID_STEP);
        EarliestArrivalProblem problem = ConflictGenerator.generateInstance(
        		staticEnv, 
        		grid,
        		grid.vertexSet(),        		
        		new int[]{BODY_RADIUS, BODY_RADIUS},
                new float[]{1.0f, 1.0f},
                200,
                true, 
                false,
                false,
                new Random(SEED));

        DynamicObstacles dynamicEnv = firstAgentAsDynamicObstacle(problem);
        RRTMission mission = secondAgentAsMissionToSolve(problem);

        new EuclidTime3iRRTDemo(staticEnv, dynamicEnv, mission).run();
    }

    private static DynamicObstacles firstAgentAsDynamicObstacle(EarliestArrivalProblem problem) {
        DirectedGraph<tt.euclid2i.Point, Line> grid = Discretization.createGrid(problem.getEnvironment(), BODY_RADIUS, GRID, GRID_STEP);
        GraphPath<tt.euclid2i.Point, Line> path = AStarShortestPathSimple.findPathBetween(grid, new ZeroHeuristic<tt.euclid2i.Point>(), problem.getStart(0), problem.getTarget(0));
        BasicSegmentedTrajectory trajectory = SegmentedTrajectoryFactory.createConstantSpeedTrajectory(path, 0, SPEED, MAX_TIME, path.getWeight());

        return new DynamicObstaclesImpl(new SegmentedTrajectory[]{trajectory}, new int[]{BODY_RADIUS}, MAX_TIME);
    }

    private static RRTMission secondAgentAsMissionToSolve(EarliestArrivalProblem problem) {
        return new RRTMissionImpl(problem.getStart(1), problem.getTarget(1), BODY_RADIUS, BODY_RADIUS, SPEED);
    }
}
