package tt.euclid2i;

import tt.euclid2i.region.Rectangle;


/**
 * A region in an Euclidean 2i space
 */
public interface Region {
    public boolean intersectsLine(Point p1, Point p2);
    public boolean isInside(Point p);
    public Rectangle getBoundingBox();
}
