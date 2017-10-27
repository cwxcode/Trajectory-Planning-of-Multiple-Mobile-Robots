package cz.agents.alite.simulation;

import cz.agents.alite.common.event.Event;
import cz.agents.alite.common.event.EventHandlerAdapter;
import cz.agents.alite.common.event.EventProcessor;
import cz.agents.alite.environment.Sensor;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * The simulation is a wrapper of the {@link EventProcessor} with an additional
 * functionality covering adjustable simulation speed.
 *
 * An agent should never get a reference to the simulation. The simulation
 * should be directly used only by other services of the universe (environment,
 * communication, etc). The agent is controlled by the simulation only
 * indirectly: by {@link Sensor} callbacks, message receiving, and so on.
 *
 *
 * @author Antonin Komenda
 */
public class Simulation extends EventProcessor {

    private Logger LOGGER = Logger.getLogger(Simulation.class);

    private double simulationSpeed = 0;
    private long eventCount = 0;
    private long runTime;
    private int printouts = 10000;

    /** drawing frame requested */
    private boolean drawFrame = false;
    private DrawListener drawListener;
    /** timeout for draw listener */
    private long drawTimeout = 1000;
    /** min delay between drawings */
    private long drawReload = 40;
    /** last time drawed */
    private long lastDrawed = 0;

    private boolean turnOnEventStepSimulation = false;
    private long durationOfOneEventStep = 1000;
    private boolean waitingOnInterruption = false;

    private final List<EventListener> eventListeners = new LinkedList<EventListener>();

    private long sleepTimeIfWaitToOtherEvent = 0 ;
    private boolean sleepTimeFlag = false; // reason for using flag is back compatibility

    /**
     * Through this constructor is possible to set duration of simulation.
     *
     * @param simulationEndTime
     */
    public Simulation(long simulationEndTime) {
        addEvent(new FinishSimulationEventHandler(this), simulationEndTime);
    }

    /**
     * If you create instance of Simulation through this constructor, that
     * simulation run to infinity.
     */
    public Simulation() {
    }

    public void run() {
        runTime = System.currentTimeMillis();
        LOGGER.info(">>> SIMULATION START");

        fireEvent(SimulationEventType.SIMULATION_STARTED, null, null, null);
        super.run();
        fireEvent(SimulationEventType.SIMULATION_FINISHED, null, null, null);

        LOGGER.info(">>> SIMULATION FINISH");
        LOGGER.info(String.format(">>> END TIME: %dms", getCurrentTime()));
        LOGGER.info(String.format(">>> EVENT COUNT: %d", eventCount));
        LOGGER.info(String.format(">>> RUNTIME: %.2fs", (System.currentTimeMillis() - runTime) / 1000.0));

    }

    public long getEventCount() {
        return eventCount;
    }

    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    /** sets listener for synchronized drawing vis frames between events */
    public void setDrawListener(DrawListener listener) {
        drawListener = listener;
    }

    public DrawListener getDrawListener() {
        return drawListener;
    }

    /**
     * request drawing frame, if not running, returns false and not draws it !
     * if running, draws after some time
     */
    public boolean requestDraw() {
        if (!isRunning()) {
            return false;
        } else {
            drawFrame = true;
            return true;
        }
    }

