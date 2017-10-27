package tt.euclidtime3i.sipprrts;

import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.Trajectories;

import java.util.List;

/**
 * Created by Vojtech Letal on 2/25/14.
 */
public class SippRRTEdge {

    private final SippRRTNode from;
    private final SippRRTNode to;
    private final List<Straight> straights;
    private int duration;

    public SippRRTEdge(SippRRTNode from, SippRRTNode to, List<Straight> straights) {
        this.from = from;
        this.to = to;
        this.straights = straights;
        this.duration = Trajectories.duration(straights);
    }

    public List<Straight> getStraights() {
        return straights;
    }

    public int getDuration() {
        return duration;
    }

    public Trajectory trajectory() {
        return new BasicSegmentedTrajectory(straights, duration, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SippRRTEdge that = (SippRRTEdge) o;

        if (!from.equals(that.from)) return false;
        if (!straights.equals(that.straights)) return false;
        if (!to.equals(that.to)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * from.hashCode() + to.hashCode()) + straights.hashCode();
    }

}
