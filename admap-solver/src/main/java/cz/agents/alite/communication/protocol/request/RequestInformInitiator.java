package cz.agents.alite.communication.protocol.request;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;

/**
 *
 * Initiator part of the basic Request-inform protocol.
 * When created, it registeres protocol handler to the given communicator.
 * In the end of the protocol the handler is automatically derregistered.
 *
 * @author Jiri Vokrinek
 */
public abstract class RequestInformInitiator extends RequestInform {

    private final Object contentData;
    private final String participantAddres;
    private final String session;
    private MessageHandler messagehandler;
    private boolean active = false;

    /**
     *
     * @param communicator
     * @param name      Custom protocol name to differentiate several Request-informs
     * @param contentData
     * @param participantAddres
     */
    public RequestInformInitiator(Communicator communicator, String name, Object contentData, String participantAddres) {
        super(communicator, name);
        this.contentData = contentData;
        this.participantAddres = participantAddres;
        this.session = generateSession();
        initProtocol();
    }

    /**
     * Check of the protocol activity.
     *
     * @return true if the protocol is running; false when finnished.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Cancels the protocol and terminates it.
     * The inform will not be processed.
     */
    public void cancel() {
        active = false;
        //TODO: is this safe?
        communicator.removeMessageHandler(messagehandler);
    }

    /**
     * Processes obtained inform.
     * The protocol is automaticaly terminated.
     *
     * @param inform
     * @param session 
     */
    abstract protected void processInform(Object inform, String session);

    private void initProtocol() {

        messagehandler = new ProtocolMessageHandler(this, session) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(message, content);
            }
        };
        communicator.addMessageHandler(messagehandler);
        ProtocolContent content = new ProtocolContent(this, Performative.REQUEST, getContentData(), session);
        Message message = communicator.createMessage(content);
        message.addReceiver(participantAddres);
        active = true;
        communicator.sendMessage(message);
    }

    private void processMessage(Message message, ProtocolContent content) {
        switch (content.getPerformative()) {
            case INFORM:
                active = false;
                communicator.removeMessageHandler(messagehandler);
                processInform(content.getData(), session);
                break;
            default:
        }
    }

    /**
     * @return the contentData
     */
    public Object getContentData() {
        return contentData;
    }
}
