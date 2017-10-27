package tt.jointtrajineuclidtime3i.demo;

import java.awt.Color;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.SynchronizedMovesTimeExtension;
import tt.euclidtime3i.vis.TimeParameter;
import tt.euclidtime3i.vis.TimeParameterProjectionTo2d;
import tt.jointtraj.solver.SearchResult;
import tt.jointtrajineuclidtime3i.solver.ODSolver;
import tt.vis.CylindricAgentsLayer;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.ParameterControlLayer;
import tt.vis.TrajectoriesLayer;
import tt.vis.TrajectoriesLayer.TrajectoriesProvider;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class FourAgentsSimpleODDemo implements Creator {

    @Override
    public void init(String[] args) {}

    @Override
    public void create() {

        initVisualization();

        // create time parameter

        final TimeParameter time = new TimeParameter(20);
        VisManager.registerLayer(ParameterControlLayer.create(time));

        // start positions

        tt.euclid2i.Point s1 = new tt.euclid2i.Point(0, 0);
        tt.euclid2i.Point s2 = new tt.euclid2i.Point(1000,1000);
        tt.euclid2i.Point s3 = new tt.euclid2i.Point(1000,0);
        tt.euclid2i.Point s4 = new tt.euclid2i.Point(0, 1000);

        tt.euclid2i.Point g1 = new tt.euclid2i.Point(1000, 1000);
        tt.euclid2i.Point g2 = new tt.euclid2i.Point(0, 0);
        tt.euclid2i.Point g3 = new tt.euclid2i.Point(0, 1000);
        tt.euclid2i.Point g4 = new tt.euclid2i.Point(1000, 0);

        final int BODYRADIUS = 200;
        final int MAXTIME = 8000;

        // create motion graph

        DirectedGraph<Point, Straight> motions1 = createMotionGraph(s1, MAXTIME);
        DirectedGraph<Point, Straight> motions2 = createMotionGraph(s2, MAXTIME);
        DirectedGraph<Point, Straight> motions3 = createMotionGraph(s3, MAXTIME);
        DirectedGraph<Point, Straight> motions4 = createMotionGraph(s4, MAXTIME);

        // solve

        ODSolver solver = new ODSolver(new tt.euclid2i.Point[] {s1, s2, s3, s4},
                    new tt.euclid2i.Point[] {g1, g2, g3, g4},
                    new DirectedGraph[] {motions1, motions2, motions3, motions4},
                    new int[] {BODYRADIUS, BODYRADIUS, BODYRADIUS, BODYRADIUS},
                    MAXTIME,
                    Double.MAX_VALUE,
                    new LinkedList<Trajectory>());

        final SearchResult result = solver.solve(Long.MAX_VALUE);

        if (result.foundSolution()) {
            System.out.println(result.trajectories);

            VisManager.registerLayer(TrajectoriesLayer.create(new TrajectoriesProvider<tt.euclid2i.Point>() {
                @Override
                public tt.discrete.Trajectory<tt.euclid2i.Point>[] getTrajectories() {
                    return result.getTrajectories();
                }
            }, new tt.euclid2i.vis.ProjectionTo2d(), 10, 6, MAXTIME, 's'));

            VisManager.registerLayer(CylindricAgentsLayer.create(new CylindricAgentsLayer.TrajectoriesProvider() {

                @Override
                public Trajectory[] getTrajectories() {
                    return result.getTrajectories();
                }
            }, new TimeParameterProjectionTo2d(time), new int[] {BODYRADIUS, BODYRADIUS, BODYRADIUS, BODYRADIUS}, 1));

        } else {
            System.out.println("Haven't found the solution...");
        }
    }

    private DirectedGraph<Point, Straight> createMotionGraph(
            tt.euclid2i.Point init, int maxTime) {

        // create discretization
        final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph
            = new LazyGrid(init,
                    new LinkedList<tt.euclid2i.Region>(),
                    new tt.euclid2i.region.Rectangle(new tt.euclid2i.Point(0,0), new tt.euclid2i.Point(1000,1000)),
                    LazyGrid.PATTERN_8_WAY_WAIT,
                    200);

        // graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<tt.euclid2i.Point, tt.euclid2i.Line>() {

            @Override
            public Graph<tt.euclid2i.Point, tt.euclid2i.Line> getGraph() {
                return ((LazyGrid) spatialGraph).generateFullGraph();
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));

        // create spatio-temporal graph
        DirectedGraph<Point, Straight> motions = new SynchronizedMovesTimeExtension(spatialGraph, maxTime, 100, new LinkedList<Region>());
        return motions;
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500,500);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            } } );

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new FourAgentsSimpleODDemo().create();
    }

}
