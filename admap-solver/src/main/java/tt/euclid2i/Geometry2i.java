package tt.euclid2i;

import tt.euclid2d.Geometry2d;

public class Geometry2i {

    private Geometry2i() {
    }

    public static double distance(Line l, Point p) {
        return Geometry2d.distance(l.toLine2d(), p.toPoint2d());
    }

    public static double dot(Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Point sub(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }
}
