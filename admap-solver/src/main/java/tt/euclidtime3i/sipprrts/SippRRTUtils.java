package tt.euclidtime3i.sipprrts;

import org.jgrapht.GraphPath;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vojtech Letal on 2/28/14.
 */
public class SippRRTUtils {

    public static SegmentedTrajectory toTrajectory(GraphPath<SippRRTNode, SippRRTEdge> path, int maxtime) {
        List<Straight> straights = new ArrayList<Straight>();

        for (SippRRTEdge edge : path.getEdgeList()) {
            straights.addAll(edge.getStraights());
        }

        return new BasicSegmentedTrajectory(straights, maxtime, path.getWeight());
    }

}
