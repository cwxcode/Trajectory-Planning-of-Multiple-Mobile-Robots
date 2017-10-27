package cz.agents.alite.communication.acquaintance;

import cz.agents.alite.communication.acquaintance.Task.TaskListener;

/**
 *  Interface for the Plan Base definition
 *  The Plan Base is persistent entity able to maintain plans for the tasks.
 *
 * @author Jiri Vokrinek
 */
public interface PlanBase {

    /**
     * Evaluates potential task insertion.
     *
     * @param task
     * @return the cost increase of the new potential plan, null if plan not found
     */
    PlanCost evaluateInsertion(Task task);

    /**
     * Evaluates potential task removal.
     * The cost of the potential task removal cannot be lower than
     * cost of subsequent insertion of the same task.
     *
     * @param task
     * @return the cost equivalent of savings when the task will be removed, null if task cannod be removed (e.g. is under execution)
     */
    PlanCost evaluateRemoval(Task task);

    /**
     * Adds the task to the PlanBase.
     * The plan is created and the respective plan cost increase is computed.
     *
     * @param task
     * @param taskListener callback for task execution feedback
     * @return total plan cost increase, null if plan not found
     */
    PlanCost insertTask(Task task, TaskListener taskListener);

    /**
     * Remove the task from the PlanBase.
     * The plan is updated and respective plan cost savings is computed.
     *
     * @param task
     * @return total plan cost decrease.
     */
    PlanCost removeTask(Task task);

    /**
     * Computes the total cost of overall plan to fullfill all the inserted tasks.
     *
     * @return PlanCost of the total plan.
     */
    PlanCost getTotalCost();

    /**
     * Registeres the PlanExecutor to this PlanBase.
     * PlanBase is responsible for creating the appropriate ExecutorFeedback and registration using PlanExecutor.registerExecutorFeedback
     *
     * @param executor
     */
    public void registerExecutor(PlanExecutor executor);

    /**
     * Exception for planning failure
     */
    class TaskPlanError extends Exception {

        private static final long serialVersionUID = 416794555267004753L;

        TaskPlanError(String string) {
            super(string);
        }
    }
}
