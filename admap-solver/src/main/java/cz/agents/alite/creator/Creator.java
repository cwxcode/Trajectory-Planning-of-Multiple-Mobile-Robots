package cz.agents.alite.creator;

import cz.agents.alite.simulation.Simulation;

/**
 * A Creator encapsulates code that creates the application universe and runs
 * the play in it.
 *
 * The universe is a data composition of various elements describing the
 * existing stuff in the application.
 * The particular elements in the universe and their interactions are not
 * strictly pre-defined, so that they can be arbitrarily designed
 * for each application and its needs.
 *
 * The typical elements of the universe are: {@link Environment},
 * {@link Simulation}, agents, communication infrastructure, and similar.
 *
 *
 * @author Antonin Komenda
 */
public interface Creator {

    public void init(String[] args);

    public void create();

}
