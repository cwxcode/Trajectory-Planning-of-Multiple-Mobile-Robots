package cz.agents.alite.communication.protocol.simple;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.DefaultProtocol;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import java.util.Collection;

/**
 * This is basic protocol for message passing. It allows direct communication between agents.
 * It creates {@link ProtocolMessageHandler} bounded to the name provided and allows
 * to send messages to such handlers.
 *
 * @author Jiri Vokrinek
 */
public abstract class SimpleProtocol extends DefaultProtocol {

    static final String SIMPLE_PROTOCOL_NAME = "SIMPLE_PROTOCOL";
    private final MessageHandler messagehandler;

    /**
     *
     * @param communicator
     * @param name unique identification of protocol instance
     */
    public SimpleProtocol(Communicator communicator, String name) {
        super(communicator, SIMPLE_PROTOCOL_NAME + ": " + name);
        messagehandler = new ProtocolMessageHandler(this) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(content.getData());
            }
        };
        communicator.addMessageHandler(messagehandler);

    }

    /**
     * The method called when new message arrives.
     *
     * @param content message content received.
     */
    abstract protected void processMessage(Object content);

    /**
     * Sends a message to a receiver.
     *
     * @param content the content to be sent
     * @param receiver address of the receiver
     */
    public void sendMessage(Object content, String receiver) {
        ProtocolContent pC = new ProtocolContent(this, Performative.PROPAGATE, content, generateSession());
        Message message = communicator.createMessage(pC);
        message.addReceiver(receiver);
        communicator.sendMessage(message);
    }

    /**
     * Sends a message to multiple receivers.
     *
     * @param content the content to be sent
     * @param receivers addresses of the receivers
     */
    public void sendMessage(Object content, Collection<String> receivers) {
        ProtocolContent pC = new ProtocolContent(this, Performative.PROPAGATE, content, generateSession());
        Message message = communicator.createMessage(pC);
        message.addReceivers(receivers);
        communicator.sendMessage(message);
    }
}
