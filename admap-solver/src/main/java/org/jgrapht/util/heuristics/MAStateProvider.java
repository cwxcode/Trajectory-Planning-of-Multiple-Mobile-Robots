package org.jgrapht.util.heuristics;

public interface MAStateProvider<S, M> {

    public S[] getAgentsStates(M state);

}
