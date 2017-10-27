package cz.agents.alite.communication.protocol.subscribe;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.common.capability.CapabilityRegister;
import java.util.Set;

/**
 * The general subscription protocol. For each subscribe instance the method {@link handleInform(Object inform)}
 * is called on all subscribers except the invoker.
 *
 * {@link SubscribeProtocolSender} allows only information sending. It does not make the subscription.
 *
 * @author Jiri Vokrinek
 */
public abstract class SubscribeProtocolSender extends SubscribeProtocol {

    final String agentName;
    private final CapabilityRegister directory;

    /**
     *
     * @param communicator
     * @param directory
     * @param name
     */
    public SubscribeProtocolSender(Communicator communicator, CapabilityRegister directory, String name) {
        super(communicator, name);
        this.directory = directory;
        this.agentName = communicator.getAddress();
    }

    /**
     * Sends a message to subscrabers of this protocol.
     *
     * @param inform a content object to be sent.
     */
    public void sendInform(Object inform) {
        ProtocolContent content = new ProtocolContent(this, Performative.INFORM, inform, generateSession());
        Message message = communicator.createMessage(content);
        Set<String> addresses = directory.getIdentities(getName());
        addresses.remove(agentName);
        message.addReceivers(addresses);
        communicator.sendMessage(message);
    }
}
