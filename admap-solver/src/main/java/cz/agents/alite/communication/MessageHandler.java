package cz.agents.alite.communication;


/**
 *
 * @author Jiri Vokrinek
 */
public interface MessageHandler {

    /**
     * Called when message handler receives a message.
     *
     * @param message
     */
    void notify(Message message);
}
