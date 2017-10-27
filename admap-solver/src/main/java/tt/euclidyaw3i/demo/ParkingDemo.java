package tt.euclidyaw3i.demo;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.planner.spatialmaneuver.maneuver.Maneuver;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import org.apache.commons.math3.util.MathUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.ZeroHeuristic;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.discretization.VisibilityGraph;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.euclidyaw3i.discretization.*;
import tt.euclidyaw3i.vis.*;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.GraphPathLayer;
import tt.vis.GraphPathLayer.PathProvider;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.*;

public class ParkingDemo implements Creator {

    final static Polygon carFootprint = (new Rectangle(new Point(-1000, -900), new Point(3000, 900))).toPolygon();

    final static Polygon duckiebotFootprint = (new Rectangle(new Point(-1600, -900), new Point(700, 900))).toPolygon();

    final static Polygon smallDuckiebotFootprint = (new Rectangle(new Point(-160 * 2, -90 * 2), new Point(70 * 2, 90 * 2))).toPolygon();

    List<Polygon> obstacles = new LinkedList<>();

    @Override
    public void init(String[] strings) {}

    public ParkingDemo() {
        initVisualization();

        obstacles.addAll(createColumns(6,6));
        // create other cars
        obstacles.add(carFootprint.getRotated(new Point(0,0), 0.1f).getTranslated(new Point(3200, 1500)));
        obstacles.add(carFootprint.getRotated(new Point(0,0), -0.05f).getTranslated(new Point(2600, 9300)));
    }

