package tt.euclidtime3i;

import tt.euclidtime3i.region.HyperRectangle;

/**
 * A region in an Euclidean 2d spacetime
 */
public interface Region {
    public boolean intersectsLine(Point p1, Point p2);
    public boolean isInside(Point p);
    public HyperRectangle getBoundingBox();
}
