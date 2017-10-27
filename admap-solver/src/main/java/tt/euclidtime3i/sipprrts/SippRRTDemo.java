package tt.euclidtime3i.sipprrts;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.heuristics.ZeroHeuristic;

import tt.euclid2i.*;
import tt.euclid2i.Point;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.probleminstance.RandomEnvironment;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclidtime3i.sipprrts.vis.RRTSearchTreeLayer;
import tt.euclidtime3i.vis.TimeParameter;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.generator.ConflictGenerator;
import tt.jointeuclid2ni.probleminstance.generator.exception.ProblemNotCreatedException;
import tt.jointtraj.util.Discretization;
import tt.planner.rrtstar.RRTStar;
import tt.planner.rrtstar.RRTStarListener;
import tt.planner.rrtstar.util.Vertex;
import tt.util.AgentColors;
import tt.vis.*;

import javax.vecmath.Point2d;

import java.awt.*;
import java.util.*;

/**
 * Created by Vojtech Letal on 2/26/14.
 */
public class SippRRTDemo {

    private static final int EXPANDED_STATES = 10000;
    private static final int ITERATION_DELAY = 25;

    private static final int MAX_TIME = 3000;
    private static final int SAMPLING_STEP = 2;
    private static final int GRID_STEP = 20;
    private static final int SPEED = 1;
    private static final int SEED = 115;
    private static final int ENV_SIZE = 1000;
    private static final int OBSTACLE_COUNT = 10;
    private static final int OBSTACLES_SIZE = 200;
    private static final int MAX_RADIUS = 2 * ENV_SIZE;
    private static final int MIN_RADIUS = 100;
    private static final int[][] GRID = LazyGrid.PATTERN_8_WAY;
    private static final int BODY_RADIUS = 65;

    protected Map<SippRRTNode, Vertex<SippRRTNode, SippRRTEdge>> rrtstarVertices;

    private final Environment staticEnv;
    private final DynamicObstacles dynamicEnv;
    private final RRTMission mission;
    private final TimeParameter time = new TimeParameter(10);

    private RRTStar<SippRRTNode, SippRRTEdge> rrtStar;

    public SippRRTDemo(Environment staticEnv, DynamicObstacles dynamicEnv, RRTMission mission) {
        this.staticEnv = staticEnv;
        this.dynamicEnv = dynamicEnv;
        this.mission = mission;
        this.rrtstarVertices = new HashMap<SippRRTNode, Vertex<SippRRTNode, SippRRTEdge>>();
        this.rrtStar = createRRTStar(staticEnv, dynamicEnv, mission);
    }

    public RRTStar<SippRRTNode, SippRRTEdge> createRRTStar(Environment staticEnv, DynamicObstacles dynamicEnv, RRTMission mission) {
        SippRRTDomain domain = new SippRRTDomain(staticEnv, dynamicEnv, mission, SAMPLING_STEP, SEED, MAX_TIME);
        SippRRTNode start = domain.createRoot(mission.getStart());
        SIPPNodeCoordinatesProvider provider = new SIPPNodeCoordinatesProvider();

        return new SippRRTStar(domain, start, MAX_RADIUS);
    }

