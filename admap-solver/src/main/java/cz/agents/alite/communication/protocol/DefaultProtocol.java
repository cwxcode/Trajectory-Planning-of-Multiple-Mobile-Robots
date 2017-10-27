package cz.agents.alite.communication.protocol;

import cz.agents.alite.communication.Communicator;

/**
 * Default protocol implementation providing basic methods.
 *
 * @author Jiri Vokrinek
 */
public abstract class DefaultProtocol implements Protocol {

    private final String name;
    /**
     * The {@link Communicator} used by the protocol.
     */
    protected final Communicator communicator;

    /**
     *
     * @param communicator
     * @param name
     */
    public DefaultProtocol(Communicator communicator, String name) {
        this.communicator = communicator;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    /**
     * Comparator for protocols. The protocol is equal if
     * {@code this.getName().equals(protocol.getName())}
     *
     * @param protocol
     * @return true if protocols are equal
     */
    public boolean equals(Protocol protocol) {
        if (protocol == null) {
            return false;
        }
        if (getName().equals(((Protocol) protocol).getName())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Session ID generator. It is generated as
     * {@code "" + this.hashCode()}
     *
     * @return unique session ID
     */
    protected String generateSession() {
        //todo: is this correct?
        return "" + this.hashCode();
    }
}
