package tt.planner.rrtstar;

import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Vertex;
import tt.util.EuclideanCoordinatesProvider;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class EuclideanKRRTStar<S, E> extends EuclideanRRTStar<S, E> {

    protected int k;

    public EuclideanKRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                             S initialState, int k) {
        super(domain, euclideanProvider, initialState, Double.NaN);
        this.k = k;
    }

    @Override
    public double getNearBallRadius() {
        return Double.NaN;
    }


    @Override
    protected Collection<Vertex<S, E>> getNearParentCandidates(S state) {
        return getKNearestVertices(state, k);
    }

    @Override
    protected Collection<Vertex<S, E>> getNearChildrenCandidates(S state) {
        // gets k+1 neighbours because the very first is the state S itself
        LinkedList<Vertex<S, E>> childrenCandidates = getKNearestVertices(state, k + 1);
        removeStateIfFirst(state, childrenCandidates);

        return childrenCandidates;
    }

    protected LinkedList<Vertex<S, E>> getKNearestVertices(S state, int k) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        Iterator<Vertex<S, E>> iterator =
                knnKdTree.getNearestNeighborIterator(key, kdKeys.size(), distanceFunction);
        LinkedList<Vertex<S, E>> list = new LinkedList<Vertex<S, E>>();

        Vertex<S, E> vertex = null;
//        int vertices = 0;

        for (int i = 0; i < k; i++) {
            if (iterator.hasNext()) {
                vertex = iterator.next();
                list.add(vertex);
//                vertices++;
            } else {
                break;
            }
        }
//        if (vertex != null)
//            System.out.println(String.format("ball,%f,%d", state.distance(vertex.state), vertices));
        return list;
    }
}
