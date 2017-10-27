package tt.planner.rrtstar;

import tt.planner.rrtstar.domain.GraphDomain;
import tt.planner.rrtstar.domain.GraphPathEdge;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.ExtensionEstimate;
import tt.planner.rrtstar.util.Vertex;
import tt.util.EuclideanCoordinatesProvider;

import javax.vecmath.Point2i;
import java.util.Collection;

public class EuclideanGraphRRTStar<S extends Point2i, E> extends EuclideanRRTStar<S, GraphPathEdge<S, E>> {

    GraphDomain<S, E> domain;

    public EuclideanGraphRRTStar(GraphDomain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                 S initialState, double initialRadius, double minRadius, double maxRadius) {
        super(domain, euclideanProvider, initialState, initialRadius, minRadius, maxRadius);
        this.domain = domain;
    }

    public EuclideanGraphRRTStar(GraphDomain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                 S initialState, double initialRadius) {
        this(domain, euclideanProvider, initialState, initialRadius, 0, Double.POSITIVE_INFINITY);
    }


    @Override
    public void iterate() {
        domain.setCostLimit(2 * getNearBallRadius());
        super.iterate();
    }

    @Override
    protected void rewire(Vertex<S, GraphPathEdge<S, E>> candidateParent, Collection<Vertex<S, GraphPathEdge<S, E>>> vertices) {
        for (Vertex<S, GraphPathEdge<S, E>> nearVertex : vertices) {
            if (nearVertex != candidateParent) {
                rewire(candidateParent, nearVertex);
            }
        }
    }

    private void rewire(Vertex<S, GraphPathEdge<S, E>> candidateParent, Vertex<S, GraphPathEdge<S, E>> vertex) {
        S candidateParentState = candidateParent.getState();
        S vertexState = vertex.getState();

        Extension<S, GraphPathEdge<S, E>> extension = domain.extendTo(candidateParentState, vertexState);

        if (extension == null || !vertexState.equals(extension.target)) return;

        double rewiredCostToRoot = candidateParent.getCostFromRoot() + extension.cost;
        double costToRoot = vertex.getCostFromRoot();

        if (extension.exact && rewiredCostToRoot < costToRoot) {
            insertExtension(candidateParent, extension, vertex);
            updateBranchCost(vertex);
        }
    }

    @Override
    protected BestParentSearchResult findBestParent(S randomSample, Collection<Vertex<S, GraphPathEdge<S, E>>> nearVertices) {
        BestParentSearchResult result = null;
        ExtensionCost minCost = ExtensionCost.minCostValue();

        for (Vertex<S, GraphPathEdge<S, E>> vertex : nearVertices) {
            S candidateState = vertex.getState();

            ExtensionEstimate extensionEst = domain.estimateExtension(candidateState, randomSample);
            if (estimateIsWorseThanMin(extensionEst, minCost))
                continue;

            Extension<S, GraphPathEdge<S, E>> extension = domain.extendTo(candidateState, randomSample);
            if (!extensionIsValid(randomSample, extension))
                continue;

            ExtensionCost cost = new ExtensionCost(vertex.getCostFromRoot(), extension.cost);
            if (cost.compareTo(minCost) < 0) {
                minCost = cost;
                result = new BestParentSearchResult(vertex, extension);
            }
        }

        return result;
    }

    private boolean extensionIsValid(S randomSample, Extension<S, GraphPathEdge<S, E>> extension) {
        return extension != null && extension.exact && randomSample.equals(extension.target);
    }

    private boolean estimateIsWorseThanMin(ExtensionEstimate extensionEst, ExtensionCost minCost) {
        return extensionEst != null && extensionEst.cost > minCost.overallCost;
    }

    protected static class ExtensionCost implements Comparable<ExtensionCost> {
        final double costFromRoot;
        final double overallCost;

        public ExtensionCost(double costFromRoot, double extensionCost) {
            this.costFromRoot = costFromRoot;
            this.overallCost = costFromRoot + extensionCost;
        }


        public static ExtensionCost minCostValue() {
            return new ExtensionCost(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        @Override
        public int compareTo(ExtensionCost that) {
            //TODO comparing double
            int comp1 = Double.compare(this.overallCost, that.overallCost);
            return comp1 == 0 ? Double.compare(that.costFromRoot, this.costFromRoot) : comp1;
        }
    }
}
