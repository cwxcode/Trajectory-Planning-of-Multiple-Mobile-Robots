package cz.agents.alite.communication;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import cz.agents.alite.communication.content.Content;

public final class Message implements Serializable {

    private static final long serialVersionUID = 5903646003802722160L;

    private final long id;
    private final String sender;
    private final LinkedList<String> receivers = new LinkedList<String>();
    private final Content content;

    Message(String sender, Content content, long id) {

        if (content == null) {
            throw new IllegalArgumentException("null Content is not permitted");
        }

        this.sender = sender;
        this.content = content;
        this.id = id;
    }

    /**
     * Get the value of receiver
     *
     * @return the value of receiver
     */
    public Collection<String> getReceivers() {
        return receivers;
    }

    /**
     * Set the value of receiver
     *
     * @param newReceivers
     */
    public void addReceivers(Collection<String> newReceivers) {
        receivers.addAll(newReceivers);
    }

    /**
     *
     * @param receiver
     */
    public void addReceiver(String receiver) {
        receivers.add(receiver);
    }

    /**
     *
     * @return
     */
    public Content getContent() {
        return content;
    }

    /**
     *
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * Get the value of senderID
     *
     * @return the value of senderID
     */
    public String getSender() {
        return sender;
    }

    public String toString(){
        return "[Sender: " + sender +"\n" +
                "Receivers: " + receivers + "\n" +
                "Content: " + content + "]";
    }
}
