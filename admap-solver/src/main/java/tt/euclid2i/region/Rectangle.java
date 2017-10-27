package tt.euclid2i.region;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;
import tt.euclid2i.Region;

public class Rectangle implements Region {

    Point corner1;
    Point corner2;

    public Rectangle(int corner1X, int corner1Y, int corner2X, int corner2Y) {
        this(new tt.euclid2i.Point(corner1X, corner1Y), new tt.euclid2i.Point(corner2X, corner2Y));
    }

    public Rectangle(Point corner1, Point corner2) {
        super();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        tt.euclid2d.Point startPoint = new tt.euclid2d.Point(p1.x, p1.y);
        tt.euclid2d.Point endPoint = new tt.euclid2d.Point(p2.x, p2.y);

        return Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner1.x, corner1.y), new tt.euclid2d.Point(corner1.x, corner2.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner1.x, corner2.y), new tt.euclid2d.Point(corner2.x, corner2.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner2.x, corner2.y), new tt.euclid2d.Point(corner2.x, corner1.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner2.x, corner1.y), new tt.euclid2d.Point(corner1.x, corner1.y), true) ;
    }

    @Override
    public boolean isInside(Point p) {
        return corner1.x <= p.x && p.x <= corner2.x &&
                corner1.y <= p.y && p.y <= corner2.y;
    }

    public Point getCorner1() {
        return corner1;
    }

    public Point getCorner2() {
        return corner2;
    }

    public Rectangle inflate(int inflateBy) {
        int minx = Math.min(corner1.x, corner2.x);
        int maxx = Math.max(corner1.x, corner2.x);
        int miny = Math.min(corner1.y, corner2.y);
        int maxy = Math.max(corner1.y, corner2.y);

        return new Rectangle(new Point(minx-inflateBy, miny-inflateBy), new Point(maxx+inflateBy, maxy+inflateBy));
    }

    @Override
    public Rectangle getBoundingBox() {
        return this;
    }

    public Polygon toPolygon() {

        int minX = Math.min(corner1.x, corner2.x);
        int maxX = Math.max(corner1.x, corner2.x);
        int minY = Math.min(corner1.y, corner2.y);
        int maxY = Math.max(corner1.y, corner2.y);


        return new Polygon(new Point[] {
                new Point(maxX, minY),
                new Point(maxX, maxY),
                new Point(minX, maxY),
                new Point(minX, minY)});
    }

    public Polygon toOutFilledPolygon() {

        int minX = Math.min(corner1.x, corner2.x);
        int maxX = Math.max(corner1.x, corner2.x);
        int minY = Math.min(corner1.y, corner2.y);
        int maxY = Math.max(corner1.y, corner2.y);


        return new Polygon(new Point[] {
                new Point(maxX, minY),
                new Point(minX, minY),
                new Point(minX, maxY),
                new Point(maxX, maxY),
                });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rectangle rectangle = (Rectangle) o;

        if (!corner1.equals(rectangle.corner1)) return false;
        if (!corner2.equals(rectangle.corner2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = corner1.hashCode();
        result = 31 * result + corner2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Rectangle [corner1=" + corner1 + ", corner2=" + corner2 + "]";
    }

	public boolean overlap(Rectangle other) {

		if (this.corner2.x < other.corner1.x || 
			other.corner2.x < this.corner1.x || 
			this.corner2.y < other.corner1.y ||
			other.corner2.y < this.corner1.y) {
			return false;
		} else {
			return true;
		}
	}


}
