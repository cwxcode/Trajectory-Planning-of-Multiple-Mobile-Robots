package cz.agents.alite.communication.channel;

/**
 *
 * @author Jiri Vokrinek
 */
public class DuplicateReceiverAddressException extends CommunicationChannelException {

    private static final long serialVersionUID = 1895646481681762411L;
    private final String receiverAddress;

    /**
     *
     * @param address
     */
    public DuplicateReceiverAddressException(String address) {
        this.receiverAddress = address;
    }

    /**
     *
     * @return
     */
    public String getDuplicateAddress() {
        return receiverAddress;
    }

    @Override
    public String getMessage() {
        return "Duplicate receiver address in communication channel: " + receiverAddress;
    }
}
