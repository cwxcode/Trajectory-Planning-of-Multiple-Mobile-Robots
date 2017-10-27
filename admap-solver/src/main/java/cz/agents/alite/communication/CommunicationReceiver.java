package cz.agents.alite.communication;

/**
 *
 * @author Jiri Vokrinek
 */
public interface CommunicationReceiver {

    /**
     * Receives a message obtained by the communication.
     * Note: implementation of this method should be thread-safe.
     *     
     * @param message
     */
    void receiveMessage(Message message);

    /**
     * Gets communication address.
     * @return
     */
    String getAddress();
}
