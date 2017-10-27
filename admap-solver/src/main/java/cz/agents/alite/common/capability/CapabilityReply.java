package cz.agents.alite.common.capability;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

class CapabilityReply implements Serializable{
	
	private static final long serialVersionUID = 7203583301689529748L;

	private static final String SET_END = "|";
	
	private final String[] data;
	
	/**
	 * Encode
	 * @param register
	 */
	public CapabilityReply(Map<String, Set<String>> register){
		LinkedList<String> dataList = new LinkedList<String>();
		
		for(String key : register.keySet()){
			dataList.add(key);
			dataList.addAll(register.get(key));
			dataList.add(SET_END);
		}
		
		data = dataList.toArray(new String[0]);
	}
	
	/**
	 * Decode
	 * @return
	 */
	public Map<String, Set<String>> getData(){
		Map<String, Set<String>> register = new HashMap<String, Set<String>>();
		
		String key = null;
		
		for(String s : data){
			if(key == null){
				key = s;
				register.put(key, new LinkedHashSet<String>());
			}else{
				if(s.equals(SET_END)){
					key = null;
				}else{
					register.get(key).add(s);
				}
			}
		}
		
		return register;
	}
}