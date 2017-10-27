package cz.agents.alite.common.event;

/**
 * This class is adapter meant to reduce usesless declerations of obsolete code. It implements
 * method {@link EventHandler#getEventProcessor()} which is obsolete and will be removed in Alite 2.
 * It is designed as temporary solution for Alite 1.
 *
 * @author Ondrej Hrstka (ondrej.hrstka at agents.fel.cvut.cz)
 */
public abstract class EventHandlerAdapter implements EventHandler {

    /**
     * This method is obsolete and is not even used. It returns null now.
     * @return null pointer
     */
    @Override
    public EventProcessor getEventProcessor() {
        return null;
    }
}
