package tt.jointeuclid2ni.demo;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.discretization.ToGoalEdgeExtension;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.SynchronizedMovesTimeExtension;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.vis.TimeParameter;
import tt.euclidtime3i.vis.TimeParameterProjectionTo2d;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.SuperconflictProblem;
import tt.jointtraj.solver.SearchResult;
import tt.jointtrajineuclidtime3i.solver.PrioritizedPlanningSolver;
import tt.vis.CylindricAgentsLayer;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.LabeledPointLayer;
import tt.vis.LabeledPointLayer.LabeledPoint;
import tt.vis.LabeledPointLayer.LabeledPointsProvider;
import tt.vis.ParameterControlLayer;
import tt.vis.TrajectoriesLayer;
import tt.vis.TrajectoriesLayer.TrajectoriesProvider;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class EightAgentsPrioritizedPlanningDemo implements Creator {

    private static final int MAXTIME = 10000;
    private static final int GRID_STEP = 100;
    private static final int SPEED = 1;
    private static final int TIME_STEP = GRID_STEP * SPEED;
    TimeParameter time;

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {

        initVisualization();

        // create time parameter

        time = new TimeParameter(20);
        VisManager.registerLayer(ParameterControlLayer.create(time));

        // create a random problem

        EarliestArrivalProblem problem = new SuperconflictProblem(8, 50);
        visualizeProblem(problem);

        // solve the problem

        final Trajectory[] solution = solve(problem);

        // show the solution

        if (solution != null) {
            visualizeSolution(solution, problem.getBodyRadiuses());
        } else {
            System.out.println("No solution found");
        }
    }

    private void visualizeSolution(final Trajectory[] solution, int[] bodyRadiuses) {
        VisManager.registerLayer(TrajectoriesLayer.create(new TrajectoriesProvider<tt.euclid2i.Point>() {
            @Override
            public tt.discrete.Trajectory<tt.euclid2i.Point>[] getTrajectories() {
                return solution;
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), 10, MAXTIME, 6, 's'));


        VisManager.registerLayer(CylindricAgentsLayer.create(new CylindricAgentsLayer.TrajectoriesProvider() {

            @Override
            public Trajectory[] getTrajectories() {
                return solution;
            }
        }, new TimeParameterProjectionTo2d(time), bodyRadiuses, 1));
    }

    private Trajectory[] solve(EarliestArrivalProblem problem) {

        tt.euclid2i.Point[] starts = new tt.euclid2i.Point[problem.nAgents()];
        tt.euclid2i.Point[] goals = new tt.euclid2i.Point[problem.nAgents()];
        DirectedGraph<Point, Straight>[] motions = new DirectedGraph[problem.nAgents()];

        for (int i = 0; i < problem.nAgents(); i++) {
            starts[i] = problem.getStart(i);
            goals[i] = problem.getTarget(i);
            motions[i] = createMotionGraph(starts[i], goals[i], problem.getEnvironment().getBoundary().getBoundingBox(), problem.getObstacles(), MAXTIME);
        }

        PrioritizedPlanningSolver solver = new PrioritizedPlanningSolver(starts,
                goals,
                motions,
                problem.getBodyRadiuses(),
                PrioritizedPlanningSolver.VARIABLE_TIMESTEP,
                TIME_STEP/4,
                MAXTIME);

        final SearchResult result = solver.solve(24*3600*1000 /* 1 day runtime limit*/);

        if (result.foundSolution()) {
            System.out.println("Found a solution: " + result.trajectories);
            return result.trajectories;
        } else {
            System.out.println("Haven't found any solution...");
            return null;
        }
    }

    private DirectedGraph<Point, Straight> createMotionGraph(final tt.euclid2i.Point init,
                                                             final tt.euclid2i.Point goal, 
                                                             Rectangle bounds, 
                                                             Collection<tt.euclid2i.Region> obstacles, 
                                                             int maxTime) {

        LazyGrid grid = new LazyGrid(init,
                obstacles,
                bounds,
                LazyGrid.PATTERN_8_WAY_WAIT,
                GRID_STEP);

        // create discretization
        final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph
                = new ToGoalEdgeExtension(grid, goal, GRID_STEP);

        // graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<tt.euclid2i.Point, tt.euclid2i.Line>() {

            @Override
            public Graph<tt.euclid2i.Point, tt.euclid2i.Line> getGraph() {
                return ((ToGoalEdgeExtension) spatialGraph).generateFullGraph(init);
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));

        // create spatio-temporal graph
        DirectedGraph<Point, Straight> motions 
        	= new ConstantSpeedTimeExtension(spatialGraph, MAXTIME, new float[]{SPEED}, new LinkedList<MovingCircle>(), TIME_STEP, TIME_STEP);
        
        return motions;
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams() {

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

    private void visualizeProblem(final EarliestArrivalProblem problem) {

        // draw bounds
        VisManager.registerLayer(RegionsLayer.create(
                new RegionsProvider() {

                    @Override
                    public Collection getRegions() {
                        LinkedList<tt.euclid2i.region.Rectangle> list = new LinkedList<tt.euclid2i.region.Rectangle>();
                        list.add(problem.getEnvironment().getBoundary().getBoundingBox());
                        return list;
                    }

                }, Color.BLACK, Color.WHITE));

        // draw regions
        VisManager.registerLayer(RegionsLayer.create(
                new RegionsProvider() {

                    @Override
                    public Collection getRegions() {
                        return problem.getObstacles();
                    }

                }, Color.GRAY, Color.GRAY));

        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointsProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledPoint<tt.euclid2i.Point>> getLabeledPoints() {
                LinkedList<LabeledPoint<tt.euclid2i.Point>> list = new LinkedList<LabeledPoint<tt.euclid2i.Point>>();

                for (int i = 0; i < problem.getStarts().length; i++) {
                    list.add(new LabeledPoint<tt.euclid2i.Point>(problem.getStarts()[i], "s" + i));
                }
                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.BLUE));


        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointsProvider<tt.euclid2i.Point>() {

            @Override
            public Collection<LabeledPoint<tt.euclid2i.Point>> getLabeledPoints() {
                LinkedList<LabeledPoint<tt.euclid2i.Point>> list = new LinkedList<LabeledPoint<tt.euclid2i.Point>>();

                for (int i = 0; i < problem.getTargets().length; i++) {
                    list.add(new LabeledPoint<tt.euclid2i.Point>(problem.getTarget(i), "g" + i));
                }
                return list;
            }

        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new EightAgentsPrioritizedPlanningDemo().create();
    }

}
