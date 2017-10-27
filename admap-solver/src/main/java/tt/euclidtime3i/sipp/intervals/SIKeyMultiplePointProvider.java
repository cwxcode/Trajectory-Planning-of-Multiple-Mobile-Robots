package tt.euclidtime3i.sipp.intervals;

import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.NearestNeighborIterator;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;
import tt.euclid2i.Point;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vojtech Letal on 3/16/14.
 */
class SIKeyMultiplePointProvider<V extends Point> implements SIKeyProvider<V> {

    private KdTree<V> kdTree;

    public SIKeyMultiplePointProvider(Set<V> vertices) {
        kdTree = new KdTree<V>(2);
        for (V vertex : vertices) {
            kdTree.addPoint(vertex.toDoubleArray(), vertex);
        }
    }

    @Override
    public Collection<V> getAffectedKeys(Point[] obstacles, int[] separation) {
        Set<V> nodes = new HashSet<V>();

        for (int i = 0; i < obstacles.length; i++) {
            NearestNeighborIterator<V> iterator
                    = kdTree.getNearestNeighborIterator(obstacles[i].toDoubleArray(), kdTree.size(), new SquareEuclideanDistanceFunction());

            for (V affected : iterator)
                if (obstacles[i].distance(affected) < separation[i])
                    nodes.add(affected);
                else
                    break;
        }
        return nodes;
    }
}
