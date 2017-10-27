package cz.agents.alite.communication.protocol.cnp;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.DefaultProtocol;

/**
 * Contract-net-protocol wrapper
 * 
 * @author Jiri Vokrinek
 */
public class Cnp extends DefaultProtocol {

    static final String CNP_PROTOCOL_NAME = "CONTRACT_NET_PROTOCOL";

    /**
     *
     * @param communicator
     * @param name
     */
    public Cnp(Communicator communicator, String name) {
        super(communicator, buildName(name));
    }

    /**
     * Builds a unique name of the protocol in the form
     * {@code CNP_PROTOCOL_NAME + ": " + name;}
     *
     * @param name
     * @return the name of the protocol
     */
    protected static String buildName(String name) {
        return CNP_PROTOCOL_NAME + ": " + name;
    }
}
