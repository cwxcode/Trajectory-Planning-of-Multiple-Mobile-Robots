package cz.agents.alite.common.event;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The event processor is a wrapper for an event-queue cycle.
 *
 * On the fly, the processor lines up events added into it, and "sends" them to their particular
 * recipients in a particular order. By sending, it is meant to call a {@link EventHandler}
 * .handleEvent() method, with the event as an argument. An event handler needs to be registered
 * into the processor to receive the events.
 *
 * The event is sent to all event handlers registered with the EventProcessor, if its recipient is
 * <code>null</code> and it is sent to the particular event handler otherwise.
 *
 * The event-queue cycle can be paused/un-paused by the setRunning() method. By the default, the
 * cycle is started running.
 *
 * The time of currently processed events can be obtained by the getCurrentTime() method.
 *
 *
 * @author Antonin Komenda
 */
public class EventProcessor {

    private volatile boolean running = true;
    private volatile boolean finished = false;

    /**
     * "Current" simulation time (timestamp of the last event received).
     */
    private volatile long currentTime = 0;

    private long eventIdCounter = 0;
    private final Thread thread = Thread.currentThread();
    private final Queue<Event> eventQueue = new PriorityQueue<Event>();
    private final List<EventHandler> entityList = new CopyOnWriteArrayList<EventHandler>();

    public void run() {
        Event event = eventQueue.poll();

        while (event != null) {
            if (event.getTime() < 0) {
                throw new RuntimeException("Event time is negative");
            }

            breforeRunningTest(event);

            currentTime = event.getTime();

            while (!running) {
                synchronized (thread) {
                    try {
                        if (!running) {
                            thread.wait();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EventProcessor.class.getName()).log(Level.ERROR, null, ex);
                    }
                }
            }

            fireEvent(event);

            event = eventQueue.poll();
        }

        finished = true;
        running = false;
    }

    /**
     * Ends the the event processor by clearing the event queue.
     *
     * This method has to be called from the same thread as the run() method was called!
     */
    public void clearQueue() {
        eventQueue.clear();
    }

    /**
     * Add an immediate event into the queue of the event processor.
     *
     * This method has to be called from the same thread as the run() method was called!
     *
     * @param type
     *            the type of the event (see {@link EventType})
     * @param recipient
     *            the target of the event (use <code>null</code> for all registered event handlers)
     * @param owner
     *            a "deprecated" version of the recipient
     * @param content
     *            the payload of the event (by the user specified data)
     */
    public void addEvent(EventType type, EventHandler recipient, String owner, Object content) {
        addEvent(type, recipient, owner, content, 1);
    }

    /**
     * Add an event into the queue of the event processor.
     *
     * This method has to be called from the same thread as the run() method was called!
     *
     * @param type
     *            the type of the event (see {@link EventType})
     * @param recipient
     *            the target of the event (use <code>null</code> for all registered event handlers)
     * @param owner
     *            a "deprecated" version of the recipient
     * @param content
     *            the payload of the event (by the user specified data)
     * @param deltaTime
     *            the duration (in milliseconds) from now till the time when the event should take
     *            place (be send to its recipients)
     */
    public void addEvent(EventType type, EventHandler recipient, String owner, Object content,
            long deltaTime) {
        // TODO: refactorize the recipients/owners/senders/groups and similar

        if (deltaTime < 1) {
            throw new IllegalArgumentException("Negative or zero deltaTime is illegal");
        }

        Event event = new Event(eventIdCounter++, currentTime + deltaTime, type, recipient, owner, content);
        if (event.getTime() < 0) {
            throw new RuntimeException("Event with negative currentTime generated (wraparound?)");
        }
        eventQueue.add(event);
    }

    /**
     * Add an immediate event into the queue of the event processor.
     * {@ EventHandler} represents specific implementation, which handles only
     * one event.
     *
     * This method has to be called from the same thread as the run() method was called!
     *
     * @param eventHandler - through its callback method is informed about end of event.
     */
    public void addEvent(EventHandler eventHandler) {
        addEvent(eventHandler, 1);
    }

    /**
     * Add an event into the queue of the event processor.
     * {@ EventHandler} represents specific implementation, which handles only
     * one event.
     *
     * This method has to be called from the same thread as the run() method was called!
     *
     * @param eventHandler - through its callback method is informed about end of event.
     *
     * @param deltaTime the duration (in milliseconds) from now till the time when the event should take
     *            place (be send to its recipients)
     */
    public void addEvent(EventHandler eventHandler, long deltaTime) {
        if (deltaTime < 1) {
            throw new IllegalArgumentException("Negative or zero deltaTime is illegal");
        }

        Event event = new Event(eventIdCounter++, currentTime + deltaTime, null, eventHandler, null, null);
        eventQueue.add(event);
    }

    /**
     * Adds an EventHandler to list of all event handlers of this processor. All added handlers,
     * by this method, are used for handling of events without a recipient.
     *
     * The method can be called from other threads (than the run() method was called).
     *
     * @param eventHandler
     */
    public void addEventHandler(EventHandler eventHandler) {
        entityList.add(eventHandler);
    }

    /**
     * Method pauses and un-pauses the processing of the events.
     *
     * The method can be called from other threads (than the run() method was called).
     *
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
        if (running) {
            synchronized (thread) {
                thread.notify();
            }
        }
    }

    /**
     * The method can be called from other threads (than the run() method was called).
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * The method can be called from other threads (than the run() method was called).
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * The method can be called from other threads (than the run() method was called).
     */
    public long getCurrentTime() {
        return currentTime;
    }

    /**
     * The method can be called from other threads (than the run() method was called).
     */
    public int getCurrentQueueLength() {
        return eventQueue.size();
    }

    protected void breforeRunningTest(Event event) {
    }

    protected void fireEvent(EventType type, EventHandler recipient, String owner, Object content) {
        fireEvent(new Event(eventIdCounter++, currentTime, type, recipient, owner, content));
    }

    private void fireEvent(Event event) {
        if (event.getRecipient() != null) {
            event.getRecipient().handleEvent(event);
        } else {
            for (EventHandler entity : entityList) {
                entity.handleEvent(event);
            }
            if (event.isType(EventProcessorEventType.STOP)) {
                eventQueue.clear();
            }
        }
    }

}
