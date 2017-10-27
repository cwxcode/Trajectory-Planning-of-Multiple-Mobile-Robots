package cz.agents.alite.communication.acquaintance.iterative;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.acquaintance.PlanCost;
import cz.agents.alite.communication.acquaintance.Task;
import cz.agents.alite.communication.acquaintance.Task.TaskListener;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator;
import cz.agents.alite.communication.protocol.cnp.DirectoredCnpInitiator;

/**
 * DelegateAll dynamic improvement strategy for {@link CNPTaskBaseSyncIter}.
 *
 * @author Jiri Vokrinek
 */
public class CNPTaskBaseSyncIterDA extends CNPTaskBaseSyncIter {

    public CNPTaskBaseSyncIterDA(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, name);
    }

    public CNPTaskBaseSyncIterDA(Communicator communicator, CapabilityRegister directory) {
        super(communicator, directory);
    }

    @Override
    void tryToImprove(final Task task, final PlanCost estR) {

        final CnpInitiator cnp = tasksOwned.get(task);
        final String lastResource = cnp.getWinner();
        final TaskListener taskListener = taskListeners.get(task);

        Set<String> addresses = directory.getIdentities(DirectoredCnpInitiator.buildName(task.getTaskType()));
        addresses.remove(lastResource);
        new DirectoredCnpInitiator(communicator, directory, task.getTaskType(), task, addresses) {

            @Override
            protected String evaluateReplies(LinkedHashMap<String, Object> responses) {
                String agentWinner = null;
                PlanCost costLowest = estR;
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
                                //somehow delegated
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
                    // improved by delegation
                    tasksOwned.put(task, this);
//                    taskListeners.put(task, taskListener);
                    invokeImprovement(true);
                    final CnpInitiator delegCNP = this;
                    cnp.cancel(new CancelCallback() {

                        public void cancelConfirmed() {
                            tasksOwned.put(task, delegCNP);
                            invokeImprovement(true);
                        }
                    });
                } else {
                    // not improved by delegation, nothing happens
                    invokeImprovement(false);
                }
            }
        };
    }
}
