package cz.agents.alite.planner.general.graph;

import java.util.Set;

import org.jgrapht.graph.AbstractGraph;

import cz.agents.alite.planner.general.Operator;

public class EdgeMoveOperator<N, E, G extends AbstractGraph<N, E>> implements Operator {

    protected E edge;
    protected N startNode;
    protected N endNode;
    protected G graph;
    protected GraphPlanningProblem<N, E, G> problem;

    boolean gcomputed = false;
    boolean hcomputed = false;
    double g;
    double h;

    private Operator predecessor = null;

    public EdgeMoveOperator(N startNode, E edge, GraphPlanningProblem<N, E, G> graphPlanningProblem) {
        this.startNode = startNode;
        this.edge = edge;
        this.problem = graphPlanningProblem;
        this.graph = problem.getGraph();
        if (startNode.equals(graph.getEdgeSource(edge))) {
            this.endNode = graph.getEdgeTarget(edge);
        }
        else {
            this.endNode = graph.getEdgeSource(edge);
        }
    }

    @Override
    public double getCost() {
        if (! gcomputed) {
            g = getPredecessor().getCost();
            gcomputed = true;
        }

        return g;
    }

    @Override
    public double getHeuristicEstimate() {
        if (! hcomputed) {
            h = problem.getHeuristicEstimate(endNode);
            hcomputed = true;
        }

        return h;
    }

    @Override
    public Operator[] getNeighbors() {
        Set<E>edges = graph.edgesOf(endNode);
        Operator[] neighbors = new Operator[edges.size()];
        int i = 0;
        for (E edge : edges) {
            neighbors[i] = new EdgeMoveOperator<N, E, G>(endNode, edge, problem);
            neighbors[i].setPredecessor(this);
            i++;
        }

        return neighbors;
    }

    @Override
    public Operator getPredecessor() {
        return predecessor;
    }

    @Override
    public boolean isGoal() {
        return endNode.equals(problem.goalNode);
    }

    @Override
    public void setPredecessor(Operator pred) {
        this.predecessor = pred;
    }

    @Override
    public int compareTo(Operator o) {
        return (int)Math.round(Math.signum((getCost()+getHeuristicEstimate()) - (o.getCost()+o.getHeuristicEstimate())));
    }

    public N getEndNode() {
        return endNode;
    }

    public E getEdge() {
        return edge;
    }

    @Override
    public String toString(){
        //return startNode + ":" + endNode;
        return "["+startNode+">"+endNode+"]";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EdgeMoveOperator) {
            return startNode.equals(((EdgeMoveOperator) obj).startNode)
                    && endNode.equals(((EdgeMoveOperator) obj).endNode);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return endNode.hashCode();
    }




}
