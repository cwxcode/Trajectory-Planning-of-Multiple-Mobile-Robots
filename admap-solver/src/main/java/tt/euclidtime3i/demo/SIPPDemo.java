package tt.euclidtime3i.demo;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.discrete.vis.TrajectoryLayer;
import tt.discrete.vis.TrajectoryLayer.TrajectoryProvider;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.discretization.L2Heuristic;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.trajectory.LinearTrajectory;
import tt.euclid2i.trajectory.StraightSegmentTrajectory;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.sipp.SippEdge;
import tt.euclidtime3i.sipp.SippGoal;
import tt.euclidtime3i.sipp.SippHeuristic;
import tt.euclidtime3i.sipp.SippNode;
import tt.euclidtime3i.sipp.SippUtils;
import tt.euclidtime3i.sipp.SippWrapper;
import tt.euclidtime3i.sipp.intervals.Interval;
import tt.euclidtime3i.sipprrts.DynamicObstaclesImpl;
import tt.euclidtime3i.sipprrts.vis.SearchTreeLayer;
import tt.euclidtime3i.sipprrts.vis.SearchTreeLayer.SearchTreeProvider;
import tt.euclidtime3i.vis.RegionsLayer;
import tt.euclidtime3i.vis.RegionsLayer.RegionsProvider;
import tt.euclidtime3i.vis.TimeParameter;
import tt.euclidtime3i.vis.TimeParameterProjectionTo2d;
import tt.vis.FastAgentsLayer;
import tt.vis.FastAgentsLayer.ColorProvider;
import tt.vis.FastAgentsLayer.TrajectoriesProvider;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.LabeledPointLayer;
import tt.vis.LabeledPointLayer.LabeledPoint;
import tt.vis.LabeledPointLayer.LabeledPointsProvider;
import tt.vis.ParameterControlLayer;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.terminal.PointLayer;


public class SIPPDemo implements Creator {
    // create time parameter
    final static TimeParameter time = new TimeParameter();

    @Override
    public void init(String[] args) {}

