package cz.agents.alite.communication.protocol.query;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.protocol.Performative;

/**
 * The general Query protocol. For each receiver instance the method {@link handleQuery(Object query)}
 * is called when initiator requests a query.
 *
 * @author Jiri Vokrinek
 * @author Antonin Komenda
 */
public abstract class QueryResponder extends Query {

    private final MessageHandler messagehandler;
    final String entityAddress;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public QueryResponder(final Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, name);
        this.entityAddress = communicator.getAddress();
        directory.register(entityAddress, getName());
        messagehandler = new ProtocolMessageHandler(this) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(message, content);
            }
        };
        communicator.addMessageHandler(messagehandler);
    }

    private void processMessage(Message message, ProtocolContent content) {
        String session = content.getSession();
        Object body = content.getData();
        switch (content.getPerformative()) {
            case QUERY:
                Object answer = handleQuery(body);
                Message msg = createReply(message, Performative.INFORM, answer, session);
                communicator.sendMessage(msg);
                break;
            default:
        }
    }

    private Message createReply(Message message, Performative performative, Object body, String session) {
        return communicator.createReply(message, new ProtocolContent(this, performative, body, session));
    }

    /**
     * This methods is called if some other agent sends Query.
     *
     * @param query the data of the query send by the initiator
     * @return the queried object to be returned to the initiator
     */
    abstract protected Object handleQuery(Object query);
}
