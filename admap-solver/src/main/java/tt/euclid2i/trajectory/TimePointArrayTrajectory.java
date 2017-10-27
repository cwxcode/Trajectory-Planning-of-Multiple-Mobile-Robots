package tt.euclid2i.trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

public class TimePointArrayTrajectory extends BasicSegmentedTrajectory {

	public TimePointArrayTrajectory(tt.euclidtime3i.Point[] points, double cost) {
		super(toSegments(points), points[points.length-1].getTime(), cost);
	}

	private static List<Straight> toSegments(tt.euclidtime3i.Point[] points) {
		List<Straight> straights = new ArrayList<Straight>();
		if (points.length >= 2) {
			for (int i=0; i<points.length-1; i++) {
				straights.add(new Straight(points[i], points[i+1]));
			}
		} else if (points.length == 1) {
			straights.add(new Straight(points[0], points[0]));
		}
		return straights;
	}

}
