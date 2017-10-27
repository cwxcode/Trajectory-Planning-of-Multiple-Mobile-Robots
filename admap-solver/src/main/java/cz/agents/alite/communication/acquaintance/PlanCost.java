package cz.agents.alite.communication.acquaintance;

/**
 *  For plan cost comparisions
 *
 * @author Jiri Vokrinek
 */
public interface PlanCost {

    /**
     * Compares this plan cost with given cost
     *
     * @param cost the cost to compare
     * @return true if this PlanCost is lower then cost, or if cost is null
     */
    boolean isLower(PlanCost cost);

    /**
     * Computes scalar representation of the plan cost.
     *
     * @return integer equivalent of the cost
     */
    Long getIntegerEquivalent();

    /**
     * Computes the summation of the cost and given cost.
     * This PlanCost object remains unchanged.
     *
     * @param cost cost to add
     * @return Summ of the costs coresponding to PlanCost = this + cost
     */
    PlanCost add(PlanCost cost);

    /**
     * Computes the substraction of the cost and given cost.
     * This PlanCost object remains unchanged.
     *
     * @param cost cost to sub
     * @return Summ of the costs coresponding to PlanCost = this - cost
     */
    PlanCost sub(PlanCost cost);
    

}
