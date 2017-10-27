package cz.agents.alite.communication.acquaintance;

/**
 * DefaultPlanExecutor for DefaultPlanBase.
 */
public class DefaultPlanExecutor implements PlanExecutor {

    private ExecutorFeedback feedback;
    private DefaultPlan plan;

    @Override
    public void registerExecutorFeedback(ExecutorFeedback feedback) {
        this.feedback = feedback;
    }

    @Override
    public void executePlan(Plan plan) {
        this.plan = (DefaultPlan) plan;
    }

    @Override
    public void addPlan(Plan plan) {
        this.plan = (DefaultPlan) plan;
    }

    @Override
    public void scratchPlan() {
        plan.clear();
    }

    /**
     * Gets the current plan.
     *
     * @return plan
     */
    public DefaultPlan getPlan() {
        return plan;
    }

    /**
     * Reports successfull execution of the task.
     *
     * @param task
     */
    public void finalize(Task task) {
        feedback.done(task);
    }

    /**
     * Reports unsuccessfull execution of the task.
     *
     * @param task
     */
    public void fail(Task task) {
        feedback.failed(task);
    }
}
