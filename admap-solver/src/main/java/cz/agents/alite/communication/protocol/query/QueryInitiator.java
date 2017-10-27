package cz.agents.alite.communication.protocol.query;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.common.capability.CapabilityRegister;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The general Query protocol. For each responder instance the method {@link sendQuery(Object query)}
 * is called on all subscribers except the initiator.
 *
 * @author Jiri Vokrinek
 * @author Antonin Komenda
 */
public abstract class QueryInitiator extends Query {

    final String entityAddress;
    private final Object query;
    private final Set<String> responderAddresses;
    private final Set<String> pendingResponders;
    private final String session;
    private final Set<Object> answers = new HashSet<Object>();

    private MessageHandler messagehandler;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     * @param query
     */
    public QueryInitiator(Communicator communicator, CapabilityRegister directory, String name, Object query) {
        super(communicator, name);
        this.entityAddress = communicator.getAddress();
        this.query = query;
        this.session = generateSession();
        Set<String> addresses = directory.getIdentities(getName());
        responderAddresses = new LinkedHashSet<String>(addresses);
        responderAddresses.remove(entityAddress);
        this.pendingResponders = new LinkedHashSet<String>(responderAddresses);
        initProtocol();
    }

    private void initProtocol() {

        messagehandler = new ProtocolMessageHandler(this, session) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(message, content);
            }
        };
        communicator.addMessageHandler(messagehandler);
        ProtocolContent content = new ProtocolContent(this, Performative.QUERY, query, session);
        Message message = communicator.createMessage(content);
        message.addReceivers(responderAddresses);
        communicator.sendMessage(message);
    }

    private void processMessage(Message message, ProtocolContent content) {
        switch (content.getPerformative()) {
            case INFORM:
                pendingResponders.remove(message.getSender());
                answers.add(content.getData());
                checkAnswers();
                break;
            default:
        }
    }

    private void checkAnswers() {
        if (pendingResponders.isEmpty()) {
            evaluateReplies(answers);
            communicator.removeMessageHandler(messagehandler);
        }
    }

    /**
     * Evaluates obtained replied queries.
     * @param answers a set of the answered queries from the responders
     */
    abstract protected void evaluateReplies(Set<Object> answers);
}
