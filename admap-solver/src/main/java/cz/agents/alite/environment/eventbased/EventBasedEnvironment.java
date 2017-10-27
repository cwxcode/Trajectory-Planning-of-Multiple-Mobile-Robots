package cz.agents.alite.environment.eventbased;

import cz.agents.alite.common.entity.Entity;
import cz.agents.alite.common.event.EventProcessor;
import cz.agents.alite.environment.Action;
import cz.agents.alite.environment.Environment;
import cz.agents.alite.environment.Sensor;


/**
 * EventBasedEnvironments provides additionally a event processor, which can
 * be used to describe successive the processes in the environment.
 *
 * The event processor is typically used in {@link EventBasedAction}s and
 * {@link EventBasedSensor}s of the environment.
 *
 *
 * @author Antonin Komenda
 */
public abstract class EventBasedEnvironment extends Environment {

    private final EventProcessor eventProcessor;
    private final EventBasedHandler handler;

    public EventBasedEnvironment(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;

        handler = new EventBasedHandler();
    }

    public EventProcessor getEventProcessor() {
        return eventProcessor;
    }

    public EventBasedEnvironment.EventBasedHandler handler() {
        return handler;
    }

    public class EventBasedHandler extends Handler {

        protected EventBasedHandler() {
        }

        @Override
        public <C extends Action> C addAction(Class<C> clazz, Entity entity) {
            return instantiateEnvironmentClass(clazz, entity, new Class<?>[] {EventBasedEnvironment.class, Entity.class});
        }

        @Override
        public <C extends Sensor> C addSensor(Class<C> clazz, Entity entity) {
            return instantiateEnvironmentClass(clazz, entity, new Class<?>[] {EventBasedEnvironment.class, Entity.class});
        }

    }

}
