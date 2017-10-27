package tt.euclid2i.demo;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.discretization.ObstacleWrapper;
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


public class ObstacleWrapperDemo implements Creator {

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {

        Rectangle bounds = new Rectangle(new Point(-100, -100), new Point(200,200));

        final Point start = new Point(-10, -30);
        final Point goal = new Point(0, 40);

        // Create discretization
        final DirectedGraph<Point, Line> graph = new LazyGrid(start,
                new LinkedList<Region>(), new Rectangle(new Point(-50, -50),
                        new Point(50, 50)), LazyGrid.PATTERN_16_WAY, 5);


        final Collection<Region> obstacles = new LinkedList<Region>();
        obstacles.add(new Circle(new Point(0,0),20));

        // Cut-out obstacles from the graph
        final DirectedGraph<Point, Line> graphWithoutObstacles = new ObstacleWrapper<Point, Line>(graph, obstacles);

        initVisualization();

        // graph
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


        // visualize graph without obstacles
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<Point, Line>() {

            @Override
            public Graph<Point, Line> getGraph() {
               return GraphBuilder.build(graphWithoutObstacles, new DefaultDirectedGraph<Point, Line>(Line.class), start);
            }
        }, new ProjectionTo2d(), Color.BLUE, Color.BLUE, 1, 4));

        final GraphPath<Point, Line> pathBetween = AStarShortestPath.findPathBetween(graphWithoutObstacles, new HeuristicToGoal<Point>() {

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
        new ObstacleWrapperDemo().create();
    }

}
