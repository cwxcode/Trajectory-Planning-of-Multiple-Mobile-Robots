package org.jgrapht.listenable.event;

import org.jgrapht.Graph;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class VertexChangeEvent<V, E> extends ListenableWrapperChangeEvent<V, E> {

    public static final int VERTEX_ADDED = 1;
    public static final int VERTEX_REMOVED = 2;
    private V vertex;

    public VertexChangeEvent(Graph<V, E> wrapper, V vertex, int type) {
        super(wrapper, type);
        this.vertex = vertex;
    }

    public V getVertex() {
        return vertex;
    }
}
