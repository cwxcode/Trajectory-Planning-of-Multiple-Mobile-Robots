package cz.agents.alite.communication.acquaintance;

import cz.agents.alite.communication.acquaintance.Task.TaskListener;
import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator;
import cz.agents.alite.communication.protocol.cnp.DirectoredCnpInitiator;
import cz.agents.alite.communication.protocol.cnp.DirectoredCnpResponder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *  Default agent TaskBase.
 *  It cooperates with any other TaskBase using DirectoredCNP.
 *  TaskBase plans all inserted tasks using CNP allocation with potential self contracting in case of registered task types.
 *  This TaskBase is not synchronized - when several TaskBases invoke tasks concurently the result is not guaranteed.
 *
 *  Corresponds to the R-Fd or R-D-Fd decommitment strategy.
 *  In case of R-D-Fd the TaskBase starts new CNP for task, which is reported unreachable (by the CNPResponder.failed).
 *  In case of R-Fd the task is reported unreachable in case of failure.
 *
 * @author Jiri Vokrinek
 */
public class CNPTaskBase implements TaskBase {

    final protected HashMap<String, PlanBase> planBases = new HashMap<String, PlanBase>();
    // tasks planned by a local PlanBase
    final protected LinkedList<Task> tasksPlanned = new LinkedList<Task>();
    // tasks owned by this TaskBase (inserted by an agent and then delegated by CNP, incl. itself)
    final protected HashMap<Task, CnpInitiator> tasksOwned = new HashMap<Task, CnpInitiator>();
    final protected HashMap<Task, TaskListener> taskListeners = new HashMap<Task, TaskListener>();
    private static int idBase = 1;
    protected final Communicator communicator;
    protected final boolean enableDelegation;
    protected final CapabilityRegister directory;

    /**
     *  Cooperative constructor for agent TaskBase.
     *  Cooperate with any other TaskBase using given communicator and {@link DirectoredCNPInitiator}.
     *
     * @param communicator the communicator used by DiroctoredCNP for cooperation
     * @param directory the directory for registering and searching other agents
     * @param enableDelegation true for R-D-Fd strategy, false for R-Fd strategy
     */
    public CNPTaskBase(Communicator communicator, CapabilityRegister directory, boolean enableDelegation) {
        this.communicator = communicator;
        this.enableDelegation = enableDelegation;
        this.directory = directory;
    }

    /**
     * Invoke a new task processing.
     * TaskBase takes care of the taks planning using registered PlanBases.
     * If there is another TaskBase capable of planning this task, the one with lowest PlanCost is used.
     * The selection is done using DirectoredCNP (included itself).
     *
     * @param task task to be planned
     * @param taskListener completition task listener for task
     * @param allocationCallback allocation listener for task (when allocation fails Task is not stored in the TaskBase)
     * @throws cz.agents.alite.acquaintance.TaskBase.UnknownTaskTypeException
     */
    public void invokeTask(final Task task, final TaskListener taskListener, final AllocationCallback allocationCallback) throws UnknownTaskTypeException {

        if (!isTypeRegistered(task.getTaskType())) {
            // nobody knows it
            throw new UnknownTaskTypeException(task.getTaskType() + " is not known task type");
        }

//        taskListeners.put(task, taskListener);

        new DirectoredCnpInitiatorImpl(communicator, directory, task, taskListener, allocationCallback);
    }

    public boolean cancelTask(Task task, CnpInitiator.CancelCallback cancelFeedback) {
        CnpInitiator cnp = tasksOwned.remove(task);
        if (cnp != null) {
            cnp.cancel(cancelFeedback);
            return true;
        }
        return false;
    }

    public boolean containsTask(Task task) {
        return ((tasksOwned.containsKey(task)) || (tasksPlanned.contains(task)));
    }

    /**
     * Check for task existence localy.
     * In case of decentralized TaskBases the delegated task can be maintained by more than one TaskBase.
     *
     * @param task
     * @return true if the task is maintained by this TaskBase, i.e. planed by local PlanBase
     */
    public boolean containsTaskLocaly(Task task) {
        return tasksPlanned.contains(task);
    }

    public void registerType(String taskType, PlanBase planner) {

        planBases.put(taskType, planner);

        new DirectoredCnpResponderImpl(communicator, directory, taskType);

    }

