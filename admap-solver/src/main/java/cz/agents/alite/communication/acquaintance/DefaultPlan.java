package cz.agents.alite.communication.acquaintance;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Basic Plan implementation.
 * Only encaptulates LinkedList<Task>.
 *
 * @author Jiri Vokrinek
 */
public class DefaultPlan extends LinkedList<Task> implements Plan {

    private static final long serialVersionUID = -5734327377766938273L;

    @Override
    public DefaultPlan getReadOnly() {
        DefaultPlan dp = new DefaultPlan();
        dp.addAll(Collections.unmodifiableList(this));
        return dp;
    }

}
