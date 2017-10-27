package cz.agents.alite.communication.acquaintance;

import java.io.Serializable;

/**
 * The default arithmetic plan cost.
 * Floats and Doubles are internally handled as float, other types as long.
 * Note the isLower, add and sub methods allways treat the argument as the same type as internal price of this instance.
 *
 * @author Jiri Vokrinek
 */
public class DefaultPlanCost implements PlanCost, Serializable {

	private static final long serialVersionUID = 8909772959707669132L;
	private Number cost;
    private boolean isFloating;

    /**
     * Default constructor.
     *
     * @param cost
     */
    public DefaultPlanCost(Number cost) {
        this.cost = cost;
        if ((cost instanceof Double) || (cost instanceof Float)) {
            isFloating = true;
        } else {
            isFloating = false;
        }
    }

    public boolean isLower(PlanCost cost) {
        if (isFloating) {
            return this.cost.floatValue() < ((DefaultPlanCost) cost).getFloat();
        } else {
            return this.cost.longValue() < ((DefaultPlanCost) cost).getLong();
        }

    }

    public Long getIntegerEquivalent() {
        return cost.longValue();
    }

    public PlanCost add(PlanCost cost) {
        if (isFloating) {
            return new DefaultPlanCost(this.cost.floatValue() + ((DefaultPlanCost) cost).getFloat());
        } else {
            return new DefaultPlanCost(this.cost.longValue() + ((DefaultPlanCost) cost).getLong());
        }

    }

    public PlanCost sub(PlanCost cost) {
        if (isFloating) {
            return new DefaultPlanCost(this.cost.floatValue() - ((DefaultPlanCost) cost).getFloat());
        } else {
            return new DefaultPlanCost(this.cost.longValue() - ((DefaultPlanCost) cost).getLong());
        }
    }

    private float getFloat() {
        return cost.floatValue();
    }

    private long getLong() {
        return cost.longValue();
    }

    @Override
    public String toString() {
        return cost.toString();
    }


}
