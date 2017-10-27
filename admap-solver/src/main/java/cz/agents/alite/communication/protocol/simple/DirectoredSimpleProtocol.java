package cz.agents.alite.communication.protocol.simple;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.simple.SimpleProtocol;
import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.directory.DirectoryFacilitatorSingleton;
import java.util.Set;

/**
 *
 * @author Jiri Vokrinek
 */
public abstract class DirectoredSimpleProtocol extends SimpleProtocol {

    private final CapabilityRegister directory;

    /**
     * Default constructor. It registers this protocol in the {@link DirectoryFacilitatorSingleton}
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public DirectoredSimpleProtocol(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, name);
        this.directory = directory;
        directory.register(communicator.getAddress(), getName());
    }

    /**
     * Searches the {@link DirectoryFacilitatorSingleton} for the agents that instantiated the same
     * {@link DirectoredSimpleProtocol} name.
     *
     * @return set of agents addresses
     */
    public Set<String> getNeighbourAdressess() {
        return directory.getIdentities(getName());
    }
}
