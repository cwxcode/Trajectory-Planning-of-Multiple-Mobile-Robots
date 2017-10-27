package cz.agents.alite.communication.acquaintance;

import cz.agents.alite.communication.acquaintance.Task.TaskListener;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator.CancelCallback;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *  Default agent TaskBase.
 *  Does not cooperate with any other TaskBase.
 *
 * @author Jiri Vokrinek
 */
public class DefaultTaskBase implements TaskBase {

    final private HashMap<String, PlanBase> planBases = new HashMap<String, PlanBase>();
    // tasks planned by a local PlanBase
    final private LinkedList<Task> tasks = new LinkedList<Task>();
    // delegated tasks inserted localy to this TaskBase
    final private HashMap<Task, TaskListener> taskListeners = new HashMap<Task, TaskListener>();
    private static int idBase = 1;

    /**
     * Invoke a new task processing localy.
     * TaskBase takes care of the taks planning using registered PlanBases.
     *
     * @param task task to be planned
     * @param taskListener completition task listener for task
     * @throws cz.agents.alite.acquaintance.TaskBase.UnknownTaskTypeException
     */
    public void invokeTask(final Task task, final TaskListener taskListener, final AllocationCallback allocationCallback) throws UnknownTaskTypeException {
        PlanBase planner = planBases.get(task.getTaskType());
        if (planner == null) {
            throw new UnknownTaskTypeException(task.getTaskType() + " is not registered task type");
        }
        if (planner.insertTask(task, new TaskListener() {

            public void taskCompleted() {
                tasks.remove(task);
                taskListeners.remove(task).taskCompleted();
            }

            public void taskUnreachable() {
                tasks.remove(task);
                taskListeners.remove(task).taskUnreachable();
            }
        }) != null) {
            tasks.add(task);
            taskListeners.put(task, taskListener);
            allocationCallback.allocated();
        } else {
            allocationCallback.failed();
        }
    }

    public boolean cancelTask(Task task, CancelCallback cancelCallback) {
        if (tasks.remove(task)) {
            planBases.get(task.getTaskType()).removeTask(task);
            taskListeners.remove(task);
            cancelCallback.cancelConfirmed();
            return true;
        }
        return false;
    }

    public boolean containsTask(Task task) {
        return tasks.contains(task);
    }

    public void registerType(String taskType, PlanBase planner) {
        planBases.put(taskType, planner);
    }

    public boolean isTypeRegistered(String taskType) {
        return planBases.containsKey(taskType);
    }

    public String generateNewTaskID() {
        return "" + this.hashCode() + idBase++;
    }

}
