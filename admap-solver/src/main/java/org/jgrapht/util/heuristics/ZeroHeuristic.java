package org.jgrapht.util.heuristics;

import org.jgrapht.util.GeneralHeuristic;

public class ZeroHeuristic<S> implements GeneralHeuristic<S> {

    @Override
    public double getCostEstimate(S from, S to) {
        return 0;
    }

    @Override
    public double getCostToGoalEstimate(S current) {
        return 0;
    }
}
