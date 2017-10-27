package tt.euclid2i.trajectory;

import org.jgrapht.GraphPath;

import tt.euclidtime3i.discretization.Straight;


/**
 * A wrapper that interprets a graph path in euclidean 2i + time graph as a trajectory.
 */

public class StraightSegmentTrajectory extends BasicSegmentedTrajectory {

    GraphPath<tt.euclidtime3i.Point, Straight> graphPath;

    public StraightSegmentTrajectory(GraphPath<tt.euclidtime3i.Point, Straight> graphPath, int duration) {

        super(graphPath.getEdgeList(), duration, graphPath.getWeight());
        this.graphPath = graphPath;
    }

}
