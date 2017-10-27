package cz.agents.alite.communication.content.binary;

import cz.agents.alite.communication.Message;
import cz.agents.alite.communication.MessageHandler;

/**
 * 
 * {@link MessageHandler} for handling {@link BinaryContent}.
 * 
 * @author stolba
 *
 */
public abstract class BinaryMessageHandler implements MessageHandler {

    @Override
    public void notify(Message message) {
        if (BinaryContent.class.equals(message.getContent().getClass())) {
        	BinaryContent content = (BinaryContent) message.getContent();
            handleMessage(message, content);
        }
    }

    /**
     * Handler for binary messages.
     *
     * @param message
     * @param content
     */
    abstract public void handleMessage(Message message, BinaryContent content);
}
