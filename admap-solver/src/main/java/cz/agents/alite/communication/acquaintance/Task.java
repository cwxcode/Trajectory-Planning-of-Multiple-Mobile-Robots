package cz.agents.alite.communication.acquaintance;

/**
 * Interfance for Task used by agents' acquaintance models.
 *
 * @author Jiri Vokrinek
 */
public interface Task {

    /**
     *
     * @return unique task name
     */
    String getTaskType();

    /**
     * Check for task compatibility with the given task type
     *
     * @param taskType the task type to check
     * @return true if this task is compatible, false otherwise
     */
    boolean compliesType(String taskType);

    /**
     * Gives unique task identiffication
     *
     * @return task ID
     */
    String getTaskID();

    /**
     * Listener for task completition callback
     */
    public interface TaskListener {

        /**
         * Invoked when task is successfully completed
         */
        void taskCompleted();

        /**
         * Invoked when task completition is unreachable and have to be dropped,
         * when commited conditions cannot be guaranteed, or task cannot be allocated
         */
        void taskUnreachable();

    }
}
