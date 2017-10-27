package cz.agents.alite.environment.eventbased;

import cz.agents.alite.common.entity.Entity;
import cz.agents.alite.common.event.Event;
import cz.agents.alite.common.event.EventHandler;
import cz.agents.alite.common.event.EventProcessor;
import cz.agents.alite.environment.Sensor;

/**
 * The EventBasedSensors can use the event processor to postpone getting of the
 * sensory data.
 *
 * A sensor, which needs a time to process the data, can be modeled as
 * an event-based sensor. The sense() methods of the sensor would not return
 * directly the requested data, bud only adds an event into the event processor.
 * Later, the event would be received by the same instance of the sensor and
 * would invoke a sensory callback, which will asynchronously return the data
 * into the requesting entity/agent.
 *
 *
 * @author Antonin Komenda
 */
public abstract class EventBasedSensor extends Sensor implements EventHandler {

    private final EventBasedEnvironment environment;

    public EventBasedSensor(EventBasedEnvironment environment, Entity relatedEntity) {
        super(environment, relatedEntity);

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
