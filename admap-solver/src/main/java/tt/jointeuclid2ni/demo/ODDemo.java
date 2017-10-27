package tt.jointeuclid2ni.demo;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.HeuristicProvider;
import org.jgrapht.util.heuristics.PerfectHeuristic;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.AdditionalPointsExtension;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.probleminstance.DiscretizedEnvironmentImpl;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.probleminstance.RandomEnvironment;
import tt.euclid2i.util.Util;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclidtime3i.vis.TimeParameter;
import tt.jointeuclid2ni.operatordecomposition.ODEdge;
import tt.jointeuclid2ni.operatordecomposition.ODNode;
import tt.jointeuclid2ni.operatordecomposition.ODUtils;
import tt.jointeuclid2ni.operatordecomposition.ODWrapper;
import tt.jointeuclid2ni.operatordecomposition.utils.ODGoal;
import tt.jointeuclid2ni.operatordecomposition.utils.ODHeuristic;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.generator.ProblemGenerator;
import tt.jointeuclid2ni.probleminstance.generator.missions.MissionBases;
import tt.util.AgentColors;
import tt.vis.*;

import javax.vecmath.Point2d;
import java.awt.*;
import java.util.*;

public class ODDemo {

    //TODO does not work with 50/50 settings!

    private static final int AGENTS = 2;
    private static final int BODY_RADIUS = 60;
    private static final int GRID_STEP = 30;
    private static final int SPEED = 1;
    private static final int SEED = 10;
    private static final int MAX_TIME = 3000;
    private static final int[][] PATTERN = LazyGrid.PATTERN_4_WAY;

    private Trajectory[] solution;

    public static void main(String[] args) {
        new ODDemo().create();
    }

    private EarliestArrivalProblem getProblem() {
        Environment environment = new RandomEnvironment(1000, 1000, 5, 300, SEED);
        DirectedGraph<Point, Line> graph = LazyGrid.zeroOriginGrid(environment, PATTERN, GRID_STEP).generateFullGraph();

        DiscretizedEnvironmentImpl discretizedEnvironment = new DiscretizedEnvironmentImpl(environment, graph);
        MissionBases bases = new MissionBases(BODY_RADIUS, SPEED, MAX_TIME);

        return ProblemGenerator.generateInstance(discretizedEnvironment, bases, AGENTS, SEED);
    }

    public void create() {
        EarliestArrivalProblem problem = getProblem();
        DirectedGraph<Point, Line>[] graph = createGraphs(problem);

        ODWrapper odWrapper = new ODWrapper(graph, problem.getTargets(), GRID_STEP, true);
        ODHeuristic odHeuristic = new ODHeuristic(graph, problem.getTargets(), new HeuristicProvider<Point, Line>() {
            @Override
            public HeuristicToGoal<Point> getHeuristicToGoal(DirectedGraph<Point, Line> graph, Point goal) {
                return new PerfectHeuristic<Point, Line>(graph, goal);
            }
        });
        ODGoal goal = new ODGoal(problem.getTargets());
        ODNode start = ODNode.start(problem.getStarts(), problem.getBodyRadiuses());

        initVisualization();
        visualizeProblem(problem, problem.getPlanningGraph());

        GraphPath<ODNode, ODEdge> path = AStarShortestPathSimple.findPathBetween(odWrapper, odHeuristic, start, goal);
        solution = ODUtils.toTrajectories(path, problem.getTargets(), MAX_TIME, SPEED, GRID_STEP);
    }