    /**
     * sets draw timeout, 0 means timeout is given by simulaiton speed
     */
    public void setDrawTimeout(long timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be >= 0");
        }
        this.drawTimeout = timeout;
    }

    /**
     * Sets sleep time for jumping time between two event, with long time gap
     * @param sleepTimeIfWaitToOtherEvent
     */
    public void setSleepTimeIfWaitToOtherEvent(long sleepTimeIfWaitToOtherEvent) {
        this.sleepTimeIfWaitToOtherEvent = sleepTimeIfWaitToOtherEvent;
        this.sleepTimeFlag = true;
    }

    /**
     * sets min interval between drawings, time for simulation to reload for new
     * drawing
     */
    public void setDrawReload(long drawReload) {
        if (drawReload < 0) {
            throw new IllegalArgumentException("Reload time must be >= 0");
        }
        this.drawReload = drawReload;
    }

    /** 0 means maximum */
    public void setSimulationSpeed(final double simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }

    public double getSimulationSpeed() {
        return simulationSpeed;
    }

    /** print every n-th event */
    public void setPrintouts(int n) {
        this.printouts = n;
    }

    /**
     * Turn on firing next event after some sleep time, which is represented by durationOfOneEventStep.
     */
    public void turnOnEventStepSimulation(){
        turnOnEventStepSimulation = true;
    }

    /**
     * Turn off firing next event after some sleep time, which is represented by durationOfOneEventStep.
     */
    public void turnOffEventStepSimulation(){
        turnOnEventStepSimulation = false;
    }

    public void setDurationOfOneEventStep(long durationOfOneEventStep){
        this.durationOfOneEventStep = durationOfOneEventStep;
    }

    /**
     * Turn on waiting simulation on next event to calling method interruptionWaitingOnNextEvent
     * or waiting time expires Long.MAX_VALUE
     *
     */
    public void turnOnWaitingOnNextEventToInterruption(){
        waitingOnInterruption = true; // FUTURE: use state pattern
        setRunning(false);

    }

    public void turnOffWaitingOnNextEventToInterruption(){
        waitingOnInterruption = false; // FUTURE: use state pattern
        setRunning(true);
    }

    public void interruptionWaitingOnNextEvent(){
        if(waitingOnInterruption){
            setRunning(true);
        }
    }

    @Override
    protected void breforeRunningTest(Event event) {
        ++eventCount;
        if (eventCount % printouts == 0) {
            LOGGER.debug(String.format(
                            ">>> SIM. TIME: %ds / RUNTIME: %.2fs / EVENTS: %d / QUEUE: %d",
                            getCurrentTime() / 1000, (System
                                    .currentTimeMillis() - runTime) / 1000.0,
                            eventCount, getCurrentQueueLength()));
        }


        if(waitingOnInterruption){
            setRunning(false);
        }

        long timeToSleep = (long) ((event.getTime() - getCurrentTime()) * simulationSpeed);
        callEventListeners();
        // draws frame for vis
        if (drawFrame && (drawListener != null)
                && (System.currentTimeMillis() > lastDrawed + drawReload)) {
            timeToSleep = drawFrame(timeToSleep);
        }

        if(sleepTimeFlag){
            if(timeToSleep > sleepTimeIfWaitToOtherEvent){
                timeToSleep = sleepTimeIfWaitToOtherEvent;
            }else{
                timeToSleep = 0;
            }

        }

        if(turnOnEventStepSimulation){
            timeToSleep = durationOfOneEventStep;
        }

        if ((simulationSpeed > 0) && (timeToSleep > 0)) {
            try {
                Thread.sleep(timeToSleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventProcessor.class.getName()).log(Level.ERROR, null, ex);
            }
        }
    }

    private void callEventListeners() {
        for (EventListener listener : eventListeners) {
            listener.eventFired();
        }
    }

    private long drawFrame(long timeToSleep) {
        drawFrame = false;
        long startTime = System.currentTimeMillis();
        final long drawDeadline = System.currentTimeMillis()
                + Math.max(timeToSleep, drawTimeout);
        // start drawing thread
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                drawListener.drawFrame(drawDeadline);
            }
        });
        thread2.start();
        // wait
        try {
            long time = Math.max(drawDeadline - System.currentTimeMillis(), 1);
            thread2.join(time);
        } catch (InterruptedException e) {
            Logger.getLogger(EventProcessor.class.getName()).log(Level.INFO,
                    null, e);
        }
        lastDrawed = System.currentTimeMillis();
        // recompute event delay
        return timeToSleep + startTime - System.currentTimeMillis();
    }

    public static class FinishSimulationEventHandler extends EventHandlerAdapter{

        private final EventProcessor simulation;

        public FinishSimulationEventHandler(EventProcessor simulation) {
            super();
            this.simulation = simulation;
        }

        public void handleEvent(Event arg0) {
            // If simulation Queue will b
            simulation.clearQueue();

        }
    }

}
