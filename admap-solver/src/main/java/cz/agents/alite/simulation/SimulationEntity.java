package cz.agents.alite.simulation;

import cz.agents.alite.common.entity.Entity;
import cz.agents.alite.common.event.EventHandler;
import cz.agents.alite.environment.Action;
import cz.agents.alite.environment.Environment;
import cz.agents.alite.environment.Sensor;
import cz.agents.alite.environment.Storage;

/**
 * Simulation entity represents an object directly interacting in
 * the {@link Simulation}.
 *
 * Also, it is an default implementation of the {@link EventHandler} interface usable
 * together with the {@link Simulation}.
 *
 * Simulation entities can be aggregated in a environment {@link Storage}, each
 * representing one element behaving in the scope of the storage (e.g. a traffic light in
 * a traffic light storage).
 *
 * Note: An agents is not an extension of a {@link SimulationEntity}, since
 * the agent do not represent the physical body. The agent is only a "brain"
 * controlling its body through the {@link Sensor}s and {@link Action}s of an
 * {@link Environment}.
 *
 *
 * @author Antonin Komenda
 */
public abstract class SimulationEntity extends Entity implements EventHandler {

    private Simulation simulation;

    public SimulationEntity(String name, Simulation simulation) {
        super(name);

        this.simulation = simulation;
    }

    @Override
    public Simulation getEventProcessor() {
        return simulation;
    }

}
