package tt.planner.homotopy;

public class HEdge<V, E> {

    private E edge;
    private HNode<V> source;
    private HNode<V> target;

    HEdge(E edge, HNode<V> source, HNode<V> target) {
        this.edge = edge;
        this.source = source;
        this.target = target;
    }

    public E getEdge() {
        return edge;
    }

    public HNode<V> getSource() {
        return source;
    }

    public HNode<V> getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HEdge hEdge = (HEdge) o;

        if (edge != null ? !edge.equals(hEdge.edge) : hEdge.edge != null) return false;
        if (source != null ? !source.equals(hEdge.source) : hEdge.source != null) return false;
        if (target != null ? !target.equals(hEdge.target) : hEdge.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return edge != null ? edge.hashCode() : 0;
    }
}
