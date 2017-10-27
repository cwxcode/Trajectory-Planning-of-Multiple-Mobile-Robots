package tt.jointeuclid2ni.probleminstance.generator;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.swing.plaf.basic.BasicToolBarUI.DockingListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.NearestNeighborIterator;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;
import cz.agents.alite.vis.VisManager;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Union;
import tt.euclid2i.discretization.GridBasedRoadmap;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemImpl;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.util.Args;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;

public class TriangulationGenerator {
	
	public static final Logger LOGGER = Logger.getLogger(TriangulationGenerator.class);

    public static void main(String[] args) throws FileNotFoundException {
        String xml = Args.getArgumentValue(args, "-problemfile", true);
        String outFile = Args.getArgumentValue(args, "-outfile", false);
        String dispersionStr = Args.getArgumentValue(args, "-dispersion", false);
        String connectionRadiusStr = Args.getArgumentValue(args, "-connectionradius", false);
        String bodyRadiusStr = Args.getArgumentValue(args, "-bodyradius", false);
        
        // generates roadmap such that every two endpoints will be connected without crossing any other endpoint 
        boolean generateInfrastructure = Args.isArgumentSet(args, "-generateinfrastructure");       
        boolean roadmapForDocks = Args.isArgumentSet(args, "-roadmapfordocks");
        boolean addBoundaryDocks = Args.isArgumentSet(args, "-boundarydocks");
        boolean addSparseDocks = Args.isArgumentSet(args, "-sparsedocks");
        boolean showVis = Args.isArgumentSet(args, "-showvis");

        final int dispersion = Integer.parseInt(dispersionStr);
        final int bodyRadius = Integer.parseInt(bodyRadiusStr);
        final int connectionRadius = Integer.parseInt(connectionRadiusStr);

        File file = new File(xml);
        EarliestArrivalProblem problem;

        problem = TrajectoryCoordinationProblemXMLDeserializer.deserialize(new FileInputStream(file));

        Collection<Region> obstacles = problem.getObstacles();

        if (showVis) {
            VisUtil.initVisualization(problem, "Trajectory Tools Vis (triangulation generator)" , null, 10);
            VisUtil.visualizeEarliestArrivalProblem(problem);
        }

        Region boundaryRegion = problem.getEnvironment().getBoundary();
        Collection<Region> boundaryRegionDeflated = Util.inflateRegions(Collections.singleton(boundaryRegion), bodyRadius);

        final Collection<Region> inflatedObstacles = new LinkedList<Region>();
        inflatedObstacles.addAll(Util.inflateRegions(obstacles, bodyRadius));

        // break polygons into max-len segments
        
        LinkedList<Point> customPoints = new LinkedList<Point>();
       
        Collection<Region> moreInflatedObstacles = new LinkedList<Region>();
		moreInflatedObstacles.addAll(Util.inflateRegions(obstacles, bodyRadius+5));
        
		for (Region region : moreInflatedObstacles)  {
			
			Polygon poly;
			if (region instanceof Polygon) {
				poly = (Polygon) region;
			} else if (region instanceof Circle){
				poly = ((Circle) region).toPolygon(16);
			} else  {
				throw new RuntimeException("Unexpected region: " + region);
			}
			
			
        	Polygon polygon = Util.breakPolygonToMaxLenSegments(poly, connectionRadius-1);
        	customPoints.addAll(Arrays.asList(polygon.getPoints()));
        }
		
		// add docks from the input problem
		if (problem.getDocks() != null) {
			customPoints.addAll(Arrays.asList(problem.getDocks()));
		}
		
		Collection<Point> newDocks = new LinkedList<Point>();
		Collection<Region> moreInflatedBoundaries = Util.inflateRegions(Collections.singleton(boundaryRegion), bodyRadius+2);
		for (Region moreInflatedBoundary : moreInflatedBoundaries) {
			Polygon brokenBoundary = Util.breakPolygonToMaxLenSegments((Polygon) moreInflatedBoundary, connectionRadius-1);
			customPoints.addAll(Arrays.asList(brokenBoundary.getPoints()));
			newDocks.addAll(Arrays.asList(brokenBoundary.getPoints()));
		}
        
		if (addBoundaryDocks) {
			for (Iterator<Point> iterator = newDocks.iterator(); iterator.hasNext();) {
				Point dock = iterator.next();
				if (!Util.isInFreeSpace(dock, boundaryRegionDeflated, inflatedObstacles)) {
					iterator.remove();
				}
			}
			newDocks = Util.selectDispersedPoints(newDocks, 2*bodyRadius+1);
		} 
		
		LOGGER.info("Constructing the grid-based roadmap. There is " + countPolygonCorners(boundaryRegionDeflated) + " points at deflated boundary, " 
		+ countPolygonCorners(inflatedObstacles) + " points at inflated obstacles and " + customPoints.size() + " custom points. ");
		
		DirectedGraph<Point, Line> planningGraph 
			= new GridBasedRoadmap(dispersion, connectionRadius, customPoints.toArray(new Point[0]), boundaryRegionDeflated, inflatedObstacles, true);
		
		LOGGER.info("Done... |V|=" + planningGraph.vertexSet().size() + " |E|=" + planningGraph.edgeSet().size() );
		
		if (addSparseDocks){
			newDocks = constructSparseDocks(planningGraph, boundaryRegion, obstacles, 2*bodyRadius+1, bodyRadius);
		}
		
		if (generateInfrastructure) {
			newDocks = constructInfrastructureDocks(planningGraph, newDocks, bodyRadius);
		}
		
		Collection<Point> outputDocks = null;
		if (addBoundaryDocks | addSparseDocks) {
			outputDocks = newDocks;
		} else if (problem.getDocks() != null) {
			outputDocks = Arrays.asList(problem.getDocks());
		}
		
		if (roadmapForDocks) {
			planningGraph = connectInfrastructureDocks(planningGraph, boundaryRegionDeflated, inflatedObstacles, outputDocks, bodyRadius);
		}
		
        if (showVis) {
            VisUtil.visualizeGraph(planningGraph, boundaryRegionDeflated);
            
            if (outputDocks != null) {
            final Collection<Point> docksFinal = outputDocks;
            VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {

                @Override
                public Collection<? extends Region> getRegions() {
                	LinkedList<Circle> circles = new LinkedList<Circle>();
                	for (Point dock : docksFinal) {
                		circles.add(new Circle(dock, bodyRadius));
                	}
                	return circles;
                }
            }, Color.BLUE));
            }
        }

		TrajectoryCoordinationProblemImpl augmentedProblem = new TrajectoryCoordinationProblemImpl(
				problem.getEnvironment(), problem.getStarts(),
				problem.getTargets(), problem.getBodyRadiuses(),
				problem.getMaxSpeeds(), planningGraph,
				(outputDocks != null ? outputDocks.toArray(new Point[0]) : null), null);

        PrintStream outStream = System.out;

        if (outFile != null) {
            outStream = new PrintStream(new File(outFile));
        }

        TrajectoryCoordinationProblemXMLSerializer.serialize(augmentedProblem, outStream);
    }
    
    private static void checkInfrastructure(
			DirectedGraph<Point, Line> graph,
			Collection<Point> outputDocks, int dockRadius) {
    	
    	List<Set<Point>> originalComponents  = (new ConnectivityInspector<Point, Line>(graph)).connectedSets();
		if (originalComponents.size() != 1) {
			LOGGER.error("Checking valid infrastructure: original component has " + originalComponents + ".");
		} else {
			final DirectedGraph<Point, Line> graphWithoutDocks = removeDocksFromGraph(graph, outputDocks, 2*dockRadius);
			List<Set<Point>> newComponents  = (new ConnectivityInspector<Point, Line>(graph)).connectedSets();
			if (newComponents.size() != originalComponents.size()) {
				LOGGER.error("Checking valid infrastructure: The docks do not form infrastructure!");
			} else{
				LOGGER.info("Checking valid infrastructure: OK");
			}
		}
	}

	private static int countPolygonCorners(Collection<Region> regions) {
		int points = 0;
    	for (Region region : regions) { 
    		if (region instanceof Polygon) {
    			points += ((Polygon) region).getPoints().length;
    		}
    	}
		return points;
	}

	private static DirectedGraph<Point, Line> connectInfrastructureDocks(DirectedGraph<Point, Line> planningGraph, 
    		Collection<Region> boundaryRegions, 
    		Collection<Region> obstacles,
    		Collection<Point> docks, 
    		int bodyRadius) {
    	
    	
    	// Find the transit network T
    	final DirectedGraph<Point, Line> transitNetwork = removeDocksFromGraph(planningGraph, docks, 2*bodyRadius);
    	removeDisconnectedComponents(transitNetwork);
    	
      	ConnectivityInspector<Point, Line> transitNetworkConnectivityInspector = new ConnectivityInspector<Point, Line>(transitNetwork);
		int components = transitNetworkConnectivityInspector.connectedSets().size();
		assert components == 1 && transitNetwork.vertexSet().size() != 0;
		Point arbitraryTransitNetworkVertex = transitNetwork.vertexSet().iterator().next();

		for (Point dock : docks) {    		
			// Construct a graph that does not contain other docks
    		LinkedList<Point> otherDocks = new  LinkedList<Point>(docks);
    		otherDocks.remove(dock);
    		LinkedList<Region> otherDockRegions = new LinkedList<>();
    		for(Point otherDock : otherDocks) {
    			otherDockRegions.add(new Circle(otherDock, 2*bodyRadius));
    		}    		
    		DirectedGraph<Point, Line> graphWithoutOtherDocks = removeDocksFromGraph(planningGraph,  otherDocks, 2*bodyRadius);
    		ConnectivityInspector<Point, Line> graphWithoutOtherDocksConnectivityInsp = new ConnectivityInspector<Point, Line>(graphWithoutOtherDocks);
    		
    		if (graphWithoutOtherDocks.containsVertex(dock) && graphWithoutOtherDocksConnectivityInsp.pathExists(dock, arbitraryTransitNetworkVertex)) {
    			// okay, there is a path that allows us to reach the transit network of the infrastructure
    		} else {
    			// not-okay: attempt to connect to one of the vertices from the transit network, start from the closest one
    			KdTree<Point> knnTree = new KdTree<Point>(2);
    			
    			for (Point tnVertex : transitNetwork.vertexSet()) {
    				knnTree.addPoint(key(tnVertex), tnVertex);
    			}
    			
    			NearestNeighborIterator<Point> iterator = knnTree.getNearestNeighborIterator(key(dock), transitNetwork.vertexSet().size(), new SquareEuclideanDistanceFunction());

	            while (iterator.hasNext()) {
	                Point next = iterator.next();
	                if (Util.isVisible(dock, next, obstacles) && Util.isVisible(dock, next, boundaryRegions) && Util.isVisible(dock, next, otherDockRegions)) {
	                	planningGraph.addEdge(dock, next, new Line(dock, next));
	                	break;
	                }
	            }
	            
	            LOGGER.error("The docks cannot be connected by an infrastructure roadmap. No edge connects dock " + dock + " with transit infrastructure." );	            
    		}
    	}
    	
    	
    	return planningGraph;
    }
    

    private static double[] key(Point point) {
        return new double[]{point.getX(), point.getY()};
    }
    

	static private void removeDisconnectedComponents(DirectedGraph<Point, Line> graph) {
		ConnectivityInspector<Point, Line> connectivityInspector = new ConnectivityInspector<Point, Line>(graph);
		List<Set<Point>> components = connectivityInspector.connectedSets();
		
		Set<Point> largestComponent = null;
		for (Set<Point> component : components) {
			if (largestComponent == null || component.size() > largestComponent.size()) {
				largestComponent = component;
			}
		}
		
		HashSet<Point> originalVertices = new HashSet<Point>(graph.vertexSet());
		for (Point vertex : originalVertices) {
			if (!largestComponent.contains(vertex)) {
				graph.removeVertex(vertex);
			}
		}
	}

	/**
     * Creates a set of docks such that any two docks are reachable with at leastmessage 2*bodyRadius clearance from other dock
     */
    private static Collection<Point> constructInfrastructureDocks(
			DirectedGraph<Point, Line> planningGraph, Collection<Point> candidateDocks, int bodyRadius) {
		Collection<Point> docksMaintainingConnectivity = new LinkedList<Point>();
		
		// 1. For each candidate check if the addition of the new dock does not disconnect the workspace.
		for (Point candidateDock : candidateDocks) {
			
			LinkedList<Point> currentDcksUCandidateDock = new LinkedList<>(docksMaintainingConnectivity);
			currentDcksUCandidateDock.add(candidateDock);
			final DirectedGraph<Point, Line> graphWithoutDocks = removeDocksFromGraph(planningGraph, currentDcksUCandidateDock, 2*bodyRadius);

			ConnectivityInspector<Point, Line> connectivityInspector = new ConnectivityInspector<Point, Line>(graphWithoutDocks);
			
			int components = connectivityInspector.connectedSets().size();
			
//			VisManager.registerLayer(GraphLayer.create(new GraphProvider<Point, Line>() {
//
//				@Override
//				public Graph<Point, Line> getGraph() {
//					return graphWithoutDocks;
//				}
//			}, new ProjectionTo2d(), Color.BLUE, Color.BLUE, 1, 2));
			
			if (components == 1) {
				docksMaintainingConnectivity.add(candidateDock);
			}
		}
		
		LOGGER.debug("There is " + docksMaintainingConnectivity.size() + "docks maintaining connectivity");
	
		
		final DirectedGraph<Point, Line> transitGraph = removeDocksFromGraph(planningGraph, docksMaintainingConnectivity, 2*bodyRadius);
		
		// 2. For each candidate check if there is a path to the infrastructure that is not obstructed by other endpoint		
		Collection<Point> infrastructureDocks = new LinkedList<Point>();
		for (Point candidateDock : docksMaintainingConnectivity) {
			
			LOGGER.debug("checking dock " + candidateDock + " for path existence...");
			
			LinkedList<Point> endpointsWithoutCandidate = new LinkedList<Point>(docksMaintainingConnectivity);
			endpointsWithoutCandidate.remove(candidateDock);
			
			final DirectedGraph<Point, Line> graphWithoutOtherEndpoints = removeDocksFromGraph(planningGraph, endpointsWithoutCandidate, 2*bodyRadius);
			
			if (graphWithoutOtherEndpoints.containsVertex(candidateDock)) {
				// try to find a path to the transit graph
				GraphPath<Point, Line> path = AStarShortestPathSimple.findPathBetween(graphWithoutOtherEndpoints, new HeuristicToGoal<Point>() {
	
					@Override
					public double getCostToGoalEstimate(Point current) {
						return 0;
					}
				}, candidateDock, new Goal<Point>() {
	
					@Override
					public boolean isGoal(Point current) {
						return transitGraph.containsVertex(current);
					}
					
				});
				
				if (path != null) {
					infrastructureDocks.add(candidateDock);
				}
			}
		}
    	
    	return infrastructureDocks;
	}
    
	private static DirectedGraph<Point,Line> removeDocksFromGraph(
			DirectedGraph<Point, Line> planningGraph,
			Collection<Point> docks, int radius) {
		
		DirectedGraph<Point,Line> graph = (DirectedGraph<Point,Line>) ((DirectedWeightedMultigraph<Point, Line>)planningGraph).clone();
		
		Set<Line> edges = new HashSet<Line>(graph.edgeSet());
		for (Line edge : edges) {
			for (Point dock : docks) {
				Circle circle = new Circle(dock, radius);
				if (circle.intersectsLine(edge.getStart(), edge.getEnd())) {
					graph.removeEdge(edge);
				}
			}
		}
		
		Set<Point> vertices = new HashSet<Point>(graph.vertexSet());
		for (Point vertex : vertices) {
			for (Point dock : docks) {
				if (vertex.distance(dock) <= radius) {
					graph.removeVertex(vertex);
				}
			}
		}
		
		return graph;
	}

	/** 
     * finds a set of points that are 
     * a) at least given distance from each otgher 
     * b) at least given distance from the nearest obstacle/boundary.
     **/
	private static Collection<Point> constructSparseDocks(
		DirectedGraph<Point, Line> planningGraph, Region boundaryRegion,
		Collection<Region> obstacles, int clearance, int radius) {
		
		Collection<Region> boundaryRegionDeflated = Util.inflateRegions(Collections.singleton(boundaryRegion), clearance+radius);

        final Collection<Region> inflatedObstacles = new LinkedList<Region>();
        inflatedObstacles.addAll(Util.inflateRegions(obstacles, clearance+radius));
        
        LinkedList<Point> sparsePoints = new LinkedList<Point>();
        Queue<Point> open = new LinkedList<Point>();
        Set<Point> closed = new HashSet<Point>();
        
        open.add(planningGraph.vertexSet().iterator().next());
        
        while (!open.isEmpty()) {
        	Point current = open.poll();
        	
        	if (Util.distanceToNearestPoint(current, sparsePoints) > clearance+2*radius 
        			&& Util.isInFreeSpace(current, boundaryRegionDeflated, inflatedObstacles)) {
        		sparsePoints.add(current);
        	}
        	
        	for (Line edge : planningGraph.outgoingEdgesOf(current)) {
        		Point neighbor = Graphs.getOppositeVertex(planningGraph, edge, current);
        		if (!closed.contains(neighbor)) {
        			open.offer(neighbor);
        			closed.add(neighbor);
        		}
        	}
        }
		
		return sparsePoints;
	}
	
	
	
}
