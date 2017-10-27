package cz.agents.alite.communication.acquaintance;

import java.util.List;

/**
 * The encapsulating interface for Plan representation.
 * Enrich the API of your implementation for capturing the specifics of your PlanBase and PlanExecutor.
 *
 * @author Jiri Vokrinek
 */
public interface Plan extends List<Task> {

    public Plan getReadOnly();
}