    private DirectedGraph<Point, Line>[] createGraphs(EarliestArrivalProblem problem) {
        DirectedGraph<Point, Line> graph[] = new DirectedGraph[problem.nAgents()];
        Arrays.fill(graph, problem.getPlanningGraph());
        return graph;
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
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

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

    private void visualizeProblem(final EarliestArrivalProblem problem, final Graph<Point, Line> graph) {

        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        LinkedList<Region> list = new LinkedList<Region>();
                        list.add(problem.getEnvironment().getBoundary());
                        return list;
                    }

                }, Color.BLACK, Color.WHITE));

        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {

                    @Override
                    public Collection<Region> getRegions() {
                        return problem.getObstacles();
                    }

                }, Color.GRAY, Color.GRAY));

        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<Point>() {

            @Override
            public Collection<LabeledPointLayer.LabeledPoint<Point>> getLabeledPoints() {
                LinkedList<LabeledPointLayer.LabeledPoint<Point>> list = new LinkedList<LabeledPointLayer.LabeledPoint<Point>>();

                for (int i = 0; i < problem.getStarts().length; i++) {
                    list.add(new LabeledPointLayer.LabeledPoint<Point>(problem.getStarts()[i], "s" + i));
                }
                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.BLUE));


        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointLayer.LabeledPointsProvider<Point>() {

            @Override
            public Collection<LabeledPointLayer.LabeledPoint<Point>> getLabeledPoints() {
                LinkedList<LabeledPointLayer.LabeledPoint<Point>> list = new LinkedList<LabeledPointLayer.LabeledPoint<Point>>();

                for (int i = 0; i < problem.getTargets().length; i++) {
                    list.add(new LabeledPointLayer.LabeledPoint<Point>(problem.getTarget(i), "g" + i));
                }
                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));

        final TimeParameter time = new TimeParameter(20);
        VisManager.registerLayer(ParameterControlLayer.create(time));

        // visualize the graph
        VisManager.registerLayer(GraphLayer.create(new GraphLayer.GraphProvider<Point, Line>() {

            @Override
            public Graph<Point, Line> getGraph() {
                return problem.getPlanningGraph();
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));

        KeyToggleLayer bKeyLayer = KeyToggleLayer.create("b");
        bKeyLayer.addSubLayer(ColoredTrajectoriesLayer.create(
                new ColoredTrajectoriesLayer.TrajectoriesProvider<Point>() {
                    @Override
                    public tt.discrete.Trajectory<Point>[] getTrajectories() {
                        return solution;
                    }
                }, new ColoredTrajectoriesLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, new tt.euclid2i.vis.ProjectionTo2d(), 10, MAX_TIME, 6, 's'
        ));
        bKeyLayer.addSubLayer(FastAgentsLayer.create(
                new FastAgentsLayer.TrajectoriesProvider() {

                    @Override
                    public Trajectory[] getTrajectories() {
                        return solution;
                    }

                    @Override
                    public int[] getBodyRadiuses() {
                        return problem.getBodyRadiuses();
                    }

                }, new FastAgentsLayer.ColorProvider() {
                    @Override
                    public Color getColor(int i) {
                        return AgentColors.getColorForAgent(i);
                    }
                }, time
        ));

        VisManager.registerLayer(bKeyLayer);
        VisManager.init();
    }


    private DirectedGraph<Point, Line> createGrid(EarliestArrivalProblem problem, int i) {
        Set<Point> points = new HashSet<Point>();
        points.addAll(Arrays.asList(problem.getStarts()));
        points.addAll(Arrays.asList(problem.getTargets()));

        Collection<Region> inflatedObstacles = Util.inflateRegions(problem.getObstacles(), problem.getBodyRadius(i));

        // create discretization
        final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph
                = new AdditionalPointsExtension(
                new LazyGrid(problem.getStart(i), inflatedObstacles, problem.getEnvironment().getBoundary().getBoundingBox(),
                        PATTERN, GRID_STEP).generateFullGraph() //it's important to generate the full graph
                , points, GRID_STEP);

        // create spatio-temporal graph
        return spatialGraph;
    }

    private int argmin(int... arr) {
        int min = Integer.MAX_VALUE;
        int argmin = -1;

        for (int i = 0; i < arr.length; i++) {
            if (min > arr[i]) {
                min = arr[i];
                argmin = i;
            }
        }

        return argmin;
    }
}
