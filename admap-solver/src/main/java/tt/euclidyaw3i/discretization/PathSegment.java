package tt.euclidyaw3i.discretization;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.MathUtils;
import tt.euclidyaw3i.Point;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class PathSegment {
	Point[] waypoints;
	PathSegment reversed;
	boolean isReversed;
	double length;
	
	public PathSegment(Point[] waypoints, PathSegment reversed, boolean isReversed, double length) {
		super();
		this.waypoints = waypoints;
		this.reversed = reversed;
		this.length = length;
		this.isReversed = isReversed;
	}
	
	public PathSegment(Point[] waypoints) {
		super();
		this.waypoints = waypoints;
		this.length = computePolylineLength(waypoints);
		this.isReversed = false;
		
		Point[] waypointsReversed = Arrays.copyOf(waypoints, waypoints.length);
		ArrayUtils.reverse(waypointsReversed);
		reversed = new PathSegment(waypointsReversed, null, true, length);
	}

	public Point[] getWaypoints() {
		return waypoints;
	}

	public double getLength() {
		return length;
	}
	
	public Point getLastWaypoint() {
		return waypoints[waypoints.length-1];
	}
	
	public Point getFirstWaypoint() {
		return waypoints[0];
	}

	@Override
	public String toString() {
		return "PathSegment [waypoints=" + Arrays.toString(waypoints) + ", length=" + length + "]";
	}
	
	public static double computePolylineLength(Point[] waypoints) {
		double distSum = 0;
		for (int i = 0; i < waypoints.length-1; i++) {
			distSum += waypoints[i].getPos().distance(waypoints[i+1].getPos());
		}
		return distSum;
	}

    public PathSegment getRotatedAndTranslated(float rotation, int translationX, int translationY) {

        Point[] transformedPoints = new Point[waypoints.length];

        AffineTransform affineTransform = AffineTransform.getRotateInstance(rotation, 0, 0);
        for (int i = 0; i < waypoints.length; i++) {
            Point2D rotatedPoint = affineTransform.transform(new Point2D.Double(waypoints[i].getPos().x,waypoints[i].getPos().y), null);

            float rotatedYaw = (float) MathUtils.normalizeAngle(waypoints[i].getYawInRads() + rotation, 0);
            transformedPoints[i] = new Point(translationX + (int) Math.round(rotatedPoint.getX()), translationY + (int) Math.round(rotatedPoint.getY()), rotatedYaw);
        }

        return new PathSegment(transformedPoints);
    }



	
	public PathSegment getReversed() {
		return reversed;
	}
	
	public boolean isReversed() {
		return isReversed;
	}
	
	
}
