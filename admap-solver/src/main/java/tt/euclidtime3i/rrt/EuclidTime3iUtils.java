package tt.euclidtime3i.rrt;

import org.jgrapht.GraphPath;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vojtech Letal on 3/7/14.
 */
public class EuclidTime3iUtils {

    public static SegmentedTrajectory toTrajectory(GraphPath<Point, List<Straight>> path, int duration) {
        ArrayList<Straight> straights = concatenate(path.getEdgeList());
        return new BasicSegmentedTrajectory(straights, duration, path.getWeight());
    }

    public static <E> ArrayList<E> concatenate(List<? extends List<? extends E>> lists) {
        ArrayList<E> concatenated = new ArrayList<E>();
        for (List<? extends E> list : lists) {
            concatenated.addAll(list);
        }
        return concatenated;
    }


}
