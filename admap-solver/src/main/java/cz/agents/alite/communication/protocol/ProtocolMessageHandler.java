package cz.agents.alite.communication.protocol;

import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;

/**
 * The message handler for protocol-based messaging.
 *
 * @author Jiri Vokrinek
 */
public abstract class ProtocolMessageHandler implements MessageHandler {

    private final Protocol protocol;
    private final Performative performative;
    private final String session;

    /**
     *
     * @param protocol
     * @param performative
     * @param session
     */
    public ProtocolMessageHandler(Protocol protocol, Performative performative, String session) {
        if (protocol == null) {
            throw new IllegalArgumentException("null Protocol is not permitted");
        }
        this.protocol = protocol;
        this.performative = performative;
        this.session = session;
    }

    /**
     *
     * @param protocol
     * @param session
     */
    public ProtocolMessageHandler(Protocol protocol, String session) {
        this(protocol, null, session);
    }

    /**
     *
     * @param protocol
     */
    public ProtocolMessageHandler(Protocol protocol) {
        this(protocol, null, null);
    }


    @Override
    public void notify(Message message) {
        if (ProtocolContent.class.equals(message.getContent().getClass())) {
            ProtocolContent content = (ProtocolContent) message.getContent();
            if (content.getProtocolName().equals(protocol.getName())) {
                if (performative == null || content.getPerformative().equals(performative)) {
                    if (session == null || content.getSession().equals(session)) {
                        handleMessage(message, content);
                    }
                }
            }
        }
    }

    /**
     * Handler for protocol content processing.
     *
     * @param message
     * @param content
     */
    abstract public void handleMessage(Message message, ProtocolContent content);
}
