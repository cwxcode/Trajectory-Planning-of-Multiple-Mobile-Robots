package tt.jointeuclid2ni.solver.impl;

import static tt.jointtraj.util.Util.getSumCost;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedGraphDelegator;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.AdditionalPointsExtension;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.util.Util;
import tt.euclidtime3i.L1Heuristic;
import tt.euclidtime3i.L2Heuristic;
import tt.euclidtime3i.ShortestPathHeuristic;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.ControlEffortWrapper;
import tt.euclidtime3i.discretization.FreeOnTargetWaitExtension;
import tt.euclidtime3i.discretization.Straight;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.jointeuclid2ni.solver.Algorithm;
import tt.jointeuclid2ni.solver.GraphType;
import tt.jointeuclid2ni.solver.HeuristicType;
import tt.jointeuclid2ni.solver.Parameters;
import tt.jointtraj.solver.SearchResult;
import tt.util.AgentColors;
import tt.util.Counters;
import tt.util.NotImplementedException;
import tt.vis.FastAgentsLayer;
import tt.vis.FastTrajectoriesLayer;
import tt.vis.TimeParameterHolder;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;
import cz.agents.alite.vis.layer.toggle.ToggleLayer;


public abstract class AbstractAlgorithm implements Algorithm {

    protected Parameters params;
    protected EarliestArrivalProblem problem;

    protected abstract SearchResult solveProblem();

    @Override
    public final SearchResult solve(EarliestArrivalProblem problem, Parameters params) {
        this.problem = problem;
        this.params = params;

        if (params.showVis) {
            VisUtil.initVisualization(problem, "Trajectory Tools Vis " + params.method, params.bgImageFile, params.timeStep);
            VisUtil.visualizeEarliestArrivalProblem(problem);
        }

        SearchResult result = solveProblem();

        if (params.showVis) {
            visualizeSolution(result);
        }

        return result;
    }

    protected DirectedGraph<tt.euclidtime3i.Point, Straight> createMotionGraph(final DirectedGraph<tt.euclid2i.Point, Line> spatialGraph,
                                                                               final tt.euclid2i.Point init, final tt.euclid2i.Point goal,
                                                                               float maxSpeed, int maxTime) {

    	// create spatio-temporal graph
        DirectedGraph<tt.euclidtime3i.Point, Straight> motions 
        	= new ConstantSpeedTimeExtension(spatialGraph, maxTime, new float[]{maxSpeed}, new LinkedList<tt.euclidtime3i.Region>(), params.waitMoveDuration, params.timeStep); 
        motions = new FreeOnTargetWaitExtension(motions, goal);
        motions = new ControlEffortWrapper(motions, 0.000001);
        return motions;
    }
    
    protected DirectedGraph<tt.euclid2i.Point, Line> preparePlanningGraphForAgent(int i, int edgeLengthBound, boolean showGraph) {

        final Collection<Region> inflatedObstacles = Util.inflateRegions(problem.getObstacles(), problem.getBodyRadius(i));
        DirectedGraph<tt.euclid2i.Point, Line> spatialGraph = null;
        DirectedGraph<Point, Line> graphToVisualize = null;

        if (problem.getPlanningGraph() != null) {
            DirectedGraph<Point, Line> commonGraph = problem.getPlanningGraph();

            // note that we do not clone the graph... this is intentional to save memory
            // all agents will share the same graph as a result

            Point start = problem.getStart(i);
            Point end = problem.getTarget(i);

            if (!commonGraph.containsVertex(start)) {
            	Util.addVertexAndConnectToNeighbors(commonGraph, start, 4, inflatedObstacles);
            }
            
            if (!commonGraph.containsVertex(end)) {
            	Util.addVertexAndConnectToNeighbors(commonGraph, end, 4, inflatedObstacles);
            }
            
            commonGraph = boundEdgeLength(commonGraph, edgeLengthBound);
            
            spatialGraph = commonGraph;
            graphToVisualize = commonGraph;

        } else if (params.graphType == GraphType.GRID) {

            DirectedGraph<tt.euclid2i.Point, Line> grid = new LazyGrid(
                    problem.getStart(i),
                    inflatedObstacles,
                    problem.getEnvironment().getBoundary().getBoundingBox(),
                    params.gridPattern,
                    params.gridStep).generateFullGraph();
            
            
            grid = boundEdgeLength(grid, edgeLengthBound);
            graphToVisualize = grid;

            spatialGraph
                    = new AdditionalPointsExtension(grid, Collections.singleton(problem.getTarget(i)),
                    params.gridStep, true);

        } else if (params.graphType == GraphType.ROADMAP) {

            Collection<Region> obstacles = problem.getObstacles();
            Region boundary = problem.getEnvironment().getBoundary();
            Point start = problem.getStart(i);
            Point end = problem.getTarget(i);
            int dispersion = params.roadmapDispersion;
            int connectionRadius = params.roadmapConnectionRadius;

            final Collection<Region> moreInflatedObstacles = Util.inflateRegions(obstacles, problem.getBodyRadius(i) + 1);

            spatialGraph = Util.buildGridBasedRoadmap(inflatedObstacles, moreInflatedObstacles, Collections.singleton(boundary),
                    dispersion, connectionRadius, Arrays.asList(new Point[]{start, end}));
            
            spatialGraph = boundEdgeLength(spatialGraph, edgeLengthBound);
            graphToVisualize = spatialGraph;
        }

        if (showGraph) {
        	VisUtil.visualizeGraph(graphToVisualize, inflatedObstacles);
        }

        return spatialGraph;
    }
    
