package org.jgrapht.listenable.event;

import org.jgrapht.Graph;

public class ListenableWrapperChangeEvent<V, E> {

    private Graph<V, E> wrapper;
    private int type;

    public ListenableWrapperChangeEvent(Graph<V, E> wrapper, int type) {
        this.wrapper = wrapper;
        this.type = type;
    }

    public Graph<V, E> getWrapper() {
        return wrapper;
    }

    public int getType() {
        return type;
    }
}
