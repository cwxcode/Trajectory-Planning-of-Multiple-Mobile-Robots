package tt.euclid2i.region;

import tt.euclid2i.Point;
import tt.euclid2i.Region;

public class Circle implements Region {

	Point center;
	int radius;

	public Circle(Point center, int radius) {
		super();
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean intersectsLine(Point p1, Point p2) {

		if (p1.equals(p2)) {
			return isInside(p1);
		}

		if (isInside(p1) || isInside(p2)) {
			return true;
		}

		int x1 = p1.x - center.x;
		int x2 = p2.x - center.x;

		int y1 = p1.y - center.y;
		int y2 = p2.y - center.y;

		double a = x1*x1 + y1*y1 +x2*x2 + y2*y2 - 2*x1*x2 - 2*y1*y2;
		double b = -2*x2*x2 -2*y2*y2 + 2*x1*x2 + 2*y1*y2;
		double c = x2*x2 + y2*y2 - radius*radius;

		double discr = b*b - 4*a*c;

		if (discr > 0) {
			// we have two solutions
			double t1 = (-b + Math.sqrt(discr)) / (2*a);
			double t2 = (-b - Math.sqrt(discr)) / (2*a);

			if ( (t1 >= 0 && t1 <= 1.0) || (t2 >= 0 && t2 <= 1.0) ) {
				return true;
			} else {
				return false;
			}
		} else if (Math.abs(discr) < 0.001) {
			double t =  (-b) / 2*a;
			if (t >= 0 && t <= 1.0) {
				return true;
			} else {
				return false;
			}
		} else if (discr < 0) {
			// supporting line does not intersect the circle
			return false;
		}

		throw new RuntimeException("Discriminant: " + discr);
	}

	@Override
	public boolean isInside(Point p) {
		return center.distance(p) <= radius;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(new Point(center.x - radius, center.y - radius),
							 new Point(center.x + radius, center.y + radius));
	}

	public Point getCenter() {
		return center;
	}

	public int getRadius() {
		return radius;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Circle circle = (Circle) o;

        if (radius != circle.radius) return false;
        if (!center.equals(circle.center)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = center.hashCode();
        result = 31 * result + radius;
        return result;
    }

	@Override
	public String toString() {
		return "Circle(" + center + ", r=" + radius + ")";
	}
	
	public Polygon toPolygon(int nCorners) {
		Point[] points = new Point[nCorners];
		double angleStep = 2*Math.PI / nCorners;
		
		int i=0;
		for (double angle = 0; angle < 2*Math.PI; angle += angleStep) {
			points[i++] = new Point(center.x + (int) (radius*Math.cos(angle)), center.y + (int) (radius*Math.sin(angle)));
		}
		
		return new Polygon(points);
	}


}
