package cz.agents.alite.planner.general;

/**
 * A specification of the planning problem. In contrast to usual way of
 * specifying planning problems, the starting state and final state are not
 * specified explicitly here. Instead, a dummy operator yielding the starting 
 * state is introduced. Similarly, each operator instance is able to determine
 * if it leads to the goal state. 
 * 
 * The intermediate states are represented as the effects of individual operator instances.
 */
public abstract class Problem {	
	public abstract Operator getStartingOperator();
}
