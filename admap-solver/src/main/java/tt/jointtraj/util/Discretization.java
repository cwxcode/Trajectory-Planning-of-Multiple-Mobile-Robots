package tt.jointtraj.util;

import org.jgraph.graph.Edge;
import org.jgrapht.DirectedGraph;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.discretization.AdditionalPointsExtension;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.jointeuclid2ni.probleminstance.AgentMission;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Discretization {

    public static DirectedGraph<Point, Line> createGrid(Environment environment, AgentMission mission, int[][] gridPattern, int gridStep) {
        Collection<Region> inflatedObstacles = Util.inflateRegions(environment.getObstacles(), mission.getBodyRadius());

        DirectedGraph<Point, Line> grid = new LazyGrid(mission.getStart(), inflatedObstacles, environment.getBoundary().getBoundingBox(),
                gridPattern, gridStep).generateFullGraph();

        if (grid.containsVertex(mission.getTarget()))
            return grid;
        else
            return new AdditionalPointsExtension(grid, Collections.singleton(mission.getTarget()), gridStep * 2);
    }

    public static DirectedGraph<Point, Line> createGrid(Environment environment, int bodyRadius, int[][] gridPattern, int gridStep) {
        Rectangle bounds = environment.getBoundary().getBoundingBox();
        Point corner1 = bounds.getCorner1();
        Point corner2 = bounds.getCorner2();

        int x = (Math.max(corner1.x, corner2.x) % gridStep) / 2;
        int y = (Math.max(corner1.y, corner2.y) % gridStep) / 2;
        Point start = new Point(x, y);

        LazyGrid lazyGrid = new LazyGrid(start, Collections.EMPTY_LIST, bounds, gridPattern, gridStep);
        DirectedGraph<Point, Line> fullGrid = lazyGrid.generateFullGraph();

        Collection<Region> inflatedObstacles = Util.inflateRegions(environment.getObstacles(), bodyRadius);

        Collection<Point> invalidVertices = new LinkedList<Point>();
        for (Point vertex : fullGrid.vertexSet()) {
            if (!Util.isInFreeSpace(vertex, inflatedObstacles)) {
                invalidVertices.add(vertex);
            }
        }
        fullGrid.removeAllVertices(invalidVertices);

        Collection<Line> invalidEdges = new LinkedList<Line>();
        for (Line edge : fullGrid.edgeSet()) {
            if (!Util.isVisible(edge.getStart(), edge.getEnd(), inflatedObstacles)) {
                invalidEdges.add(edge);
            }
        }
        fullGrid.removeAllEdges(invalidEdges);

        return fullGrid;
    }
}