    protected HeuristicToGoal<tt.euclidtime3i.Point> getHeuristic(
			HeuristicType heuristic, DirectedGraph<Point, Line> spatialGraph,
			Point target, final float maxSpeed, final int timeStep) {
    	
    	 switch (heuristic) {
         case PERFECT:
         		 return new ShortestPathHeuristic(new DirectedGraphDelegator<Point, Line>(spatialGraph) {

						@Override
						public double getEdgeWeight(Line e) {
							return (Math.ceil(super.getEdgeWeight(e) / maxSpeed) / timeStep) * timeStep ;
						}
         			
					}, target);

         case L1:
             return new L1Heuristic(target);

         case L2:
             return new L2Heuristic(target);

         default:
             throw new NotImplementedException();
         }
	}


    protected void visualizeSolution(final SearchResult result) {

        if (result.foundSolution()) {

            VisManager.registerLayer(
            		KeyToggleLayer.create("t", true, 
            		FastTrajectoriesLayer.create(
            			new FastTrajectoriesLayer.TrajectoriesProvider() {
                              @Override
                              public Trajectory[] getTrajectories() {
                                  return result.getTrajectories();
                              }

                          }, new FastTrajectoriesLayer.ColorProvider() {

                              @Override
                              public Color getColor(int i) {
                                  return AgentColors.getColorForAgent(i);
                              }
                          }, 4, 2//params.timeStep
            )));

            VisManager.registerLayer(FastAgentsLayer.create(new FastAgentsLayer.TrajectoriesProvider() {

                                                                @Override
                                                                public Trajectory[] getTrajectories() {
                                                                    return result.getTrajectories();
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
                                                            }, TimeParameterHolder.time
            ));

        }
    }

    protected void printSummary(EvaluatedTrajectory[] trajs) {
        String costStr;
        if (trajs != null) {
            double cost = getSumCost(trajs);
            costStr = String.format("%.2f", cost);
        } else {
            costStr = "inf";
        }

        long runtimeElapsed = System.currentTimeMillis() - params.startedAtMs;

        System.out.print(params.summaryPrefix);
        System.out.print(costStr + ";");
        System.out.print(runtimeElapsed + ";");
        System.out.print(Counters.expandedStatesCounter + ";");
        System.out.println();
    }
    
    protected DirectedGraph<Point, Line> boundEdgeLength(DirectedGraph<Point, Line> original, int maxLength) {
        DirectedWeightedMultigraph<Point, Line> newGraph = new DirectedWeightedMultigraph<Point, Line>(
                new EdgeFactory<Point, Line>() {

                    @Override
                    public Line createEdge(Point sourceVertex,
                                           Point targetVertex) {
                        return new Line(sourceVertex, targetVertex);
                    }
                }) {
					private static final long serialVersionUID = 1L;

					@Override
					public double getEdgeWeight(Line line) {
						return line.getDistance();
					}
        };
        
        for (Line edge : original.edgeSet()) {
        	int nSegments = (int) Math.ceil(edge.getDistance() / (double) maxLength);
        	Point[] points = Util.breakLineToEqualSegments(edge, nSegments);
        	
        	boolean roundingBreaksMaxLength = false;
        	
        	
        	// check whether the property was not lost due to rounding to 2i
        	for (int i = 1; i < points.length; i++) {
				if (points[i].distance(points[i-1]) > maxLength) {
					roundingBreaksMaxLength = true;
				}
			}
        	
        	if (roundingBreaksMaxLength)
        		points = Util.breakLineToEqualSegments(edge, nSegments+2);
        	
        	for (int i = 1; i < points.length; i++) {
				if (points[i].distance(points[i-1]) > maxLength) {
					throw new RuntimeException("Cannot construct the segments... " + points[i-1] + " -> " + points[i] + " distance: " + points[i].distance(points[i-1]));
				}
			}
        	
        	for (int i=0; i < points.length; i++) {
        		newGraph.addVertex(points[i]);
        	}
        	
        	for (int i=0; i < points.length-1; i++) {
        		newGraph.addEdge(points[i], points[i+1]);
        	}
        }
        
        return newGraph;
    }
    
}
