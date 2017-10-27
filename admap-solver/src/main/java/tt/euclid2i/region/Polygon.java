package tt.euclid2i.region;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;
import tt.euclid2i.Region;

public class Polygon implements Region, Serializable{

    private static final long serialVersionUID = -8113732712690427548L;

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    public void addPoint(Point point) {
        int len = points.length;
        points = Arrays.copyOf(points, len + 1);
        points[len] = point;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < points.length-1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[i].x, points[i].y, points[i+1].x, points[i+1].y, true)) {
                return true;
            }
        }
        int l = points.length-1;
        if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[l].x, points[l].y, points[0].x, points[0].y, true)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isInside(Point p) {
          int i;
          int j;
          boolean result = false;
          for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > p.y) != (points[j].y > p.y) &&
                (p.x < (points[j].x - points[i].x) * (p.y - points[i].y) / (points[j].y-points[i].y) + points[i].x)) {
              result = !result;
             }
          }

          if (isFilledInside()) {
              return result;
          } else {
              return !result;
          }
    }

    public Point[] getPoints() {
        return points;
    }

    @Override
    public Rectangle getBoundingBox() {
        //TODO check implementation
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            int X = point.getX();
            int Y = point.getY();

            if (X < minX) minX = X;
            if (Y < minY) minY = Y;
            if (X > maxX) maxX = X;
            if (Y > maxY) maxY = Y;
        }

        return new Rectangle(new Point(minX,minY),new Point(maxX,maxY));
    }

    public boolean isFilledInside() {
        return isClockwise(points);
    }

    public List<Polygon> inflate(double inflateBy, int pointsAtCorner) {

        tt.euclid2d.region.Polygon inPolygon2d = new tt.euclid2d.region.Polygon(points);
        List<tt.euclid2d.region.Polygon> inflatedPolygons2d = inPolygon2d.inflate(inflateBy, pointsAtCorner);

        List<Polygon> inflatedPolygons = new LinkedList<Polygon>();

        for (tt.euclid2d.region.Polygon inflatedPolygon2d : inflatedPolygons2d) {
            // convert back to 2i
            Point[] inflatedPoints = new Point[inflatedPolygon2d.getPoints().length];
            for (int i = 0; i < inflatedPolygon2d.getPoints().length; i++) {
                inflatedPoints[i] = new Point((int) inflatedPolygon2d.getPoints()[i].x, (int) inflatedPolygon2d.getPoints()[i].y);
            }

           inflatedPolygons.add(new Polygon(inflatedPoints));
        }

        return inflatedPolygons;
    }

    public Polygon minkowskiSum(Polygon q) {
        tt.euclid2d.region.Polygon inPolygon2d = new tt.euclid2d.region.Polygon(points);
        tt.euclid2d.region.Polygon qPolygon2d = new tt.euclid2d.region.Polygon(q.getPoints());

        tt.euclid2d.region.Polygon sum = inPolygon2d.minkowskiSum(qPolygon2d);

        Point[] sumPoints = new Point[sum.getPoints().length];
        for (int i = 0; i < sum.getPoints().length; i++) {
            sumPoints[i] = new Point((int) sum.getPoints()[i].x, (int) sum.getPoints()[i].y);
        }

        return new Polygon(sumPoints);
    }

    /**
     * Determines if the ring of points is defined in a clockwise direction
     * @param points the array of points constituting the border of the polygon
     * @return true if the ring is defined clockwise
     */
    public static boolean isClockwise(Point[] points){

        double sumOverEdges = 0;

        for (int i = 0; i < points.length; i++) {
            if(i < points.length - 1){
                sumOverEdges += ((points[i + 1].x - points[i].x) * (points[i + 1].y + points[i].y));
            }
            else{
                sumOverEdges += ((points[0].x - points[i].x) * (points[0].y + points[i].y));
            }
        }

        return (sumOverEdges < 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polygon polygon = (Polygon) o;

        if (!Arrays.equals(points, polygon.points)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }

	public int area() {
		Point[] pointsArr = Arrays.copyOf(points, points.length);

		if (isClockwise(pointsArr)) {
			ArrayUtils.reverse(pointsArr);
		}

		int area = 0;
		int j = pointsArr.length - 1;

		for (int i = 0; i < pointsArr.length; i++) {
			area = area + (pointsArr[j].x + pointsArr[i].x)
					* (pointsArr[j].y - pointsArr[i].y);
			j = i;
		}
		return area / 2;
	}

	/** Flips outside-filled polygon to outside-filled polygon and vice versa **/
	public Polygon flip() {
		Point[] newPoints = Arrays.copyOf(points, points.length);
		ArrayUtils.reverse(newPoints);
		return new Polygon(newPoints);
	}

	@Override
	public String toString() {
		return (isFilledInside() ? "(in)" : "(out)") + Arrays.toString(points);
	}
	
	public tt.euclid2d.region.Polygon toPolygon2d() {
		tt.euclid2d.Point[] points2d = new tt.euclid2d.Point[points.length];
		for (int i = 0; i < points.length; i++) {
			points2d[i] = new tt.euclid2d.Point(points[i].x, points[i].y);
		}
		return new tt.euclid2d.region.Polygon(points2d);
	}

    public Polygon getRotated(double angleRad) {
        return getRotated(new Point(0,0), angleRad);
    }

	public Polygon getRotated(Point anchorPoint, double angleRad) {
		int size = this.points.length;
		Point[] rotatedPoints = new Point[size];

	    AffineTransform affineTransform = AffineTransform.getRotateInstance(angleRad, anchorPoint.x, anchorPoint.y);
	    for (int i = 0; i < size; i++) {
	    	Point2D rotatedPoint = affineTransform.transform(new Point2D.Double(points[i].x,points[i].y), null);
	    	rotatedPoints[i] = new Point((int) Math.round(rotatedPoint.getX()), (int) Math.round(rotatedPoint.getY()));
	    }

	    return new Polygon(rotatedPoints);
	}

	public Polygon getTranslated(Point translation) {
		int size = this.points.length;
		Point[] translatedPoints = new Point[size];

	    AffineTransform affineTransform = AffineTransform.getTranslateInstance(translation.x, translation.y);
	    for (int i = 0; i < size; i++) {
	    	Point2D rotatedPoint = affineTransform.transform(new Point2D.Double(points[i].x,points[i].y), null);
	    	translatedPoints[i] = new Point((int) Math.round(rotatedPoint.getX()), (int) Math.round(rotatedPoint.getY()));
	    }

	    return new Polygon(translatedPoints);
	}

	public boolean intersects(Polygon other) {
        for (int i = 0; i < points.length; i++) {
        	Point first = points[i];
        	Point second = i != points.length-1 ? points[i+1] : points[0];

            if (!first.equals(second)){
            	if (other.intersectsLine(first, second)) {
            		return true;
            	}
            }
        }

        for (int i = 0; i < points.length; i++) {
            if (other.isInside(points[i])) {
                return true;
            }
        }

        for (int i = 0; i < other.points.length; i++) {
            if (this.isInside(other.points[i])) {
                return true;
            }
        }



        return false;
	}

}
