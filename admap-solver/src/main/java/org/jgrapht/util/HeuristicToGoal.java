package org.jgrapht.util;

public interface HeuristicToGoal<V> {
    double getCostToGoalEstimate(V current);
}
