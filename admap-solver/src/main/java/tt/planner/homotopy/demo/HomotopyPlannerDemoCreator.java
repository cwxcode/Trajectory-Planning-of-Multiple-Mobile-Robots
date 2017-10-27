package tt.planner.homotopy.demo;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.ExpansionListener;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.jscience.mathematics.number.Complex;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.probleminstance.ShortestPathProblem;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclid2i.vis.RegionsLayer;
import tt.planner.homotopy.HEdge;
import tt.planner.homotopy.HNode;
import tt.planner.homotopy.HomotopyGraphWrapper;
import tt.planner.homotopy.ProjectionToComplexPlane;
import tt.planner.homotopy.hclass.HClassDiscretized;
import tt.planner.homotopy.hclass.HClassProvider;
import tt.planner.homotopy.hvalue.HValueForbidPolicy;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.planner.homotopy.hvalue.HValueNumericIntegrator;
import tt.vis.GraphLayer;
import tt.vis.GraphPathLayer;
import tt.vis.LabeledPointLayer;

import javax.vecmath.Point2d;
import java.awt.*;
import java.util.*;
import java.util.List;

public class HomotopyPlannerDemoCreator {

    private static final boolean VISUALIZE = true;

    private static final int ITERATION_DELAY = 20;
    private ShortestPathProblem problem;

    List<Complex> qRoots;
    List<Complex> pRoots;

    HomotopyPlannerDemoCreator(ShortestPathProblem problem) {
        this.problem = problem;
    }

    void run() {
        // >>>>>>> 1. you need to initialize projection from graph nodes to complex plane
        ProjectionToComplexPlane<Point> projection = new ProjectionToComplexPlane<Point>() {
            @Override
            public Complex complexValue(Point state) {
                return Complex.valueOf(state.getX(), state.getY());
            }
        };

        Collection<Rectangle> polygons = getObstaclesBoundingBoxes();

        // >>>>>>> 2. select a point inside every significant obstacle
        pRoots = new ArrayList<Complex>();
        for (Region obstacle : polygons) {
            Rectangle boundingBox = obstacle.getBoundingBox();
            pRoots.add(projection.complexValue(boundingBox.getCorner1()).plus(projection.complexValue(boundingBox.getCorner2())).divide(2));
        }

        // >>>>>>> 3. select N-1 free space samples, where N is a number of selected obstacles
        qRoots = new ArrayList<Complex>();
        for (int i = 1; i < pRoots.size(); i++) {
            qRoots.add(projection.complexValue(Util.sampleFreeSpace(problem.getBoundary().getBoundingBox(), problem.getObstacles(), new Random(qRoots.size()))));
        }

        // >>>>>>> 4. initialize integrator
        HValueIntegrator numericIntegrator = new HValueNumericIntegrator(qRoots, pRoots, 0.1, 100);

        // >>>>>>> 5. select hClass provider - this class gets hValue of a assigns it into some hClass cluster!
        HClassProvider<Point> provider = new HClassDiscretized.Provider<Point>();

        // >>>>>>> 6. Wrap any graph you want into the

        Goal<HNode<Point>> goal = new Goal<HNode<Point>>() {
            @Override
            public boolean isGoal(HNode<Point> current) {
                return problem.getTargetRegion().isInside(current.getNode());
            }
        };

        DirectedGraph<Point, Line> visibilityGraph = Util.getVisibilityGraph(problem.getStart(), problem.getTargetPoint(), polygons);
        final HomotopyGraphWrapper<Point, Line> homotopyWrapper = new HomotopyGraphWrapper<Point, Line>(visibilityGraph, goal, projection, numericIntegrator, provider, 0.05);

        // >>>>>>> 7. choose policy of allowing / forbidding hValues
        HValueForbidPolicy policy = new HValueForbidPolicy();
        homotopyWrapper.setPolicy(policy);

        // >>>>>>> 8. Use any kind of graph path planning algorithm you want. It it advised to use informed one.
        AStarShortestPathSimple<HNode<Point>, HEdge<Point, Line>> aStar =
                new AStarShortestPathSimple<HNode<Point>, HEdge<Point, Line>>(homotopyWrapper,
                        new HeuristicToGoal<HNode<Point>>() {
                            @Override
                            public double getCostToGoalEstimate(HNode<Point> current) {
                                return problem.getTargetPoint().distance(current.getNode());
                            }
                        },
                        homotopyWrapper.wrapNode(problem.getStart(), Complex.ZERO), goal);

        aStar.addExpansionListener(new ExpansionListener<HNode<Point>>() {
            @Override
            public void exapanded(HNode<Point> expandedState) {
                try {
                    if (VISUALIZE)
                        Thread.sleep(ITERATION_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        if (VISUALIZE) {
            initializeVisualization();
            showVisualization(visibilityGraph, aStar);
        }
        GraphPath<HNode<Point>, HEdge<Point, Line>> path;

        for (int i = 0; i < 100; i++) {
            aStar.findPath(Integer.MAX_VALUE);
            path = aStar.getTraversedPath();
            if (path != null) {
                policy.forbid(path.getEndVertex().getHValue());
                System.out.println(String.format("Iteration: %d Solution length: %f", i, path.getWeight()));
            }
        }

    }

    private Collection<Rectangle> getObstaclesBoundingBoxes() {
        Collection<Rectangle> polygons = new LinkedList<Rectangle>();
        for (Region obstacle : problem.getObstacles()) {
            Rectangle boundingBox = obstacle.getBoundingBox();
            polygons.add(boundingBox);
        }
        return polygons;
    }

    private void initializeVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768);
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

        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {
                    @Override
                    public Collection<Region> getRegions() {
                        LinkedList<Region> list = new LinkedList<Region>();
                        list.add(problem.getBoundary());
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
                list.add(new LabeledPointLayer.LabeledPoint<Point>(problem.getStart(), "start"));
                return list;
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED));

        VisManager.registerLayer(RegionsLayer.create(
                new RegionsLayer.RegionsProvider() {
                    @Override
                    public Collection<Region> getRegions() {
                        LinkedList<Region> list = new LinkedList<Region>();
                        list.add(problem.getTargetRegion());
                        return list;
                    }
                }, Color.PINK, Color.PINK));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

    private void showVisualization(final DirectedGraph<Point, Line> visibilityGraph, final AStarShortestPathSimple<HNode<Point>, HEdge<Point, Line>> aStar) {
        VisManager.registerLayer(GraphLayer.create(new GraphLayer.GraphProvider<Point, Line>() {
            @Override
            public Graph<Point, Line> getGraph() {
                return visibilityGraph;
            }
        }, new ProjectionTo2d(), Color.LIGHT_GRAY, Color.BLUE, 1, 4));

        VisManager.registerLayer(GraphPathLayer.create(new GraphPathLayer.PathProvider<HNode<Point>, HEdge<Point, Line>>() {
                                                           @Override
                                                           public GraphPath<HNode<Point>, HEdge<Point, Line>> getPath() {
                                                               return aStar.getTraversedPath();
                                                           }
                                                       }, new tt.vis.ProjectionTo2d<HNode<Point>>() {
                                                           @Override
                                                           public Point2d project(HNode<Point> point) {
                                                               return new Point2d(point.getNode().getX(), point.getNode().getY());
                                                           }
                                                       }, Color.RED, Color.RED.darker(), 2, 2
        ));
    }

    public static void main(String[] args) {
        new HomotopyPlannerDemoCreator(new ShortestPathProblem(750, 500, 10, 150, 40, 43)).run();
    }
}
