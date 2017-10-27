package tt.euclidtime3i.sipp;

import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;

import java.util.List;

public class SippEdge {

    private SippNode start, end;
    private List<Straight> lines;
    private int weight;

    public SippEdge(SippNode start, SippNode end, List<Straight> lines) {
        this.start = start;
        this.end = end;
        this.lines = lines;
        this.weight = Trajectories.duration(lines);
    }

    public SippNode getSource() {
        return start;
    }

    public SippNode getTarget() {
        return end;
    }

    public List<Straight> getLines() {
        return lines;
    }

    public int weight() {
        return weight;
    }

    public Trajectory trajectory() {
        return new BasicSegmentedTrajectory(lines, weight, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SippEdge sippEdge = (SippEdge) o;

        if (end != null ? !end.equals(sippEdge.end) : sippEdge.end != null) return false;
        if (lines != null ? !lines.equals(sippEdge.lines) : sippEdge.lines != null) return false;
        if (start != null ? !start.equals(sippEdge.start) : sippEdge.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (lines != null ? lines.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("SippEdge{start=%s, end=%s, lines=%s, weight=%d}", start, end, lines, weight);
    }
}
