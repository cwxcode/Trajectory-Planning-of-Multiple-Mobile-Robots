package cz.agents.alite.communication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cz.agents.alite.communication.channel.CommunicationChannel;
import cz.agents.alite.communication.channel.CommunicationChannelException;
import cz.agents.alite.communication.content.Content;
import cz.agents.alite.communication.content.error.ErrorContent;

/**
 *
 * @author Jiri Vokrinek
 */
public class DefaultCommunicator implements Communicator {

    private final String address;
    private final List<CommunicationChannel> channels = new LinkedList<CommunicationChannel>();
    private final List<MessageHandler> messageHandlers = new CopyOnWriteArrayList<MessageHandler>();

    private static long counter = System.currentTimeMillis();

    /**
     *
     * @param address
     */
    public DefaultCommunicator(String address) {
        this.address = address;
    }

    /**
     * Adds communication channel to the communicator.
     *
     * @param channel
     */
    public void addChannel(CommunicationChannel channel) {
        channels.add(channel);
    }


    @Override
    public String getAddress() {
        return address;
    }


    @Override
    public Message createMessage(Content content) {
        return new Message(address, content, generateId());
    }


    @Override
    public Message createReply(Message message, Content content) {
        Message reply = new Message(address, content, generateId());
        reply.addReceiver(message.getSender());
        return reply;
    }


    @Override
    public void addMessageHandler(MessageHandler handler) {
        messageHandlers.add(handler);
    }


    @Override
    public void removeMessageHandler(MessageHandler handler) {
        messageHandlers.remove(handler);
    }


    @Override
    public void sendMessage(Message message) {
        for (CommunicationChannel channel : channels) {
            try {
                channel.sendMessage(message);
            } catch (CommunicationChannelException e) {
                Message errorMessage = createMessage(new ErrorContent(e));
                errorMessage.addReceiver(getAddress());
                receiveMessage(errorMessage);
            }
        }
    };


    @Override
    public synchronized void receiveMessage(Message message) {
        for (MessageHandler messageHandler : messageHandlers) {
            messageHandler.notify(message);
        }
    }

    private long generateId() {
        return address.hashCode() + counter;
    }
    
    @Override
    public String toString() {
    	return channels.toString();
    }
}
