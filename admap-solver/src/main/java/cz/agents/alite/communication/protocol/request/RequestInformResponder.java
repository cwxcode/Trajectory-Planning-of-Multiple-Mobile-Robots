package cz.agents.alite.communication.protocol.request;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import java.util.LinkedHashMap;

/**
 *
 * Responder part of the basic Request-inform.
 * When created, it registeres protocol handler to the given communicator.
 * The RequestInformInitiator maintains all the active requests of the given type.
 * To deactivate the protocol, call the appropriate method.
 *
 * @author Jiri Vokrinek
 */
public abstract class RequestInformResponder extends RequestInform {

    private final LinkedHashMap<String, Message> requests = new LinkedHashMap<String, Message>();
    private MessageHandler messagehandler;

    /**
     *
     * @param communicator
     * @param name      Custom protocol name to differentiate several RequestInforms
     */
    public RequestInformResponder(Communicator communicator, String name) {
        super(communicator, name);
        initProtocol();
    }

    /**
     * Prepare inform for the given request identified by the session.
     * When the inform is prepared call {@link inform(String session, Object inform)} method
     *
     * @param request
     * @param session
     */
    abstract protected void handleRequest(Object request, String session);

    /**
     * Invokes the inform part of the protocol. Does nothing if session is not active.
     *
     * @param session
     * @param inform
     */
    public void inform(String session, Object inform) {
        Message message = requests.remove(session);
        if (message != null) {
            communicator.sendMessage(createReply(message, Performative.INFORM, inform, session));
        }
    }

    /**
     * Check of the protocol activity.
     *
     * @param session
     * @return true if the protocol session is running; false when finnished.
     */
    public boolean isActive(String session) {
        return requests.containsKey(session);
    }

    /**
     * Searches for request
     *
     * @param session
     * @return request of corresponding session, null if request is not active
     */
    public Object getRequest(String session) {
        return requests.get(session).getContent().getData();

    }

    private void initProtocol() {

        messagehandler = new ProtocolMessageHandler(this) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(message, content);
            }
        };
        communicator.addMessageHandler(messagehandler);
    }

    private void processMessage(Message message, ProtocolContent content) {
        String session = content.getSession();
        Object body = content.getData();
        switch (content.getPerformative()) {
            case REQUEST:
                requests.put(session, message);
                handleRequest(body, session);
                break;
            default:
        }
    }

    private Message createReply(Message message, Performative performative, Object body, String session) {
        return communicator.createReply(message, new ProtocolContent(this, performative, body, session));
    }
}