    public boolean isTypeRegistered(String taskType) {
        return !directory.getIdentities(DirectoredCnpInitiator.buildName(taskType)).isEmpty();
    }

    public String generateNewTaskID() {
        return "" + this.hashCode() + idBase++;
    }

    private class DirectoredCnpInitiatorImpl {

        public DirectoredCnpInitiatorImpl(Communicator communicator, CapabilityRegister directory, final Task task, final TaskListener taskListener, final AllocationCallback allocationCallback) {
            new DirectoredCnpInitiator(communicator, directory, task.getTaskType(), task) {

                @Override
                protected String evaluateReplies(LinkedHashMap<String, Object> responses) {
                    String agentWinner = null;
                    PlanCost costLowest = null;
                    for (Entry<String, Object> proposal : responses.entrySet()) {
                        String agent = proposal.getKey();
                        PlanCost cost = (PlanCost) proposal.getValue();
                        if ((costLowest == null) || (cost.isLower(costLowest))) {
                            costLowest = cost;
                            agentWinner = agent;
                        }
                    }
                    return agentWinner;
                }

                @Override
                protected void failed() {
                    //                Task task = (Task) getContentData();
                    tasksOwned.remove(task);
                    taskListeners.remove(task);
                    if (enableDelegation) {
                        //R-D-Fd
                        try {
                            invokeTask(task, taskListener, new AllocationCallback() {

                                public void allocated() {
                                    //TODO: something successfully delegated
                                }

                                public void failed() {
                                    taskListener.taskUnreachable();
                                }
                            });
                        } catch (UnknownTaskTypeException ex) {
                        }
                    } else {
                        //R-Fd
                        taskListener.taskUnreachable();
                    }
                }

                @Override
                protected void done() {
                    //                Task task = (Task) getContentData();
                    tasksOwned.remove(task);
                    taskListeners.remove(task);
                    taskListener.taskCompleted();
                }

                @Override
                protected void allocated(boolean success) {
                    if (success) {
                        tasksOwned.put(task, this);
                        taskListeners.put(task, taskListener);
                        allocationCallback.allocated();
                    } else {
                        allocationCallback.failed();
                    }
                }
            };
        }
    }

    private class DirectoredCnpResponderImpl extends DirectoredCnpResponder {

        public DirectoredCnpResponderImpl(Communicator communicator, CapabilityRegister directory, String name) {
            super(communicator, directory, name);
        }

        @Override
        protected Object prepareProposal(Object request, String session) {
            Task what = (Task) request;
            PlanBase planner = planBases.get(what.getTaskType());
            if (planner == null) {
                Logger.getLogger(CNPTaskBase.class.getName()).log(Level.ERROR, "REGISTERING TASK TYPE");
                return null;
            }
            return planner.evaluateInsertion(what);
        }

        @Override
        protected void proposalAccepted(final String session) {
            final Task what = (Task) getRequest(session);
            PlanBase planner = planBases.get(what.getTaskType());
            if (planner == null) {
                Logger.getLogger(CNPTaskBase.class.getName()).log(Level.ERROR, "PLANNING TASK TYPE");
            } else {
                if (planner.insertTask(what, new TaskListener() {

                    public void taskCompleted() {
                        tasksPlanned.remove(what);
                        informDone(session);
                    }

                    public void taskUnreachable() {
                        tasksPlanned.remove(what);
                        informFail(session);
                    }
                }) != null) {
                    tasksPlanned.add(what);
                } else {
                    informFail(session);
                }
            }
        }

        @Override
        protected void proposalRejected(String session) {
        }

        @Override
        protected void canceled(String session) {
            if (!isActive(session)) {
            	Logger.getLogger(CNPTaskBase.class.getName()).log(Level.WARN, "CANCELING NON ACTIVE SESSION");
            	return;
            }
        	Task what = (Task) getRequest(session);
            PlanBase planner = planBases.get(what.getTaskType());
            if (planner == null) {
                Logger.getLogger(CNPTaskBase.class.getName()).log(Level.ERROR, "CANCELING TASK TYPE");
            } else {
                planner.removeTask(what);
                tasksPlanned.remove(what);
            }
        }
    }
}
