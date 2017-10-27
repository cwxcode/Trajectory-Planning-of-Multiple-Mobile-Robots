package cz.agents.alite.common.entity;

/**
 * An Entity is a named object.
 *
 *
 * @author Antonin Komenda
 * @author Ondrej Milenovsky
 */
public class Entity {

    private final String name;

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
