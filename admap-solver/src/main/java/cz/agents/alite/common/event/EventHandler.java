package cz.agents.alite.common.event;


/**
 * An EventHandler is an object that can be affected (can receive) events.
 *
 * @author Antonin Komenda
 */
public interface EventHandler {

    public EventProcessor getEventProcessor();

    public void handleEvent(Event event);

}
