package cz.agents.alite.planner.spatialmaneuver;

import java.util.LinkedList;
import java.util.ListIterator;

import javax.vecmath.Point3d;

/**
 * The polyline in 3D representing the path found by the A* pathfinder.
 *
 * @author Miroslav Uller
 */
public class Path implements Cloneable {

    /**
     * The list of points. The polyline is a sequence of joined line segments with these points as their endpoints.
     */
    LinkedList<Point3d> path;

    /**
     * Class constructor.
     * Creates an empty path.
     */
    public Path() {
        path = new LinkedList<Point3d>();
    }

    /**
     * Adds a point to the end of the path.
     * @param pt a point
     */
    public void addPoint(Point3d pt) {
        path.add(pt);
    }

    /**
     * Adds a point to the end of the path.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param z the z coordinate of the point
     */
    public void addPoint(double x, double y, double z) {
        path.add(new Point3d(x, y, z));
    }

    /**
     * Adds a point to the start of the path.
     * @param pt a point
     */
    public void addPointToStart(Point3d pt) {
        path.addFirst(pt);
    }

    /**
     * Adds a point to the end of the path.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param z the z coordinate of the point
     */
    public void addPointToStart(double x, double y, double z) {
        path.addFirst(new Point3d(x, y, z));
    }

    /**
     * Returns the number of points in this path.
     * @return the number of points
     */
    public int size() {
        return path.size();
    }

    /**
     * Prunes this path. Removes some of the points of the path, so that the minimum distance of adjacent points is greater or equal than the parameter distance.
     * @param minDistance the requested minimum distance between adjacent points
     * @todo modify - use better approach, e.g. Douglas-Pluecker algorithm
     */
    public void prune(double minDistance) {
        if(path.size() < 3) return;
        ListIterator<Point3d> iter = path.listIterator();
        Point3d p1, p2;
        double d2 = minDistance*minDistance;
        p1 = iter.next();
        while(iter.hasNext()) {
            p2 = iter.next();
            if(p1.distanceSquared(p2) < d2 && iter.hasNext()) {
                iter.remove();
            }
            p1 = p2;
        }

    }

    /**
     * Returns an iterator for iterating over the sequence of points of this path.
     * @return an iterator for iterating over the sequence of points
     */
    public ListIterator<Point3d> iterator() {
        return path.listIterator();
    }

    @Override
    public Object clone() {
        Path copy = new Path();
        for(Point3d pt : path) {
            copy.addPoint(new Point3d(pt));
        }
        return copy;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
