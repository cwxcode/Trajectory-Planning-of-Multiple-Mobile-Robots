package tt.planner.rrtstar;

import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;
import ags.utils.dataStructures.utils.MaxHeap;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.Vertex;
import tt.util.EuclideanCoordinatesProvider;

import java.util.*;

public class EuclideanRRTStar<S, E> extends RRTStar<S, E> {

    protected Set<S> kdKeys;
    protected KdTree<Vertex<S, E>> knnKdTree;
    protected EuclideanCoordinatesProvider<S> euclideanProvider;
    protected SquareEuclideanDistanceFunction distanceFunction;

    public EuclideanRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                            S initialState, double initialRadius, double minRadius, double maxRadius) {
        super(domain, initialState, initialRadius, minRadius, maxRadius);

        int dimensions = euclideanProvider.getSpaceDimension();

        this.euclideanProvider = euclideanProvider;
        this.distanceFunction = new SquareEuclideanDistanceFunction();

        this.knnKdTree = new KdTree<Vertex<S, E>>(dimensions);
        this.kdKeys = new HashSet<S>();

        insertIntoKDTree(root);
    }

    public EuclideanRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                            S initialState, double initialRadius) {
        this(domain, euclideanProvider, initialState, initialRadius, 0, Double.POSITIVE_INFINITY);
    }

    @Override
    protected Vertex<S, E> insertExtension(Vertex<S, E> parent, Extension<S, E> extension) {
        Vertex<S, E> newVertex = super.insertExtension(parent, extension);

        if (newVertex != null) {
            insertIntoKDTree(newVertex);
        }

        return newVertex;
    }

    private void insertIntoKDTree(Vertex<S, E> newVertex) {
        S state = newVertex.state;
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        if (!kdKeys.contains(state)) {
            knnKdTree.addPoint(key, newVertex);
            kdKeys.add(state);
        }
    }

    @Override
    protected Vertex<S, E> getNearestParentVertex(S state) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        MaxHeap<Vertex<S, E>> nearestNeighbour = knnKdTree.findNearestNeighbors(key, 1, distanceFunction);

        return nearestNeighbour.getMax();
    }

    @Override
    protected Collection<Vertex<S, E>> getNearParentCandidates(S state) {
        double radius = getNearBallRadius();
        return getVerticesWithinRadius(state, radius);
    }

    @Override
    protected Collection<Vertex<S, E>> getNearChildrenCandidates(S state) {
        double radius = getNearBallRadius();
        LinkedList<Vertex<S, E>> childrenCandidates = getVerticesWithinRadius(state, radius);
        removeStateIfFirst(state, childrenCandidates);

        return childrenCandidates;
    }

    protected void removeStateIfFirst(S state, LinkedList<Vertex<S, E>> childrenCandidates) {
        if (childrenCandidates.size() == 0)
            return;

        Vertex<S, E> nearest = childrenCandidates.get(0);
        if (state.equals(nearest.state)) {
            childrenCandidates.removeFirst();
        } else {
            childrenCandidates.removeLast();
        }

    }

    protected LinkedList<Vertex<S, E>> getVerticesWithinRadius(S state, double radius) {
        double radius_sq = radius * radius;
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        Iterator<Vertex<S, E>> iterator =
                knnKdTree.getNearestNeighborIterator(key, kdKeys.size(), distanceFunction);
        LinkedList<Vertex<S, E>> list = new LinkedList<Vertex<S, E>>();

//        int vertices = 0;
        while (iterator.hasNext()) {
            Vertex<S, E> vertex = iterator.next();
            double[] key2 = euclideanProvider.getEuclideanCoordinates(vertex.state);
            if (distanceSquared(key, key2) < radius_sq) {
                list.add(vertex);
//                vertices++;
            } else {
                break;
            }
        }

//        System.out.println(String.format("ball,%f,%d", radius, vertices));

        return list;
    }

    protected static double distanceSquared(double[] key1, double[] key2) {
        double dist_sq = 0;
        for (int i = 0; i < key1.length; i++) {
            double diff = key1[i] - key2[i];
            dist_sq += diff * diff;
        }
        return dist_sq;
    }
}