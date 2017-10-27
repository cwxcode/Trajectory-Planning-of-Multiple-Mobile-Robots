package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.Random;

import org.jgrapht.DummyEdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.NearestNeighborIterator;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;


public class ProbabilisticRoadmap extends DirectedWeightedMultigraph<Point, Line> {

    private static final long serialVersionUID = 7461735648599585309L;

    private int nVertices;
    private int radius;
    private Point[] customPoints;
    private Rectangle bounds;
    private Collection<Region> obstacles;
    private Random random;

    private KdTree<Point> knnTree;

    public ProbabilisticRoadmap(int nVertices, int radius, Point[] customPoints, Rectangle bounds, Collection<Region> obstacles, Random random) {
        super(new DummyEdgeFactory<Point, Line>());
        this.nVertices = nVertices;
        this.radius = radius;
        this.customPoints = customPoints;
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.random = random;

        this.knnTree = new KdTree<Point>(2);

        generateVertices();
        generateEdges();
    }

    private void generateEdges() {
        for (Point point : vertexSet()) {
            NearestNeighborIterator<Point> iterator
                = knnTree.getNearestNeighborIterator(key(point), vertexSet().size(), new SquareEuclideanDistanceFunction());

            while (iterator.hasNext()) {
                Point next = iterator.next();

                if (point.equals(next))
                    continue;

                if (point.distance(next) > radius)
                    break;

                if (Util.isVisible(point, next, obstacles))
                    addEdge(point, next, new Line(point, next));
            }
        }
    }

    private void generateVertices() {
        for (int i = 0; i < nVertices; i++) {
            int x = bounds.getCorner1().x + random.nextInt(bounds.getCorner2().x - bounds.getCorner1().x);
            int y = bounds.getCorner1().y + random.nextInt(bounds.getCorner2().y - bounds.getCorner1().y);
            Point point = new Point(x, y);
            addVertex(point);
            knnTree.addPoint(key(point), point);
        }

        for (int i = 0; i < customPoints.length; i++) {
            knnTree.addPoint(key(customPoints[i]), customPoints[i]);
            addVertex(customPoints[i]);
        }
    }

    private static double[] key(Point point) {
        return new double[]{point.getX(), point.getY()};
    }

    @Override
    public double getEdgeWeight(Line edge) {
        return edge.getDistance();
    }

}
