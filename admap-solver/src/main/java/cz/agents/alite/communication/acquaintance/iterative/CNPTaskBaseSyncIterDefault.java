package cz.agents.alite.communication.acquaintance.iterative;

import cz.agents.alite.communication.acquaintance.PlanCost;
import cz.agents.alite.communication.acquaintance.Task;
import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.Communicator;

/**
 *
 * @author Jiri Vokrinek
 */
public class CNPTaskBaseSyncIterDefault extends CNPTaskBaseSyncIter{

    public CNPTaskBaseSyncIterDefault(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, name);
    }

    public CNPTaskBaseSyncIterDefault(Communicator communicator, CapabilityRegister directory) {
        super(communicator, directory);
    }

    @Override
    void tryToImprove(Task task, PlanCost estR) {
//         System.out.println(this.toString()+" Improving "+task+" with cost "+estR.toString());
        invokeImprovement(false);
    }

}
