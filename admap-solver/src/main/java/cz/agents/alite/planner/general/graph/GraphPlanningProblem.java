package cz.agents.alite.planner.general.graph;

import org.jgrapht.graph.AbstractGraph;

import cz.agents.alite.planner.general.Operator;
import cz.agents.alite.planner.general.Problem;

public class GraphPlanningProblem<N, E, G extends AbstractGraph<N,E>> extends Problem {
	
	N startNode;
	N goalNode;
	G graph;
	public Heuristic<N> heuristic;
	
	public GraphPlanningProblem(N startNode, N goalNode, G graph, Heuristic<N> heuristic) {
		super();
		this.startNode = startNode;
		this.goalNode = goalNode;
		this.graph = graph;
		this.heuristic = heuristic;
	}

	@Override
	public Operator getStartingOperator() {
		return new StartOperator<N, E, G>(startNode, this);
	}
	
	public G getGraph() {
		return graph;
	}
	
	public double getHeuristicEstimate(N current) {
		return heuristic.getEstimate(current, goalNode);
	} 

}
