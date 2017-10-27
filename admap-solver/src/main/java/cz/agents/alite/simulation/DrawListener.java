package cz.agents.alite.simulation;

/**
 * Interface for all synchronized drawing objects in simulation.
 * 
 * @author Ondrej Milenovsky
 * */
public interface DrawListener {
    /**
     * Method called in new thread, limited by deadline. After the deatline the simulation
     * starts and any reading of shared object can cause errors.
     * 
     * @return true if drawed everything in until deadline or not known
     */
    public boolean drawFrame(long deadline);
    
    public String getName();
}
