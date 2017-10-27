package org.jgrapht.listenable;

import org.jgrapht.listenable.event.EdgeChangeEvent;
import org.jgrapht.listenable.event.VertexChangeEvent;

public interface ListenableWrapperListener<V, E> {

    public void handleEdgeEvent(EdgeChangeEvent<V, E> event);

    public void handleVertexEvent(VertexChangeEvent<V, E> event);
}
