package cz.agents.alite.communication.protocol.query;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.protocol.DefaultProtocol;

/**
 * Querry protocol wrapper.
 *
 * @author Jiri Vokrinek
 * @author Antonin Komenda
 */
public class Query extends DefaultProtocol {

    static final String QUERY_PROTOCOL_NAME = "QUERY_PROTOCOL";

    /**
     *
     * @param communicator
     * @param name
     */
    public Query(Communicator communicator, String name) {
        super(communicator, QUERY_PROTOCOL_NAME + ": " + name);
    }
}
