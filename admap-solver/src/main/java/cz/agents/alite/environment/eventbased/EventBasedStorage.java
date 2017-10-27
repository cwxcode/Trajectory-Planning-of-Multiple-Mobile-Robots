package cz.agents.alite.environment.eventbased;

import cz.agents.alite.common.event.Event;
import cz.agents.alite.common.event.EventHandler;
import cz.agents.alite.common.event.EventProcessor;
import cz.agents.alite.environment.Storage;

/**
 * An EventBasedStorage can use the event processor to implement a spontaneous
 * changes of the held data.
 *
 * An example can be a time ticker storage. The storage would repeatedly add an
 * event into the event processor and for each handling of the event would
 * increase a time tick counter in it.
 *
 *
 * @author Antonin Komenda
 */
public class EventBasedStorage extends Storage implements EventHandler {

    private final EventBasedEnvironment environment;

    public EventBasedStorage(EventBasedEnvironment environment) {
        this.environment = environment;
    }

    protected EventBasedEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public EventProcessor getEventProcessor() {
        return environment.getEventProcessor();
    }

    @Override
    public void handleEvent(Event event) {
    }

}
