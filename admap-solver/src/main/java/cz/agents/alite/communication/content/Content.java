package cz.agents.alite.communication.content;

import java.io.Serializable;

/**
 * Basic content wrapper for the messaging.
 *
 * @author Jiri Vokrinek
 */
public class Content implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3523260995414320200L;
	private final Object data;

    /**
     *
     * @param data the content data
     */
    public Content(Object data) {
        this.data = data;
    }

    /**
     * Returns the content data.
     *
     * @return the content data
     */
    public Object getData() {
        return data;
    }
    
    public String toString(){
    	if(data == null){
    		return "";
    	}else{
    		return data.toString();
    	}
    }
}
