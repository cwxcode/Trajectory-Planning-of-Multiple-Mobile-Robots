package cz.agents.alite.communication.protocol;

import cz.agents.alite.communication.content.Content;

/**
 * The wrapper for protocol content for messaging.
 * It encaptulates the protocol, performative and session ID.
 *
 * @author Jiri Vokrinek
 */
public class ProtocolContent extends Content {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4365846092504434052L;
	private final String protocolName;
    private final Performative performative;
    private final String session;

    /**
     *
     * @param protocol
     * @param performative
     * @param data
     * @param session
     */
    public ProtocolContent(Protocol protocol, Performative performative, Object data, String session) {
        super(data);

        if (protocol == null || performative == null || session == null) {
            throw new IllegalArgumentException("null is not permitted");
        }

        this.protocolName = protocol.getName();
        this.performative = performative;
        this.session = session;

    }

//    public ProtocolContent(Protocol protocol, Object body, String session) {
//        this(protocol, null, body, session);
//    }
//
//    public ProtocolContent(Performative performative, Object body, String session) {
//        this(null, performative, body, session);
//    }

    /**
     * Get the value of performative
     *
     * @return the value of performative
     */
    public Performative getPerformative() {
        return performative;
    }

    /**
     * Get the value of protocol
     *
     * @return the value of protocol
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     *
     * @return
     */
    public String getSession() {
        return session;
    }
    
    public String toString(){
    	return "[protocol:"+protocolName+", performative:"+performative+", session:"+session+", data:"+super.toString()+"]";
    }
    /*
     *
     * performative


    Type of communicative acts

    sender


    Participant in communication

    receiver


    Participant in communication

    reply-to


    Participant in communication

    content


    Content of message

    language


    Description of Content

    encoding


    Description of Content

    ontology


    Description of Content

    protocol


    Control of conversation

    conversation-id


    Control of conversation

    reply-with


    Control of conversation

    in-reply-to


    Control of conversation

    reply-by


    Control of conversation
     *
     */
}
