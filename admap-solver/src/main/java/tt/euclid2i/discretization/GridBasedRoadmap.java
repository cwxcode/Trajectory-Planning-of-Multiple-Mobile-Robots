package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DummyEdgeFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Union;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.NearestNeighborIterator;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;


public class GridBasedRoadmap extends DirectedWeightedMultigraph<Point, Line> {

    private static final long serialVersionUID = 7461735648599585309L;

    private int radius;
    private Point[] customPoints;

    private Collection<Region> boundaryRegions;
    private Collection<Region> obstacles;

    private KdTree<Point> knnTree;
    
    public GridBasedRoadmap(int dispersion, int connectionRadius, Point[] customPoints, 
    		Collection<Region> boundaryRegions, Collection<Region> obstacles, boolean keepOnlyLargestComponent) {
        super(new DummyEdgeFactory<Point, Line>());
        this.radius = connectionRadius;
        this.customPoints = customPoints;
        this.boundaryRegions = boundaryRegions;
        this.obstacles = obstacles;
        this.knnTree = new KdTree<Point>(2);

        generateVerticesWithDispersion(dispersion);
        generateEdges();
        
        if (keepOnlyLargestComponent) {
        	removeDisconnectedComponents();
        }
    }

    private void removeDisconnectedComponents() {
    	ConnectivityInspector<Point, Line> connectivityInspector = new ConnectivityInspector<Point, Line>(this);
    	List<Set<Point>> components = connectivityInspector.connectedSets();
    	
    	Set<Point> largestComponent = null;
    	for (Set<Point> component : components) {
    		if (largestComponent == null || component.size() > largestComponent.size()) {
    			largestComponent = component;
    		}
    	}
    	
    	HashSet<Point> originalVertices = new HashSet<Point>(vertexSet());
    	for (Point vertex : originalVertices) {
    		if (!largestComponent.contains(vertex)) {
    			removeVertex(vertex);
    		}
    	}
    	
	}

	private void generateEdges() {
        for (Point point : vertexSet()) {
            NearestNeighborIterator<Point> iterator = knnTree.getNearestNeighborIterator(key(point), vertexSet().size(), new SquareEuclideanDistanceFunction());

            while (iterator.hasNext()) {
                Point next = iterator.next();

                if (point.equals(next))
                    continue;

                if (point.distance(next) > radius)
                    break;

                if (Util.isVisible(point, next, obstacles) && Util.isVisible(point, next, boundaryRegions))
                    addEdge(point, next, new Line(point, next));
            }
        }
    }

    private void generateVerticesWithDispersion(int dispersion) {

    	final Collection<Region> boundaryRegionsFlipped = new LinkedList<Region>();
        for (Region r : boundaryRegions) {
        	Polygon p = (Polygon)r;        	
        	
        	if (!p.isFilledInside()) {
        		p = p.flip();
        	}
        	
        	if (p.isFilledInside()) {
        		// during the inflation sometimes we get small degenerate polygons, one, two pixels in size, 
        		// where reversing does not do the job
        		boundaryRegionsFlipped.add(p);
        	}
        }

    	Union samplingRegion = new Union(boundaryRegionsFlipped);
    	Rectangle bounds = samplingRegion.getBoundingBox();
        int width  = bounds.getCorner2().x - bounds.getCorner1().x;
        int height = bounds.getCorner2().y - bounds.getCorner1().y;
        int cellSize = (int) Math.round(dispersion/Math.sqrt(2));

        int columns = width / cellSize + 1;
        int rows = height / cellSize + 1;
        
        LinkedList<Region> obstaclesCopy = new LinkedList<Region>(obstacles);
        for (Region obstacle : obstaclesCopy) {
        	if (obstacle instanceof Polygon && !((Polygon) obstacle).isFilledInside()) {
        		System.out.println("One of the obstacles is outside-filled. Removing!");
        		obstacles.remove(obstacle);
        	}
        }
        
        for (int row=0; row<rows; row++) {
            for (int col=0; col<columns; col++) {
                int x = bounds.getCorner1().x + col*cellSize + cellSize/2;
                int y = bounds.getCorner1().y + row*cellSize + cellSize/2;
                Point point = new Point(x, y);
            	if (Util.isInFreeSpace(point, boundaryRegions, obstacles)) {
	                addVertex(point);
	                knnTree.addPoint(key(point), point);
            	}
            }
        }

        for (int i = 0; i < customPoints.length; i++) {
        	if (bounds.isInside(customPoints[i])) {
	            knnTree.addPoint(key(customPoints[i]), customPoints[i]);
	            addVertex(customPoints[i]);
        	}
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
