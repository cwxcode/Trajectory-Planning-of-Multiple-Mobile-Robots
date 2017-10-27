package cz.agents.alite.communication.protocol.cnp;

import cz.agents.alite.communication.Communicator;
import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;
import cz.agents.alite.communication.protocol.Performative;
import cz.agents.alite.communication.protocol.ProtocolContent;
import cz.agents.alite.communication.protocol.ProtocolMessageHandler;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * Initiator part of the basic Contract-net-protocol.
 * When created, it registeres protocol handler to the given communicator.
 * In the end of the protocol the handler is automatically derregistered.
 *
 * @author Jiri Vokrinek
 */
//TODO: state machine
public abstract class CnpInitiator extends Cnp {

    private final Object contentData;
    private final Set<String> participantAddress;
    private final HashSet<String> pendingParticipants;
    private final LinkedHashMap<String, Object> responses = new LinkedHashMap<String, Object>();
    private final LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>();
    private final String session;
    private MessageHandler messagehandler;
    private boolean active = false;
    private String winner = null;
    private Object winnerResponse = null;
    private CancelCallback cancelCallback = null;

    /**
     *
     * @param communicator
     * @param name      Custom protocol name to differentiate several CNPs
     * @param contentData
     * @param participantAddress
     */
    public CnpInitiator(Communicator communicator, String name, Object contentData, Set<String> participantAddress) {
        super(communicator, name);
        this.contentData = contentData;
        this.participantAddress = new LinkedHashSet<String>(participantAddress);
        this.session = generateSession();
        this.pendingParticipants = new LinkedHashSet<String>(participantAddress);
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
     * After termination the cancelCallback is invoked.
     *
     * @param cancelCallback
     */
    public void cancel(CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
        ProtocolContent content = new ProtocolContent(this, Performative.CANCEL, null, session);
        Message message = communicator.createMessage(content);
        if (getWinner() != null) {
            message.addReceiver(getWinner());
        } else {
            message.addReceivers(participantAddress);
        }
        communicator.sendMessage(message);
//        active = false;
//        communicator.removeMessageHandler(messagehandler);
    }

    /**
     * Evaluates obtained proposals.
     * After evaluation the ACCEPT message is automatically sent
     * to the winner and REJECT to the others.
     *
     * @param responses Map of <participant address, response>
     * @return sellected winning participant address; null if no proposal is acceptable
     */
    abstract protected String evaluateReplies(LinkedHashMap<String, Object> responses);

    /**
     * Method is called when accepted responder reports FAIL
     * Then the protocol is terminated
     *
     */
    abstract protected void failed();

    /**
     * Method is called when accepted responder reports DONE
     * Then the protocol is terminated
     */
    abstract protected void done();

    /**
     * Method is called after ACCEPT_PROPOSAL is confirmed by the winner
     * @param success true if allocation has been successfull, false otherwise
     */
    abstract protected void allocated(boolean success);

    private void initProtocol() {

        messagehandler = new ProtocolMessageHandler(this, session) {

            @Override
            public void handleMessage(Message message, ProtocolContent content) {
                processMessage(message, content);
            }
        };
        communicator.addMessageHandler(messagehandler);
        ProtocolContent content = new ProtocolContent(this, Performative.CALL_FOR_PROPOSAL, getContentData(), session);
        Message message = communicator.createMessage(content);
        message.addReceivers(participantAddress);
        active = true;
        communicator.sendMessage(message);
    }

    private void processMessage(Message message, ProtocolContent content) {
        switch (content.getPerformative()) {
            case REFUSE:
                pendingParticipants.remove(message.getSender());
                participantAddress.remove(message.getSender());
                checkAnswers();
                break;
            case PROPOSE:
                responses.put(message.getSender(), content.getData());
                messages.put(message.getSender(), message);
                pendingParticipants.remove(message.getSender());
                checkAnswers();
                break;
            case CONFIRM:
                allocated(true);
                break;
            //TODO: potential DISCONFIRM branching
//            case DISCONFIRM:
//                allocated(false);
//                active = false;
//                communicator.removeMessageHandler(messagehandler);
//                break;
            case INFORM:
                active = false;
                communicator.removeMessageHandler(messagehandler);
                if (cancelCallback != null) {
                    cancelCallback.cancelConfirmed();
                }
                break;
            case FAILURE:
                failed();
                active = false;
                communicator.removeMessageHandler(messagehandler);
                break;
            case DONE:
                done();
                active = false;
                communicator.removeMessageHandler(messagehandler);
                break;
            default:
        }
    }

    private void checkAnswers() {
        if (pendingParticipants.isEmpty()) {
            winner = evaluateReplies(responses);
            if (winner != null) {
                winnerResponse = responses.get(winner);
                participantAddress.remove(getWinner());
                Message msg = createReply(getWinner(), Performative.ACCEPT_PROPOSAL);
                communicator.sendMessage(msg);
            } else {
                allocated(false);
            }
            for (String participant : participantAddress) {
                Message msg = createReply(participant, Performative.REJECT_PROPOSAL);
                communicator.sendMessage(msg);
            }
        }
    }

    private Message createReply(String address, Performative performative) {
        return communicator.createReply(messages.get(address), new ProtocolContent(this, performative, null, session));
    }

    /**
     * @return the contentData
     */
    public Object getContentData() {
        return contentData;
    }

    /**
     * @return the winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * @return the winnerResponse
     */
    public Object getWinnerResponse() {
        return winnerResponse;
    }

    /**
     * The callback for handling protocol cancelling.
     */
    public interface CancelCallback {

        /**
         * Method is called after CANCEL is confirmed by the winner
         */
        void cancelConfirmed();
    }
}