    private void run() {
        rrtStar.registerListener(new RRTStarListener<SippRRTNode, SippRRTEdge>() {
            @Override
            public void notifyNewVertexInTree(
                    Vertex<SippRRTNode, SippRRTEdge> v) {
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
                        new GraphPathLayer.PathProvider<SippRRTNode, SippRRTEdge>() {
                            @Override
                            public GraphPath<SippRRTNode, SippRRTEdge> getPath() {
                                return (rrtStar.getBestVertex() != null) ? rrtStar.getBestPath() : null;
                            }
                        }, new ProjectionTo2d<SippRRTNode>() {
                            @Override
                            public Point2d project(SippRRTNode point) {
                                return point.getPoint().toPoint2d();
                            }
                        }, Color.RED, Color.RED.darker(), 2, 2
                )
        );
    }

    private void initVisualization() {
        VisManager.setInitParam("SIPP-RRT*", 1024, 768);
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
        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<Point>() {
            @Override
            public Collection<LabeledPointLayer.LabeledPoint<Point>> getLabeledPoints() {
                return Collections.singleton(new LabeledPointLayer.LabeledPoint<Point>(mission.getStart(), "start"));
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));

        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<Point>() {
            @Override
            public Collection<LabeledPointLayer.LabeledPoint<Point>> getLabeledPoints() {
                return Collections.singleton(new LabeledPointLayer.LabeledPoint<Point>(mission.getTarget(), "target"));
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));
    }

    protected void showSearchTreeLayer() {
        final double maxFValueForVis = 3 * mission.getStart().distance(mission.getTarget());

        VisManager.registerLayer(
                RRTSearchTreeLayer.create(
                        new RRTSearchTreeLayer.RRTSearchTreeProvider<SippRRTNode>() {

                            @Override
                            public Collection<SippRRTNode> getOpen() {
                                return rrtstarVertices.keySet();
                            }

                            @Override
                            public Collection<SippRRTNode> getClosed() {
                                return new HashSet<SippRRTNode>();
                            }

                            @Override
                            public SippRRTNode getParent(SippRRTNode vertex) {
                                return rrtstarVertices.get(vertex).getParent().getState();
                            }

                            @Override
                            public double getValue(SippRRTNode vertex) {
                                if (rrtstarVertices.containsKey(vertex)) {
                                    return rrtstarVertices.get(vertex).getCostFromRoot() / maxFValueForVis;
                                } else {
                                    return Double.NaN;
                                }
                            }

                            @Override
                            public SippRRTNode getCurrent() {
                                return rrtStar.getLastSample();
                            }

                            @Override
                            public double getBallRadius() {
                                return rrtStar.getNearBallRadius();
                            }

                        }, new ProjectionTo2d<SippRRTNode>() {
                            @Override
                            public Point2d project(SippRRTNode point) {
                                return point.getPoint().toPoint2d();
                            }
                        }, 4
                )
        );

        // X-time projection
        VisManager.registerLayer(
                RRTSearchTreeLayer.create(
                        new RRTSearchTreeLayer.RRTSearchTreeProvider<SippRRTNode>() {

                            @Override
                            public Collection<SippRRTNode> getOpen() {
                                return rrtstarVertices.keySet();
                            }

                            @Override
                            public Collection<SippRRTNode> getClosed() {
                                return new HashSet<SippRRTNode>();
                            }

                            @Override
                            public SippRRTNode getParent(SippRRTNode vertex) {
                                return rrtstarVertices.get(vertex).getParent().getState();
                            }

                            @Override
                            public double getValue(SippRRTNode vertex) {
                                if (rrtstarVertices.containsKey(vertex)) {
                                    return rrtstarVertices.get(vertex).getCostFromRoot() / maxFValueForVis;
                                } else {
                                    return Double.NaN;
                                }
                            }

                            @Override
                            public SippRRTNode getCurrent() {
                                return rrtStar.getLastSample();
                            }

                            @Override
                            public double getBallRadius() {
                                return rrtStar.getNearBallRadius();
                            }

                        }, new ProjectionTo2d<SippRRTNode>() {
                            @Override
                            public Point2d project(SippRRTNode point) {
                                return new Point2d(point.getPoint().x, -(point.getTime() + 10 ));
                            }
                        }, 4
                )
        );

        // Y-time projection
        VisManager.registerLayer(
                RRTSearchTreeLayer.create(
                        new RRTSearchTreeLayer.RRTSearchTreeProvider<SippRRTNode>() {

                            @Override
                            public Collection<SippRRTNode> getOpen() {
                                return rrtstarVertices.keySet();
                            }

                            @Override
                            public Collection<SippRRTNode> getClosed() {
                                return new HashSet<SippRRTNode>();
                            }

                            @Override
                            public SippRRTNode getParent(SippRRTNode vertex) {
                                return rrtstarVertices.get(vertex).getParent().getState();
                            }

                            @Override
                            public double getValue(SippRRTNode vertex) {
                                if (rrtstarVertices.containsKey(vertex)) {
                                    return rrtstarVertices.get(vertex).getCostFromRoot() / maxFValueForVis;
                                } else {
                                    return Double.NaN;
                                }
                            }

                            @Override
                            public SippRRTNode getCurrent() {
                                return rrtStar.getLastSample();
                            }

                            @Override
                            public double getBallRadius() {
                                return rrtStar.getNearBallRadius();
                            }

                        }, new ProjectionTo2d<SippRRTNode>() {
                            @Override
                            public Point2d project(SippRRTNode point) {
                                return new Point2d(point.getTime() + 1020, point.getPoint().y);
                            }
                        }, 4
                )
        );


        VisManager.registerLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public Trajectory[] getTrajectories() {
                        GraphPath<SippRRTNode, SippRRTEdge> path = rrtStar.getBestPath();
                        if (path == null)
                            return new Trajectory[]{};
                        else
                            return new Trajectory[]{SippRRTUtils.toTrajectory(path, MAX_TIME)};
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
                new ColoredTrajectoriesLayer.TrajectoriesProvider<Point>() {
                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
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
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
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

        new SippRRTDemo(staticEnv, dynamicEnv, mission).run();
    }

    private static DynamicObstacles firstAgentAsDynamicObstacle(EarliestArrivalProblem problem) {
        DirectedGraph<Point, Line> grid = Discretization.createGrid(problem.getEnvironment(), BODY_RADIUS, GRID, GRID_STEP);
        GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(grid, new ZeroHeuristic<Point>(), problem.getStart(0), problem.getTarget(0));
        BasicSegmentedTrajectory trajectory = SegmentedTrajectoryFactory.createConstantSpeedTrajectory(path, 0, SPEED, MAX_TIME, path.getWeight());

        return new DynamicObstaclesImpl(new SegmentedTrajectory[]{trajectory}, new int[]{BODY_RADIUS}, MAX_TIME);
    }

    private static RRTMission secondAgentAsMissionToSolve(EarliestArrivalProblem problem) {
        return new RRTMissionImpl(problem.getStart(1), problem.getTarget(1), BODY_RADIUS, BODY_RADIUS, SPEED);
    }
}
