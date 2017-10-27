package cz.agents.alite.communication;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import cz.agents.alite.communication.channel.CommunicationPerformerChannel;

/**
 * Used to implement passive communication - in order to receive any messages 
 * via a MessageHandler, the performReceive() method must be invoked. 
 * The messages (if any) are delivered via registered callbacks using the same thread which called 
 * the performReceive() method. The call is non-blocking.
 * 
 * @author stolba
 *
 */
public class DefaultPerformerCommunicator extends DefaultCommunicator implements PerformerCommunicator {

	private static final Logger LOGGER = Logger.getLogger(DefaultPerformerCommunicator.class);

	private final List<CommunicationPerformerChannel> performerChannels = new LinkedList<CommunicationPerformerChannel>();
	
	public DefaultPerformerCommunicator(String address) {
		super(address);
	}

	@Override
	public boolean performReceiveNonblock() {
		boolean received=false;
		for(CommunicationPerformerChannel channel : performerChannels){
			received = received || channel.performReceiveNonblock();
		}
		return received;
	}
	
	@Override
	public void performReceiveBlock(long timeout) {
		for(CommunicationPerformerChannel channel : performerChannels){
			channel.performReceiveBlock(timeout);
		}
	}
	
	/**
	 * Add a passive channel.
	 * WARNING: The performReceive() method must be called in order to receive any messages!
	 * @param channel
	 */
	public void addPerformerChannel(CommunicationPerformerChannel channel) {
		performerChannels.add(channel);
		addChannel(channel);
    }
	
	@Override
    public void addMessageHandler(MessageHandler handler) {
		if(!performerChannels.isEmpty()){
			LOGGER.info("The performReceive() method must be (periodically) called in order to receive any messages!");
		}
		super.addMessageHandler(handler);
	}

	@Override
	public void performClose() {
		for(CommunicationPerformerChannel channel : performerChannels){
			channel.performClose();
		}
	}

	

}
