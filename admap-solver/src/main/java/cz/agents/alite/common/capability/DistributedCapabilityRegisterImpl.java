package cz.agents.alite.common.capability;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.channel.CommunicationChannelBroadcast;
import cz.agents.alite.communication.content.Content;
import cz.agents.alite.communication.content.error.ErrorContent;

/**
 * Provides basic distributed implementation of @CapabilityRegister using @HashMap 
 * and broadcast of registered capabilities
 * 
 * @author Jiri Vokrinek
 * @author Michal Stolba
 */
public class DistributedCapabilityRegisterImpl implements CapabilityRegister, MessageHandler {

    final HashMap<String, Set<String>> localRegister = new HashMap<String, Set<String>>();
    final HashMap<String, Set<String>> remoteRegister = new HashMap<String, Set<String>>();
    
    private final Communicator comm;
    
    /**
     * 
     * @param communicator
     */
    public DistributedCapabilityRegisterImpl(Communicator communicator){
    	comm = communicator;
    	comm.addMessageHandler(this);
    	
    	//broadcast request to others
        Message m = comm.createMessage(new Content(new CapabilityRequest()));
        m.addReceiver(CommunicationChannelBroadcast.BROADCAST_ADDRESS);
        comm.sendMessage(m);
    }

    public void register(String identity, String capabilityName) {
    	register(localRegister,identity, capabilityName);
        
    	//broadcast to others
        Message m = comm.createMessage(new Content(new CapabilityRecord(identity,capabilityName)));
        m.addReceiver(CommunicationChannelBroadcast.BROADCAST_ADDRESS);
        comm.sendMessage(m);
    }
    
    
    
    private void register(HashMap<String, Set<String>> register,String identity, String capabilityName) {
    	System.out.println("Register " + identity + " - " + capabilityName);
        Set<String> recS = register.get(capabilityName);
        if (recS == null) {
            LinkedHashSet<String> page = new LinkedHashSet<String>();
            page.add(identity);
            register.put(capabilityName, page);
        } else {
            recS.add(identity);
        }
        System.out.println(" local:  " + localRegister);
        System.out.println(" remote: " + remoteRegister);
    }
    

    public Set<String> getIdentities(String capabilityName) {
        Set<String> getLocal = localRegister.get(capabilityName);
        Set<String> getRemote = remoteRegister.get(capabilityName);
        
        if (getLocal == null) {
        	getLocal = new LinkedHashSet<String>();
        }
        
        if (getRemote == null) {
        	getRemote = new LinkedHashSet<String>();
        }
        
        Set<String> get = new LinkedHashSet<String>();
        
        get.addAll(getLocal);
        get.addAll(getRemote);
        
        return new LinkedHashSet<String>(get);

    }

    public Set<String> getIdentities() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (Set<String> page : localRegister.values()) {
            result.addAll(page);
        }
        for (Set<String> page : remoteRegister.values()) {
            result.addAll(page);
        }
        return result;
    }
    
    public void broadcastLocalRegister(){
    	sendLocalRegister(CommunicationChannelBroadcast.BROADCAST_ADDRESS);
    }

    private void sendLocalRegister(String receiver){
    	Message m = comm.createMessage(new Content(new CapabilityReply(localRegister)));
        m.addReceiver(receiver);
        comm.sendMessage(m);
    }
    
    private void receiveRemoteRegister(CapabilityReply reply){
    	Map<String, Set<String>> receivedRegister = reply.getData();
    	
    	for(String key : receivedRegister.keySet()){
    		if(!remoteRegister.containsKey(key)){
    			remoteRegister.put(key, new LinkedHashSet<String>());
    		}
    		for(String s : receivedRegister.get(key)){
    			remoteRegister.get(key).add(s);
    		}
    	}
    }

	@Override
	public void notify(Message message) {
		if(message.getContent().getData() instanceof CapabilityRecord  && !message.getSender().equals(comm.getAddress())){
			
			CapabilityRecord cr = (CapabilityRecord)message.getContent().getData();
			register(remoteRegister,cr.identity, cr.capabilityName);
			
		}else if(message.getContent().getData() instanceof CapabilityRequest && !message.getSender().equals(comm.getAddress())){
			
			sendLocalRegister(message.getSender());
			
		}else if(message.getContent().getData() instanceof CapabilityReply){
			
			receiveRemoteRegister((CapabilityReply)message.getContent().getData());
			
		}else if(message.getContent() instanceof ErrorContent){
			
			System.err.println(message.getContent());
			
		}
	}
}
