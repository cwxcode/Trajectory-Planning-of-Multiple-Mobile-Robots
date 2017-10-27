package org.jgrapht.util;

public interface HeuristicPointToPoint<S> {
    double getCostEstimate(S from, S to);
}
