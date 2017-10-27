package cz.agents.alite.planner.general;
/**
 * An operator that can be applied in a a given planning domain.
 * An object that implements interface Operator also represent the state
 * reached after the given operator instance is applied to the planning problem.
 * We call this state S.
 */

public interface Operator extends Comparable<Operator>{
    /** Actions (operator instances) applicable in state S, i.e. the state in which this operator was applied. **/
    public Operator[] getNeighbors();
    /** Determines if state S, i.e. the state in which this operator was applied, is the goal state. **/
    public boolean isGoal();


    /** Returns the cost of getting to state S, i.e. the state in which this operator was applied. In A*, this will be g. **/
    public double getCost();
    /** Returns the heuristic guess of getting from state S, i.e. the state in which this operator was applied top the goal state. In A*, this will be h. */
    public double getHeuristicEstimate();

    /** Sets the predecessor of this operator, so that the sequence can be recovered from the last operator.*/
    public void setPredecessor(Operator pred);
    public Operator getPredecessor();

}
