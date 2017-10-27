package cz.agents.alite.communication.channel;

import cz.agents.alite.communication.CommunicationReceiver;

/**
 *
 * @author Jiri Vokrinek
 */
public abstract class DefaultCommunicationChannelBroadcast extends DefaultCommunicationChannel implements CommunicationChannelBroadcast {

    /**
     *
     * @param communicator
     */
    public DefaultCommunicationChannelBroadcast(CommunicationReceiver communicator) throws DuplicateReceiverAddressException {
        super(communicator);
        if (BROADCAST_ADDRESS.equals(communicator.getAddress())) {
            throw new DuplicateReceiverAddressException(communicator.getAddress());
        }

    }
}
