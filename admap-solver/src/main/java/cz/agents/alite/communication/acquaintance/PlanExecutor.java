package cz.agents.alite.communication.acquaintance;

/**
 * PlanExecutor interface.
 * It represents the entity that realizes the plans.
 *
 * @author Jiri Vokrinek
 */
public interface PlanExecutor {

    /**
     * Binds the executor to the specific feedback (usually implemented by PlanBase).
     * @param feedback
     */
    public void registerExecutorFeedback(ExecutorFeedback feedback);

    /**
     * Executes given plan.
     *
     * @param plan
     */
    public void executePlan(Plan plan);

    /**
     * Extends actual plan by given plan.
     *
     * @param plan
     */
    public void addPlan(Plan plan);

    /**
     * Invalidates curen plan and stops the execution.
     */
    public void scratchPlan();

    /**
     * The encapsulating interface for feedback from executor to PlanBase.
     * Enrich the API of your implementation for capturing the specifics of your PlanExecutor to PlanBase interactions.
     */
    public interface ExecutorFeedback {

        /**
         * Call when Task execution is done.
         *
         * @param task
         */
        public void done(Task task);

        /**
         * Call when Task execution fails.
         *
         * @param task
         */
        public void failed(Task task);

        /**
         * Call when Plan cannot be executed.
         *
         */
        public void planUnreachable();
    }
}
