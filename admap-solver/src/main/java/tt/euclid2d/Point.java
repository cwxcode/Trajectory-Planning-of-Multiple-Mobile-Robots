package tt.euclid2d;

import javax.vecmath.Point2d;
import javax.vecmath.Point2i;
import javax.vecmath.Tuple2d;

public class Point extends Point2d {

	private static final long serialVersionUID = 1936317975431366847L;
    private static final Point zero = new Point(0, 0);

    public static Point zero() {
        return zero;
    }

    public Point() {
        super();
    }

    public Point(double x, double y) {
        super(x, y);
    }

    public Point(Point2i p) {
        super(p.x, p.y);
    }

    public Point(Tuple2d t1) {
        super(t1);
    }

    public static Point interpolate(Point p1, Point p2, double alpha) {
        Point result = new Point();
        ((Tuple2d) result).interpolate(p1, p2, alpha);
        return result;
    }

    public double[] toDoubleArray() {
        return new double[]{x, y};
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
    
    public tt.euclid2i.Point toPoint2i() {
    	return new tt.euclid2i.Point((int) Math.round(x), (int) Math.round(y));
    }
    
}