    public void minkowskiDemo(Polygon footprint) {
        List<Polygon> minkowskiObstacles = new LinkedList<>();

        float[] angleHolder = new float[1];

        VisManager.registerLayer(RegionsLayer.create(() --> minkowskiObstacles, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -->
                        Collections.singleton(new tt.euclidyaw3i.Point(0,0,angleHolder[0])),
                Color.RED, null));

        for (int i=0; i < 1000; i++) {

            float angle = (float) ((((i % 100)-50) / 100.0) * 2 * Math.PI);
            Polygon footprintR = footprint.getRotated(new tt.euclid2i.Point (0,0),angle);
            angleHolder[0] = angle;

            minkowskiObstacles.clear();
            for (Polygon obstacle : obstacles) {
                minkowskiObstacles.add(obstacle.minkowskiSum(footprintR));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void configurationSpaceDemo() {
        final Polygon footprint = duckiebotFootprint;
        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                Collections.singleton(new tt.euclidyaw3i.Point(0, 0, 0)),
                Color.RED, null));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                        Collections.singleton(new tt.euclidyaw3i.Point(5000, 3000, (float) Math.PI / 4)),
                Color.RED, null));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                        Collections.singleton(new tt.euclidyaw3i.Point(0, 5000, (float) Math.PI)),
                Color.RED, null));
    }

    public void visGraphDemo(Polygon footprint, int agentRadius) {
        tt.euclidyaw3i.Point initConf = new tt.euclidyaw3i.Point(0, 0, (float) Math.PI/2);
        tt.euclidyaw3i.Point goalConf = new tt.euclidyaw3i.Point(4000, 5500, (float) 0);

        obstacles.clear();
        obstacles.addAll(createColumns(0,3));
        // create other cars
        obstacles.add(carFootprint.getRotated(new Point(0,0), 0.1f).getTranslated(new Point(3200, 1500)));

        Collection<Region> inflatedObstaclesForCollisionChecking = Util.inflateRegions(obstacles, agentRadius);
        Collection<Region> inflatedObstaclesForGraph = Util.inflateRegions(obstacles, agentRadius+5);

        DirectedGraph<Point, Line> visGraph = VisibilityGraph.createVisibilityGraph(
                initConf.getPos(),
                goalConf.getPos(),
                inflatedObstaclesForCollisionChecking,
                inflatedObstaclesForGraph);

        VisManager.registerLayer(RegionsLayer.create(() --> inflatedObstaclesForGraph, Color.GRAY));


        VisManager.registerLayer(
                KeyToggleLayer.create("g", true,
                GraphLayer.create(
                        () --> visGraph
                        , new tt.euclid2i.vis.ProjectionTo2d(), Color.LIGHT_GRAY, Color.GRAY, 1,4)
                 ));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(initConf),
                Color.BLACK, Color.RED));
        VisManager.registerLayer(RegionsLayer.create(() -> Collections.singleton(new Circle(goalConf.getPos(), agentRadius)), Color.RED, null));

        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));
        VisManager.registerLayer(RegionsLayer.create(() -> Collections.singleton(new Circle(new Point(0,0), agentRadius)), Color.RED, null));

        final GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(visGraph, new ZeroHeuristic(), initConf.getPos(), goalConf.getPos());

        VisManager.registerLayer(
                KeyToggleLayer.create("p", true,
                GraphPathLayer.create(
                () -> path,
                new ProjectionTo2d(), Color.BLUE, Color.BLUE, 2,4)));

    }

    public void maneuverTree(Polygon footprint) {
        tt.euclidyaw3i.Point initConf = new tt.euclidyaw3i.Point(0, 0, (float) Math.PI/2);
        tt.euclidyaw3i.Point goalConf = new tt.euclidyaw3i.Point(-5600, 9800, (float) 2.8);

        //visualize initial configuration
        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(initConf), Color.RED, null));

        //visualize goal configuration
        VisManager.registerLayer(
                KeyToggleLayer.create("t", true,
                FootPrintLayer.create(footprint, () -> Collections.singleton(goalConf), Color.GREEN, null)));

        Collection<PathSegment> maneuvers = ManeuverTree.getConstantCurvatureArcs(new double[]{-0.14/1000.0, -0.07/1000.0, 0, +0.07/1000.0, 0.14/1000.0}, 4000.0f, 1000.0f);
        Collection<PathSegment> maneuversFromInit = ManeuverTree.applyManeuvers(initConf, maneuvers);

        Rectangle boundingBox = new Rectangle(-10000,-2000, 7000, 18000);
        VisManager.registerLayer(
                KeyToggleLayer.create("b", true,
                RegionsLayer.create(() -> Collections.singleton(boundingBox), new Color(100,220,220))));


        DirectedGraph<tt.euclidyaw3i.Point, PathSegment> graphNoColCheck = ManeuverTree.buildTree(initConf, maneuvers, boundingBox, footprint, Collections.EMPTY_LIST);
        VisManager.registerLayer(
                KeyToggleLayer.create("s", true, PathSegmentGraphLayer.create(graphNoColCheck, false, new Color(220,220,220))));

        DirectedGraph<tt.euclidyaw3i.Point, PathSegment> graphColCheck = ManeuverTree.buildTree(initConf, maneuvers, boundingBox, footprint, obstacles);
        VisManager.registerLayer(
                KeyToggleLayer.create("g", true,
                    PathSegmentGraphLayer.create(graphColCheck, false, Color.BLUE)));

        // visualize obstacles
        VisManager.registerLayer(
                KeyToggleLayer.create("o", true,
                RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK)));

        // find path
        GraphPath<tt.euclidyaw3i.Point, PathSegment> path = AStarShortestPathSimple.findPathBetween(
                graphColCheck,
                p -> 0.0,
                initConf,
                new Goal<tt.euclidyaw3i.Point>() {
                    @Override
                    public boolean isGoal(tt.euclidyaw3i.Point current) {
                        return current.getPos().distance(goalConf.getPos()) < 300.0;
                    }
                }
        );

        // visualize path
        VisManager.registerLayer(
                KeyToggleLayer.create("p", true,
                       PathSegmentLayer.create(()->path.getEdgeList(), Color.RED, 3)));

    }

    public void lattice(Polygon footprint) {
        tt.euclidyaw3i.Point initConf = new tt.euclidyaw3i.Point(0, 0, (float) Math.PI/2);
        tt.euclidyaw3i.Point goalConf = new tt.euclidyaw3i.Point(-2600, 14200, (float) 1.56);

        //visualize initial configuration
        VisManager.registerLayer(
                KeyToggleLayer.create("k", true,
                    FootPrintLayer.create(footprint, () -> Collections.singleton(initConf), Color.RED, null)));

        //visualize goal configuration
        VisManager.registerLayer(
                KeyToggleLayer.create("t", true,
                        FootPrintLayer.create(footprint, () -> Collections.singleton(goalConf), Color.GREEN, null)));

        Rectangle boundingBox = new Rectangle(-10000,-2000, 7000, 18000);
        Collection<tt.euclidyaw3i.Point> specialPoints = Collections.singleton(initConf);

        double connRadius = 9000;
        int n = 8;
        int angles = 16;
        double rho = 6000;

        DirectedGraph<tt.euclidyaw3i.Point, PathSegment> graph = SampledRoadmap.buildLattice(boundingBox, n, n, angles,
                specialPoints,
                new DubinsDistance(rho),
                connRadius,
                new DubinsSteering(rho,500),
                footprint, obstacles);

        VisManager.registerLayer(
                KeyToggleLayer.create("b", true,
                        RegionsLayer.create(() -> Collections.singleton(boundingBox), new Color(100,220,220))));

        VisManager.registerLayer(
                KeyToggleLayer.create("g", true,
                        PathSegmentGraphLayer.create(graph, false, Color.BLUE)));

        VisManager.registerLayer(
                KeyToggleLayer.create("x", true,
                        PointsLayer.create(() -> graph.vertexSet(), Color.BLUE)));

        // edges out of single vertex

        tt.euclidyaw3i.Point v = new tt.euclidyaw3i.Point(-437,4250, (float) -Math.PI);

        // visualize obstacles
        VisManager.registerLayer(
                KeyToggleLayer.create("o", true,
                        RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK)));

        // find path
        GraphPath<tt.euclidyaw3i.Point, PathSegment> path = AStarShortestPathSimple.findPathBetween(
                graph,
                p -> 0.0,
                initConf,
                new Goal<tt.euclidyaw3i.Point>() {
                    @Override
                    public boolean isGoal(tt.euclidyaw3i.Point current) {
                        return current.getPos().distance(goalConf.getPos()) < 500.0
                                && Math.abs(tt.euclidyaw3i.Util.angleDiff(current.getYawInRads(), goalConf.getYawInRads())) < 0.1;
                    }
                }
        );


        // visualize path
        if (path != null)
            VisManager.registerLayer(
                    KeyToggleLayer.create("p", true,
                            PathSegmentLayer.create(()->path.getEdgeList(), Color.RED, 3)));

    }

    @Override
    public void create() {
        final Polygon footprint = duckiebotFootprint;

        tt.euclidyaw3i.Point initConf = new tt.euclidyaw3i.Point(0, 0, (float) Math.PI/2);
        tt.euclidyaw3i.Point goalConf = new tt.euclidyaw3i.Point(4000, 5500, (float) 0);

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(initConf),
                Color.BLACK, Color.RED));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(goalConf),
                Color.RED, null));

        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));
    }


    private List<Polygon> createColumns(int nLeft, int nRight) {
        List<Polygon> obstacles = new LinkedList<>();

        int ystart = -500;
        int yspacing = 4000;
        int size = 600;

        for (int i = 0; i < nLeft; i++) {
            obstacles.add((new Rectangle(-5000, ystart + i*yspacing - size/2, -5000+size, ystart + i*yspacing + size/2)).toPolygon());
        }

        for (int i = 0; i < nRight; i++) {
            obstacles.add((new Rectangle(1500, ystart + i*yspacing - size/2, 1500+size, ystart + i*yspacing + size/2)).toPolygon());
        }

        return obstacles;
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 30000, 30000);
        VisManager.setSceneParam(new SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0, 0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.05;
            }
        });

        VisManager.init();
        VisManager.setInvertYAxis(true);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

        VisManager.registerLayer(AxisLayer.create(1000));
    }


    public static void main(String[] args) {


        // Visibility graph demo
        //new ParkingDemo().visGraphDemo(ParkingDemo.smallDuckiebotFootprint, 400);

        // Minkowski Sum demo
        //new ParkingDemo().minkowskiDemo(carFootprint);

        // Lattice demo
        //new ParkingDemo().lattice(ParkingDemo.carFootprint);

        // Maneuver tree demo
        new ParkingDemo().maneuverTree(ParkingDemo.carFootprint);

    }

}
