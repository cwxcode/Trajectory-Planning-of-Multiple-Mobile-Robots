package tt.euclidtime3i.vis;

import javax.vecmath.Point2d;

import tt.euclidtime3i.Point;

public class TimeParameterProjectionTo2d implements tt.vis.ProjectionTo2d<Point> {

	TimeParameter time;

    public TimeParameterProjectionTo2d(TimeParameter time) {
		super();
		this.time = time;
	}

	@Override
    public Point2d project(Point point) {
		if (point.getTime() == time.getTime()) {
			return new Point2d(point.x, point.y);
		} else {
			return null;
		}
    }

}
