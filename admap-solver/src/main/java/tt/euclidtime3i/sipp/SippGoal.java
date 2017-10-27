package tt.euclidtime3i.sipp;

import org.jgrapht.util.Goal;
import tt.euclid2i.Point;

public class SippGoal implements Goal<SippNode> {

    private Point target;
    private int maxTime;

    public SippGoal(Point target, int maxTime) {
        this.target = target;
        this.maxTime = maxTime;
    }

    @Override
    public boolean isGoal(SippNode current) {
        return isInLastSafeInterval(current) && isOnTarget(current);
    }


    public boolean isOnTarget(SippNode current) {
        return current.getPoint().equals(target);
    }

    public boolean isInLastSafeInterval(SippNode current) {
        return current.getSafeInterval().end == maxTime;
    }

}
