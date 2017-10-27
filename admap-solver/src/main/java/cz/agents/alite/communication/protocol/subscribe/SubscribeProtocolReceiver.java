package cz.agents.alite.communication.protocol.subscribe;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import cz.agents.alite.common.capability.CapabilityRegister;

/**
 * The general subscription protocol. For each subscribe instance the method {@link handleInform(Object inform)}
 * is called on all subscribers except the invoker.
 *
 * @author Jiri Vokrinek
 */
public abstract class SubscribeProtocolReceiver extends SubscribeProtocolSender {

    private final MessageHandler messagehandler;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public SubscribeProtocolReceiver(final Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, directory, name);
        directory.register(agentName, getName());
        messagehandler = new ProtocolMessageHandler(this) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(content);
            }
        };
        communicator.addMessageHandler(messagehandler);
    }

    private void processMessage(ProtocolContent content) {
//        String session = content.getSession();
        Object body = content.getData();
        switch (content.getPerformative()) {
            case INFORM:
                handleInform(body);
                break;
            default:
        }
    }

    /**
     * This methods is called if some other agent sends the subscribed inform to this protocol.
     *
     * @param inform
     */
    abstract protected void handleInform(Object inform);
}
