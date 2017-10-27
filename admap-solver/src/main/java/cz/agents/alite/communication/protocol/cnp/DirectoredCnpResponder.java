package cz.agents.alite.communication.protocol.cnp;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.cnp.CnpResponder;
import cz.agents.alite.common.capability.CapabilityRegister;

/**
 *
 * @author Jiri Vokrinek
 */
public abstract class DirectoredCnpResponder extends CnpResponder {

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public DirectoredCnpResponder(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, name);
        directory.register(communicator.getAddress(), getName());
    }
}
