package tt.jointeuclid2ni.operatordecomposition;

import tt.euclid2i.Line;

public class ODEdge {

    private ODNode source;
    private ODNode target;
    private Line line;

    public ODEdge(ODNode source, ODNode target, Line line) {
        this.source = source;
        this.target = target;
        this.line = line;
    }

    public ODNode getSource() {
        return source;
    }

    public ODNode getTarget() {
        return target;
    }

    public Line getLine() {
        return line;
    }

    public double getWeight() {
        return line.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ODEdge odEdge = (ODEdge) o;

        if (!source.equals(odEdge.source))
            return false;
        if (!target.equals(odEdge.target))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }
}
