package tt.jointeuclid2ni.solver.impl;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.graph.DirectedGraphDelegator;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.HeuristicProvider;
import org.jgrapht.util.heuristics.PerfectHeuristic;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.jointeuclid2ni.operatordecomposition.ODEdge;
import tt.jointeuclid2ni.operatordecomposition.ODNode;
import tt.jointeuclid2ni.operatordecomposition.ODUtils;
import tt.jointeuclid2ni.operatordecomposition.ODWrapper;
import tt.jointeuclid2ni.operatordecomposition.utils.ODGoal;
import tt.jointeuclid2ni.operatordecomposition.utils.ODHeuristic;
import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointtraj.solver.SearchResult;
import tt.util.NotImplementedException;

public class AlgorithmODCN extends AbstractAlgorithm {

    public AlgorithmODCN() {
        super();
    }

    @Override
    public SearchResult solveProblem() {
    	
    	if (params.timeStep == 1) {
    		throw new RuntimeException("This algorithm requires that all spatio-temporal edges have the same duration. Use -timestep parameter to set the duration of an edge.");
    	}
    	
        DirectedGraph<Point, Line>[] graph = createSpatialGraphs(problem);
        ODWrapper odWrapper = new ODWrapper(graph, problem.getTargets(), params.timeStep, true);
        ODNode start = ODNode.start(problem.getStarts(), problem.getBodyRadiuses());

        ODHeuristic odHeuristic = new ODHeuristic(graph, problem.getTargets(), new HeuristicProvider<Point, Line>() {
            @Override
            public HeuristicToGoal<Point> getHeuristicToGoal(DirectedGraph<Point, Line> graph, Point goal) {
                return getHeuristic(graph, goal, params.timeStep);
            }
        });

        AStarShortestPathSimple<ODNode, ODEdge> astar
                = new AStarShortestPathSimple<ODNode, ODEdge>(odWrapper, odHeuristic, start, new ODGoal(problem.getTargets()));

        GraphPath<ODNode, ODEdge> path = astar.findPathCostAndDeadlineLimit(Integer.MAX_VALUE, params.runtimeDeadlineMs);
        EvaluatedTrajectory[] trajectories = ODUtils.toTrajectories(path, problem.getTargets(), params.maxTime, params.maxSpeed, params.timeStep);

        return new SearchResult(trajectories, System.currentTimeMillis() < params.runtimeDeadlineMs);
    }

    private DirectedGraph<Point, Line>[] createSpatialGraphs(EarliestArrivalProblem problem) {
        @SuppressWarnings("unchecked")
		DirectedGraph<Point, Line> graph[] = new DirectedGraph[problem.nAgents()];
        for (int i = 0; i < problem.nAgents(); i++) {
        	int edgeLengthBound = (int) Math.floor(params.timeStep * problem.getMaxSpeed(i));
            graph[i] = preparePlanningGraphForAgent(i, edgeLengthBound, i == 0 /* show only first*/);
        }
        return graph;
    }
    
    public HeuristicToGoal<Point> getHeuristic(DirectedGraph<Point, Line> graph, Point goal, final int timeStep) {
        switch (params.heuristic) {
            case PERFECT:
                return new PerfectHeuristic<Point, Line>(new DirectedGraphDelegator<Point, Line>(graph) {

					@Override
					public double getEdgeWeight(Line e) {
						return timeStep ;
					}
     			
				}, goal);
            case L1:
                return new tt.euclid2i.discretization.L1Heuristic(goal);
            case L2:
                return new tt.euclid2i.discretization.L1Heuristic(goal);
            default:
                throw new NotImplementedException();
        }
    }
}
