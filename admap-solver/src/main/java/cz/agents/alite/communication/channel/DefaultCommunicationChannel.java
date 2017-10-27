package cz.agents.alite.communication.channel;

import cz.agents.alite.communication.CommunicationReceiver;
import cz.agents.alite.communication.Message;

/**
 *
 * @author Jiri Vokrinek
 */
public abstract class DefaultCommunicationChannel implements CommunicationChannel {

    private final CommunicationReceiver communicationReceiver;

    /**
     *
     * @param communicator
     */
    public DefaultCommunicationChannel(CommunicationReceiver communicator) {
        this.communicationReceiver = communicator;
    }

    @Override
    public abstract void sendMessage(Message message) throws CommunicationChannelException;

    @Override
    public void receiveMessage(Message message) {
        communicationReceiver.receiveMessage(message);
    }

    protected CommunicationReceiver getCommunicationReceiver() {
        return communicationReceiver;
    }
}
