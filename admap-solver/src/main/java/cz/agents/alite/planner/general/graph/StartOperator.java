package cz.agents.alite.planner.general.graph;

import java.util.Set;

import org.jgrapht.graph.AbstractGraph;

import cz.agents.alite.planner.general.Operator;

public class StartOperator<N, E, G extends AbstractGraph<N, E>> implements Operator {

    protected N node;

    protected G graph;
    protected GraphPlanningProblem<N, E, G> problem;

    boolean hcomputed = false;
    double h;

    public StartOperator(N node, GraphPlanningProblem<N, E, G> graphPlanningProblem) {
        this.node = node;
        this.problem = graphPlanningProblem;
        this.graph = problem.getGraph();
    }

    @Override
    public double getCost() {
        return 0.0;
    }

    @Override
    public double getHeuristicEstimate() {
        if (! hcomputed) {
            // compute h
            h = problem.getHeuristicEstimate(node);
            hcomputed = true;
        }

        return h;
    }

    @Override
    public Operator[] getNeighbors() {
        Set<E>edges = graph.edgesOf(node);
        Operator[] neighbors = new Operator[edges.size()];
        int i = 0;
        for (E edge : edges) {
            neighbors[i] = new EdgeMoveOperator<N, E, G>(node, edge, problem);
            neighbors[i].setPredecessor(this);
            i++;
        }

        return neighbors;
    }

    @Override
    public Operator getPredecessor() {
        return null;
    }

    @Override
    public boolean isGoal() {
        return node.equals(problem.goalNode);
    }

    @Override
    public void setPredecessor(Operator pred) {
        throw new UnsupportedOperationException("Start operator cannot have a predecessor.");
    }

    @Override
    public int compareTo(Operator o) {
        return (int)Math.round(Math.signum((getCost()+getHeuristicEstimate()) - (o.getCost()+o.getHeuristicEstimate())));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if (o instanceof StartOperator) {
            return node.equals(((StartOperator)node).node);
        }
        return false;
    }
}
