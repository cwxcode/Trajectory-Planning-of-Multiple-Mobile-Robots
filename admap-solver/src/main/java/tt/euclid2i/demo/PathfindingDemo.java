package tt.euclid2i.demo;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.GraphPathLayer;
import tt.vis.GraphPathLayer.PathProvider;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class PathfindingDemo implements Creator {

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {

        // plan the shortest path between these two points
        final Point start = new Point(-5, -35);
        final Point goal = new Point(0, 30);
        
        // obstacle to avoid
        Region obstacle = new Circle(new Point(0, 0), 22);
        final Collection<Region> obstacles = Arrays.asList(new Region[] {obstacle});
        
        // create grid discretization
        // Note that this is "lazy" implementation, i.e. the graph is not stored in memory, 
        // but instead edges are computed on request
        final DirectedGraph<Point, Line> graph = new LazyGrid(start,
                obstacles, new Rectangle(new Point(-50, -50),
                        new Point(50, 50)), LazyGrid.PATTERN_8_WAY, 5);

		// or  a roadmap...
		//        Rectangle bounds = new Rectangle(new Point(-100, -100), new Point(200,200));
		//        final DirectedGraph<Point, Line> graph = new ProbabilisticRoadmap(1000, 14, new Point[] {start, goal}, bounds, obstacles , new Random(1));
        
        
        initVisualization();

        // visualize graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<Point, Line>() {

            @Override
            public Graph<Point, Line> getGraph() {
                if (graph instanceof LazyGrid) {
                    return ((LazyGrid) graph).generateFullGraph();
                } else {
                    return graph;
                }
            }
        }, new ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));
        
        // visualize obstacles
        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {

			@Override
			public Collection<? extends Region> getRegions() {
				return obstacles;
			}
		}, Color.BLACK));
        
        // run A* on the graph
        final GraphPath<Point, Line> pathBetween = AStarShortestPath.findPathBetween(graph, new HeuristicToGoal<Point>() {

            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.distance(goal);
            }
        }, start, goal);

        // visualize the shortest path
        VisManager.registerLayer(GraphPathLayer.create(new PathProvider<Point, Line>() {

            @Override
            public GraphPath<Point, Line> getPath() {
                return pathBetween;
            }

        }, new ProjectionTo2d(), Color.RED, Color.RED, 2, 4));


    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0, 0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 5;
            }
        });

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new PathfindingDemo().create();
    }

}
