package cz.agents.alite.communication.protocol.cnp;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.cnp.CnpInitiator;
import cz.agents.alite.common.capability.CapabilityRegister;
import java.util.Set;

/**
 *
 * @author Jiri Vokrinek
 */
public abstract class DirectoredCnpInitiator extends CnpInitiator {

    private final CapabilityRegister directory;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     * @param contentData
     * @param participantAddress
     */
    public DirectoredCnpInitiator(Communicator communicator, CapabilityRegister directory, String name, Object contentData, Set<String> participantAddress) {
        super(communicator, name, contentData, participantAddress);
        this.directory = directory;
    }

    /**
     *
     * @param communicator
     * @param directory
     * @param name      Custom protocol name to differentiate several CNPs
     * @param contentData
     */
    public DirectoredCnpInitiator(Communicator communicator, CapabilityRegister directory, String name, Object contentData) {
        this(communicator, directory, name, contentData, directory.getIdentities(buildName(name)));
    }

    /**
     *
     * @param name
     * @return Set of addresses registered to serviceType that corresponds to protocol name
     */
    public Set<String> getCorrespondingAddresses(String name) {
        return directory.getIdentities(buildName(name));
    }

    public static String buildName(String name) {
        return CnpInitiator.buildName(name);
    }
}
