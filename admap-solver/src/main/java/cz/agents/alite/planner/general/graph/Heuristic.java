package cz.agents.alite.planner.general.graph;

public interface Heuristic<N> {
	public double getEstimate(N current, N goal);
}
