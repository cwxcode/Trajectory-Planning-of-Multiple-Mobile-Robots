package cz.agents.alite.communication.acquaintance.iterative;

import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.acquaintance.CNPTaskBase;
import cz.agents.alite.communication.acquaintance.PlanCost;
import cz.agents.alite.communication.acquaintance.Task;
import cz.agents.alite.communication.acquaintance.Task.TaskListener;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator.CancelCallback;
import cz.agents.alite.communication.protocol.request.RequestInformInitiator;
import cz.agents.alite.communication.protocol.request.RequestInformResponder;
import cz.agents.alite.communication.protocol.tokenring.MasteredTokenRing;
import cz.agents.alite.communication.protocol.tokenring.TokenRing.TokenProcessCallback;

/**
 *  Default agent TaskBase with iterative improvement and synchronization..
 *  It cooperates with any other TaskBase using DirectoredCNP.
 *  TaskBase plans all inserted tasks using CNP allocation with potential self contracting in case of registered task types.
 *  This TaskBase is synchronized. The task insertion and improvement are synchronized using {@see MasteredTokenRing}.
 *
 *  Corresponds to the R-Fd or R-D-Fd decommitment strategy.
 *  In case of R-D-Fd the TaskBase starts new CNP for task, which is reported unreachable (by the CNPResponder.failed).
 *  In case of R-Fd the task is reported unreachable in case of failure.
 *
 * @author Jiri Vokrinek
 */
public abstract class CNPTaskBaseSyncIter extends CNPTaskBase {

    private final MasteredTokenRing synchroRing;
    private static final String DEFAULT_RING_NAME = "DEFAULT_RING_NAME";
    private static final String ESTIMATE_REMOVAL_REQUEST_NAME = "ESTIMATE_REMOVAL_REQUEST_NAME";
    private static final String TOKEN_KEEP_IMPROVING = "TOKEN_KEEP_IMPROVING";
    private final LinkedList<TaskCallbackStorer> taskToAdd = new LinkedList<TaskCallbackStorer>();
    private final LinkedList<Task> taskToImprove = new LinkedList<Task>();
    private boolean keepImproving = false;
    private TokenProcessCallback tokenCallback = null;

    public CNPTaskBaseSyncIter(Communicator communicator, CapabilityRegister directory) {
        this(communicator, directory, DEFAULT_RING_NAME);
    }

    public CNPTaskBaseSyncIter(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, true);

        synchroRing = new MasteredTokenRing(communicator, directory, name) {

            @Override
            protected void handleToken(Object token, TokenProcessCallback callback) {
                tokenCallback = callback;
                invokeNextTask();
            }
        };

        new RequestInformResponder(communicator, ESTIMATE_REMOVAL_REQUEST_NAME) {

            @Override
            protected void handleRequest(Object request, String session) {
                Task task = (Task) request;
                inform(session, planBases.get(task.getTaskType()).evaluateRemoval(task));
            }
        };
    }

    protected void invokeImprovement(boolean changesHappen) {
        if (changesHappen) {
            keepImproving = true;
        }
        if (taskToImprove.isEmpty()) {
            if (keepImproving) {
                fireNewToken();
            }
            tokenCallback.processingDone();
        } else {
            final Task task = taskToImprove.removeFirst();
            // try to improve
            improve(task);
        }
    }

    private void improve(final Task task) {

        new RequestInformInitiator(communicator, ESTIMATE_REMOVAL_REQUEST_NAME, task, tasksOwned.get(task).getWinner()) {

            @Override
            protected void processInform(Object inform, String session) {
                final PlanCost estR = (PlanCost) inform;
                System.out.println(this.toString() + " Improving " + task + " with cost " + estR.toString());
                tryToImprove(task, estR);
            }
        };
    }

    /*
     * Method for task improvement.
     * Must call {@link invokeImprovement(boolean changesHappen)} after task improvement finish
     * to continue the process.
     */
    abstract void tryToImprove(Task task, PlanCost estR);

    private void invokeNextTask() {
        if (taskToAdd.isEmpty()) {
            //all allocated, let's improve
//            fireNewToken();
            taskToImprove.addAll(tasksOwned.keySet());
            invokeImprovement(false);
        } else {
            final TaskCallbackStorer taskEntry = taskToAdd.removeFirst();
            keepImproving = true;

            invokeTaskDirect(taskEntry.task, taskEntry.taskListener, new AllocationCallback() {

                public void allocated() {
                    taskEntry.allocationCallback.allocated();
                    invokeNextTask();
                }

                public void failed() {
                    taskEntry.allocationCallback.failed();
                    invokeNextTask();
                }
            });
        }
    }

    protected void invokeTaskDirect(Task task, TaskListener taskListener, AllocationCallback allocationCallback) {
        try {
            super.invokeTask(task, taskListener, allocationCallback);
        } catch (UnknownTaskTypeException ex) {
            Logger.getLogger(CNPTaskBaseSyncIterRA.class.getName()).log(Level.ERROR, null, ex);
        }
    }

    @Override
    public void invokeTask(Task task, TaskListener taskListener, AllocationCallback allocationCallback) throws UnknownTaskTypeException {
        if (!isTypeRegistered(task.getTaskType())) {
            // nobody knows it
            throw new UnknownTaskTypeException(task.getTaskType() + " is not known task type");
        }
        taskToAdd.add(new TaskCallbackStorer(task, taskListener, allocationCallback));
        fireNewToken();
    }

    @Override
    public boolean cancelTask(Task task, CancelCallback cancelFeedback) {
        boolean cancelTask = super.cancelTask(task, cancelFeedback);
        fireNewToken();
        return cancelTask;
    }

    private void fireNewToken() {
        keepImproving = false;
        synchroRing.invokeToken(TOKEN_KEEP_IMPROVING);
    }

    class TaskCallbackStorer {

        Task task;
        TaskListener taskListener;
        AllocationCallback allocationCallback;

        public TaskCallbackStorer(Task task, TaskListener taskListener, AllocationCallback allocationCallback) {
            this.task = task;
            this.taskListener = new TaskListenerWrapper(taskListener);
            this.allocationCallback = allocationCallback;
        }
    }

    class TaskListenerWrapper implements TaskListener {

        final TaskListener listener;

        public TaskListenerWrapper(TaskListener listener) {
            this.listener = listener;
        }

        public void taskCompleted() {
            fireNewToken();
            listener.taskCompleted();
        }

        public void taskUnreachable() {
            fireNewToken();
            listener.taskUnreachable();
        }
    }
}