    @Override
    public void create() {

        initVisualization();

        VisManager.registerLayer(ParameterControlLayer.create(time));

        final tt.euclid2i.Point START = new tt.euclid2i.Point(100,-100);
        final tt.euclid2i.Point END = new tt.euclid2i.Point(-100,100);

        // create discretization
        final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph
            = new LazyGrid(START,
                    new LinkedList<tt.euclid2i.Region>(),
                    new tt.euclid2i.region.Rectangle(new tt.euclid2i.Point(-200,-200),
                    new tt.euclid2i.Point(200,200)),
                    LazyGrid.PATTERN_8_WAY,
                    10).generateFullGraph();

        // graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<tt.euclid2i.Point, tt.euclid2i.Line>() {

        	@Override
        	public Graph<tt.euclid2i.Point, tt.euclid2i.Line> getGraph() {
        		return spatialGraph;
        	}
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));

        final int MAXTIME = 3000;
        final int BODY_RADIUS = 1;
        final int OBSTACLE_RADIUS = 45;

        final SegmentedTrajectory[] trajArr = new SegmentedTrajectory[] {
        	new LinearTrajectory(0, new tt.euclid2i.Point(-100,-100), new tt.euclid2i.Point(50, 50), 1, MAXTIME, MAXTIME),
        	new LinearTrajectory(0, new tt.euclid2i.Point(-100,0), new tt.euclid2i.Point(70,0), 1, MAXTIME, MAXTIME)
        };

        int[] radiuses = new int[trajArr.length];
        Arrays.fill(radiuses, OBSTACLE_RADIUS);

        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {
        	@Override
        	public Collection<tt.euclidtime3i.Region> getRegions() {
        		Collection<tt.euclidtime3i.Region> regions = new LinkedList<tt.euclidtime3i.Region>();
        		for (SegmentedTrajectory traj : trajArr) {
        			regions.add(new MovingCircle(traj, OBSTACLE_RADIUS));
        		}
        		return regions;
        	}
        }, new TimeParameterProjectionTo2d(time), Color.RED, Color.GRAY));


        // **** Solve *****
        final SegmentedTrajectory trajectorySipp = solveSIPP(START, END, spatialGraph, MAXTIME, BODY_RADIUS, trajArr, radiuses);
        final SegmentedTrajectory trajectoryFullSpaceTime = solveConventional(START, END, spatialGraph, MAXTIME, BODY_RADIUS, trajArr, radiuses);

        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider<tt.euclid2i.Point>() {

            @Override
            public tt.discrete.Trajectory<tt.euclid2i.Point> getTrajectory() {
                return trajectorySipp;
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.BLUE, 5, 1000, 's'));

        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider<tt.euclid2i.Point>() {

            @Override
            public tt.discrete.Trajectory<tt.euclid2i.Point> getTrajectory() {
                return trajectoryFullSpaceTime;
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GREEN, 5, 1000, 's'));

        VisManager.registerLayer(FastAgentsLayer.create(new TrajectoriesProvider() {

			@Override
			public tt.euclid2i.Trajectory[] getTrajectories() {
				tt.euclid2i.Trajectory[] trajArr = new tt.euclid2i.Trajectory[] { (tt.euclid2i.Trajectory) trajectorySipp, (tt.euclid2i.Trajectory) trajectoryFullSpaceTime  };
				return trajArr ;
			}

			@Override
			public int[] getBodyRadiuses() {
				return new int [] {BODY_RADIUS, BODY_RADIUS};
			}
		}, new ColorProvider() {

			@Override
			public Color getColor(int i) {
				return i == 0 ? Color.BLUE : Color.GREEN;
			}
		}, time));

    }

	protected SegmentedTrajectory solveSIPP(
			final tt.euclid2i.Point start,
			final tt.euclid2i.Point goal,
			final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph,
			final int maxtime, final int bodyRadius,
			final SegmentedTrajectory[] trajArr, int[] radiuses) {

		System.out.println("Creating SIPP Wrapper...");
        DynamicObstaclesImpl dynObstacles = new DynamicObstaclesImpl(trajArr, radiuses, maxtime);
        final SippWrapper wrapper = new SippWrapper(spatialGraph, dynObstacles, bodyRadius, 1, 2, maxtime);
        SippNode sippStartNode = new SippNode(start, Interval.toInfinity(0), 0);
        SippHeuristic heuristic = new SippHeuristic(new L2Heuristic(goal), 1);
        SippGoal sippGoalNode = new SippGoal(goal, maxtime);


        // Show SIPP nodes

        VisManager.registerLayer(LabeledPointLayer.create(new LabeledPointsProvider<tt.euclid2i.Point>() {
        	@Override
        	public Collection<LabeledPoint<tt.euclid2i.Point>> getLabeledPoints() {
        		LinkedList<LabeledPoint<tt.euclid2i.Point>> lpoints = new LinkedList<LabeledPoint<tt.euclid2i.Point>>();
        		for (tt.euclid2i.Point vertex : spatialGraph.vertexSet()) {
        			lpoints.add(new LabeledPoint<tt.euclid2i.Point>(vertex, ""+wrapper.getNoOfSafeIntervals(vertex)));
        		}
        		return lpoints;
        	}
        }, new ProjectionTo2d(), Color.BLACK));

        System.out.println("..Done \nStarting A* search...");

        final AStarShortestPathSimple<SippNode, SippEdge> astar = new AStarShortestPathSimple<SippNode, SippEdge>(wrapper, heuristic, sippStartNode, sippGoalNode);

        // Show search tree

        VisManager.registerLayer(SearchTreeLayer.create(new SearchTreeProvider<SippNode>() {

			@Override
			public Collection<SippNode> getOpen() {
				return astar.getOpenedNodes();
			}

			@Override
			public Collection<SippNode> getClosed() {
				return astar.getClosedNodes();
			}

			@Override
			public SippNode getParent(SippNode vertex) {
				return astar.getParent(vertex);
			}

			@Override
			public double getValue(SippNode vertex) {
				return astar.getFValue(vertex) / 300;
			}

			@Override
			public SippNode getCurrent() {
				return astar.getCurrentVertex();
			}
		},

		new tt.vis.ProjectionTo2d<SippNode>() {

			@Override
			public Point2d project(SippNode point) {
				if (point.getTime() == time.getTime()) {
					return new Point2d(point.getPoint().x, point.getPoint().y);
				} else {
					return null;
				}
			}

		}

		, 4));

        VisManager.registerLayer(SearchTreeLayer.create(new SearchTreeProvider<SippNode>() {

			@Override
			public Collection<SippNode> getOpen() {
				return astar.getOpenedNodes();
			}

			@Override
			public Collection<SippNode> getClosed() {
				return astar.getClosedNodes();
			}

			@Override
			public SippNode getParent(SippNode vertex) {
				return astar.getParent(vertex);
			}

			@Override
			public double getValue(SippNode vertex) {
				return astar.getFValue(vertex) / 300;
			}

			@Override
			public SippNode getCurrent() {
				return astar.getCurrentVertex();
			}
		}, new tt.vis.ProjectionTo2d<SippNode>() {

			@Override
			public Point2d project(SippNode point) {
				return new Point2d(point.getPoint().x, -200  - point.getTime());
			}

		}, 4));

        long startedAt = System.currentTimeMillis();
        GraphPath<SippNode, SippEdge> path = astar.findPath(Integer.MAX_VALUE);
        final SegmentedTrajectory trajectory = SippUtils.parseTrajectory(path, maxtime);
        System.out.println("...Done. Iterations: " + astar.getIterationCount()  + ". Took " + (System.currentTimeMillis() - startedAt) + " ms");
        System.out.println("Finished planning: " + trajectory);

		return trajectory;
	}

	protected SegmentedTrajectory solveConventional(
			final tt.euclid2i.Point start,
			final tt.euclid2i.Point goal,
			final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph,
			final int maxtime, final int bodyRadius,
			final SegmentedTrajectory[] trajArr, int[] radiuses) {

		System.out.println("\n\nSolving in full space-time...");

		Collection<tt.euclidtime3i.Region> dynamicObstacles = new LinkedList<tt.euclidtime3i.Region>();


		for (int i=0; i < trajArr.length; i++) {
			dynamicObstacles.add(new MovingCircle(trajArr[i], radiuses[i] + bodyRadius));
		}

		ConstantSpeedTimeExtension spaceTimeGraph = new ConstantSpeedTimeExtension(spatialGraph, maxtime, new float[] {1}, dynamicObstacles , 1);

        System.out.println("\nStarting A* search...");

        final AStarShortestPathSimple<tt.euclidtime3i.Point, Straight> astar = new AStarShortestPathSimple<tt.euclidtime3i.Point, Straight>(spaceTimeGraph, new HeuristicToGoal<tt.euclidtime3i.Point>() {

			@Override
			public double getCostToGoalEstimate(Point current) {
				return current.getPosition().distance(goal);
			}
		}, new tt.euclidtime3i.Point(start, 0), new Goal<tt.euclidtime3i.Point>() {

			@Override
			public boolean isGoal(Point current) {
				return current.getPosition().equals(goal);
			}

		});

        VisManager.registerLayer(SearchTreeLayer.create(new SearchTreeProvider<tt.euclidtime3i.Point>() {

			@Override
			public Collection<tt.euclidtime3i.Point> getOpen() {
				return astar.getOpenedNodes();
			}

			@Override
			public Collection<tt.euclidtime3i.Point> getClosed() {
				return astar.getClosedNodes();
			}

			@Override
			public tt.euclidtime3i.Point getParent(tt.euclidtime3i.Point vertex) {
				return astar.getParent(vertex);
			}

			@Override
			public double getValue(tt.euclidtime3i.Point vertex) {
				return astar.getFValue(vertex) / 300;
			}

			@Override
			public tt.euclidtime3i.Point getCurrent() {
				return astar.getCurrentVertex();
			}
		},

		new tt.vis.ProjectionTo2d<tt.euclidtime3i.Point>() {

			@Override
			public Point2d project(tt.euclidtime3i.Point point) {
				if (point.getTime() == time.getTime()) {
					return new Point2d(point.x + 400, point.y);
				} else {
					return null;
				}
			}

		}
		, 4));

        VisManager.registerLayer(SearchTreeLayer.create(new SearchTreeProvider<tt.euclidtime3i.Point>() {

			@Override
			public Collection<tt.euclidtime3i.Point> getOpen() {
				return astar.getOpenedNodes();
			}

			@Override
			public Collection<tt.euclidtime3i.Point> getClosed() {
				return astar.getClosedNodes();
			}

			@Override
			public tt.euclidtime3i.Point getParent(tt.euclidtime3i.Point vertex) {
				return astar.getParent(vertex);
			}

			@Override
			public double getValue(tt.euclidtime3i.Point vertex) {
				return astar.getFValue(vertex) / 300;
			}

			@Override
			public tt.euclidtime3i.Point getCurrent() {
				return astar.getCurrentVertex();
			}
		}, new tt.vis.ProjectionTo2d<tt.euclidtime3i.Point>() {

			@Override
			public Point2d project(tt.euclidtime3i.Point point) {
				return new Point2d(point.x + 400, -200 -point.getTime());
			}

		}, 4));


        long startedAt = System.currentTimeMillis();

        GraphPath<Point, Straight> path = astar.findPath(Integer.MAX_VALUE);
        final SegmentedTrajectory trajectory = new StraightSegmentTrajectory(path, maxtime);

        System.out.println("...Done. Iterations: " + astar.getIterationCount()  + ". Took " + (System.currentTimeMillis() - startedAt) + " ms");
        System.out.println("Finished planning: " + trajectory);
		return trajectory;
	}

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0,0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 5;
            } } );

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new SIPPDemo().create();
    }

}
