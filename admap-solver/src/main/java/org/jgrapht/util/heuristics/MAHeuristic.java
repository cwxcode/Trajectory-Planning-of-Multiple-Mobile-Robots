package org.jgrapht.util.heuristics;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.util.HeuristicToGoal;

public class MAHeuristic<S, M, E> implements HeuristicToGoal<M> {

    private HeuristicProvider<S, E> heuristicProvider;
    private MAStateProvider<S, M> stateProvider;

    private HeuristicToGoal<S>[] heuristics;
    private int size;

    public MAHeuristic(DirectedGraph<S, E> graph, M goal, HeuristicProvider<S, E> heuristicProvider, MAStateProvider<S, M> stateProvider) {
        this.heuristicProvider = heuristicProvider;
        this.stateProvider = stateProvider;

        S[] goals = stateProvider.getAgentsStates(goal);
        this.size = goals.length;
        this.heuristics = getIndividualHeuristics(graph, goals);
    }

    @SuppressWarnings("unchecked")
    private HeuristicToGoal<S>[] getIndividualHeuristics(DirectedGraph<S, E> graph, S[] goals) {
        HeuristicToGoal<S>[] heuristics = new HeuristicToGoal[size];

        for (int i = 0; i < size; i++) {
            heuristics[i] = heuristicProvider.getHeuristicToGoal(graph, goals[i]);
        }

        return heuristics;
    }

    @Override
    public double getCostToGoalEstimate(M current) {
        S[] agentsStates = stateProvider.getAgentsStates(current);

        double estimate = 0;
        for (int i = 0; i < size; i++) {
            estimate += heuristics[i].getCostToGoalEstimate(agentsStates[i]);
        }

        return estimate;
    }
}
