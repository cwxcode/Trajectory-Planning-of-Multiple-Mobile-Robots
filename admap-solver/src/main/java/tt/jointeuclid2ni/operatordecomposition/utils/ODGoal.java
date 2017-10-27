package tt.jointeuclid2ni.operatordecomposition.utils;

import org.jgrapht.util.Goal;
import tt.euclid2i.Point;
import tt.jointeuclid2ni.operatordecomposition.ODNode;

import java.util.Arrays;

public class ODGoal implements Goal<ODNode> {
    private Point[] targets;

    public ODGoal(Point[] targets) {
        this.targets = targets;
    }

    @Override
    public boolean isGoal(ODNode current) {
        if (!current.isComplete())
            return false;

        Point[] values = current.getAgentPositions();
        return Arrays.deepEquals(values, targets);
    }
}
