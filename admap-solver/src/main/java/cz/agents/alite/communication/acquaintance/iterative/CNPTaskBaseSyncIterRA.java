package cz.agents.alite.communication.acquaintance.iterative;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.acquaintance.CNPTaskBase;
import cz.agents.alite.communication.acquaintance.PlanCost;
import cz.agents.alite.communication.acquaintance.Task;
import cz.agents.alite.communication.acquaintance.Task.TaskListener;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator.CancelCallback;

/**
 *  ReallocateAll dynamic improvement strategy for {@link CNPTaskBaseSyncIter}.
 *
 * @author Jiri Vokrinek
 */
public class CNPTaskBaseSyncIterRA extends CNPTaskBaseSyncIter {

    public CNPTaskBaseSyncIterRA(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, name);
    }

    public CNPTaskBaseSyncIterRA(Communicator communicator, CapabilityRegister directory) {
        super(communicator, directory);
    }

    @Override
    void tryToImprove(final Task task, final PlanCost estR) {

        final CnpInitiator cnp = tasksOwned.remove(task);
        final String lastResource = cnp.getWinner();
        final TaskListener taskListener = taskListeners.remove(task);


        cnp.cancel(new CancelCallback() {

            public void cancelConfirmed() {
                invokeTaskDirect(task, taskListener, new AllocationCallback() {

                    boolean improved = false;

                    public void allocated() {
                        if (estR.isLower((PlanCost) tasksOwned.get(task).getWinnerResponse())) {
                            Logger.getLogger(CNPTaskBase.class.getName()).log(Level.ERROR, "REALLOCATION");
                        }
                        if (!lastResource.equals(tasksOwned.get(task).getWinner())) {
                            //improved by delegation
                            improved = true;
                        } else if (((PlanCost) tasksOwned.get(task).getWinnerResponse()).isLower(estR)) {
                            //improved by replanning
                            improved = true;
                        }
                        invokeImprovement(improved);
                    }

                    public void failed() {
                        taskListener.taskUnreachable();
                        invokeImprovement(true);
                    }
                });
            }
        });
    }

    @Override
    protected void invokeTaskDirect(Task task, TaskListener taskListener, AllocationCallback allocationCallback) {
        super.invokeTaskDirect(task, taskListener, allocationCallback);
    }
}
