package tt.jointtrajineuclidtime3i;

import java.util.List;

import org.jgrapht.DirectedGraph;

import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.Straight;

public class ODGraphWithSoftObstacles extends ODGraph {

    List<Region> softObstacles;

    public ODGraphWithSoftObstacles(
            DirectedGraph<Point, Straight>[] motionModels, int[] bodyRadiuses) {
        super(motionModels, bodyRadiuses);
        this.softObstacles = softObstacles;
    }

    @Override
    public double getEdgeWeight(Transition edge) {
        return super.getEdgeWeight(edge);
    }
}
