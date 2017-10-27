package tt.jointeuclid2ni.probleminstance.generator;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.region.Circle;
import tt.euclid2i.vis.PointLayer;
import tt.euclid2i.vis.PointLayer.PointProvider;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.util.Args;
import cz.agents.alite.vis.VisManager;


public class InfrastructureChecker {
	
	public static final Logger LOGGER = Logger.getLogger(InfrastructureChecker.class);

    public static void main(String[] args) throws FileNotFoundException {
        String xml = Args.getArgumentValue(args, "-problemfile", true);
        String bodyRadiusStr = Args.getArgumentValue(args, "-bodyradius", false);
        int bodyRadius = Integer.parseInt(bodyRadiusStr);

        File file = new File(xml);
        EarliestArrivalProblem problem;

        problem = TrajectoryCoordinationProblemXMLDeserializer.deserialize(new FileInputStream(file));

        VisUtil.initVisualization(problem, "Trajectory Tools Vis (triangulation generator)" , null, 10);
        VisUtil.visualizeEarliestArrivalProblem(problem);
        
        DirectedGraph<Point, Line> graph = problem.getPlanningGraph();
        List<Point> docks = Arrays.asList(problem.getDocks());
		
    	List<Set<Point>> originalComponents  = (new ConnectivityInspector<Point, Line>(graph)).connectedSets();
		if (originalComponents.size() != 1) {
			throw new RuntimeException("The graph must be connected.");
		} 
		
		LOGGER.info("Checking valid infrastructure: Start");
		
		// assume that the largest connected component is the transit part of the infrastructure
		final DirectedGraph<Point, Line> graphWithoutDocks = removeDocksFromGraph(graph, docks, 2*bodyRadius);
		List<Set<Point>> newComponents  = (new ConnectivityInspector<Point, Line>(graphWithoutDocks)).connectedSets();
		final Set<Point> transitInfrastructurePoints = getLargestComponent(newComponents);
		
		List<Point> badDocks = new LinkedList<Point>();
		List<Point> goodDocks = new LinkedList<Point>();
		
		VisManager.registerLayer(
				PointLayer.create(new PointProvider() {
					
					@Override
					public Collection<Point> getPoints() {
						return transitInfrastructurePoints;
					}
				}, Color.GREEN, 3));
		
		for (Point dock : docks) {
			LOGGER.info("Checking valid infrastructure: Validating " + dock);
			Set<Point> otherDocks = new HashSet<>(docks);
			otherDocks.remove(dock);
			final DirectedGraph<Point, Line> graphWithoutOtherDocks = removeDocksFromGraph(graph, otherDocks, 2*bodyRadius);
			
			GraphPath<Point, Line> path = null;
			
			if (graphWithoutOtherDocks.containsVertex(dock)) {
				path = AStarShortestPathSimple.findPathBetween(graphWithoutOtherDocks, new HeuristicToGoal<Point>() {
	
					@Override
					public double getCostToGoalEstimate(Point current) {
						return 0;
					}
				}, dock, new Goal<Point>() {
	
					@Override
					public boolean isGoal(Point current) {
						return transitInfrastructurePoints.contains(current);
					}
				});
			}
			
			if (path == null) {
				badDocks.add(dock);
				LOGGER.error("Checking valid infrastructure: Endpoint " + dock + " cannot reach the transit infrastructure.");
			} else {
				goodDocks.add(dock);
				LOGGER.info("Checking valid infrastructure: OK ");
			}
			
		}
		LOGGER.info("Checking valid infrastructure: Finished");
		
		System.out.println("Good docks:");
		for (Point dock : goodDocks) {
			System.out.print(dock.x + "," + dock.y + " ");
		}
		
		System.out.println();
		System.out.println("Bad docks:");
		for (Point dock : badDocks) {
			System.out.print(dock.x + "," + dock.y + " ");
		}
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
	
    private static Set<Point> getLargestComponent(List<Set<Point>> components ) {
    	
    	Set<Point> largestComponent = null;
    	for (Set<Point> component : components) {
    		if (largestComponent == null || component.size() > largestComponent.size()) {
    			largestComponent = component;
    		}
    	}   
    	
    	return largestComponent;
	}
	
}
