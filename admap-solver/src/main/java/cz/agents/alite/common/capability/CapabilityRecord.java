package cz.agents.alite.common.capability;

import java.io.Serializable;

class CapabilityRecord implements Serializable{
	
	private static final long serialVersionUID = -6082625766262200377L;
	
	public final String identity;
	public final String capabilityName;
	
	public CapabilityRecord(String identity, String capabilityName){
		this.identity = identity;
		this.capabilityName = capabilityName;
	}
}