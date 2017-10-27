package org.jgrapht.listenable.event;

import org.jgrapht.Graph;

public class EdgeChangeEvent<V, E> extends ListenableWrapperChangeEvent<V, E> {

    public static final int EDGE_ADDED = 1;
    public static final int EDGE_REMOVED = 2;
    public static final int EDGE_WEIGHT_CHANGED = 3;
    private E edge;
    private V source;
    private V target;

    public EdgeChangeEvent(Graph<V, E> wrapper, E edge, V source, V target, int type) {
        super(wrapper, type);
        this.edge = edge;
        this.source = source;
        this.target = target;
    }

    public E getEdge() {
        return edge;
    }

    public V getSource() {
        return source;
    }

    public V getTarget() {
        return target;
    }
}
