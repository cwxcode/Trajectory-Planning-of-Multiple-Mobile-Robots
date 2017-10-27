package cz.agents.alite.communication.channel;

import java.util.Set;

/**
 *
 * @author Jiri Vokrinek
 */
public class UnknownReceiversException extends CommunicationChannelException {

    private static final long serialVersionUID = 8896341303688182411L;
    private final Set<String> unknownReceivers;

    /**
     *
     * @param unknownReceivers
     */
    public UnknownReceiversException(Set<String> unknownReceivers) {
        this.unknownReceivers = unknownReceivers;
    }

    /**
     *
     * @return
     */
    public Set<String> getUnknownReceivers() {
        return unknownReceivers;
    }

    @Override
    public String getMessage() {
        return "Unknown receivers in communication channel: " + unknownReceivers;
    }
}
