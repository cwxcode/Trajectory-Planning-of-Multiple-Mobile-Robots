package tt.euclidtime3i.vis;

import javax.vecmath.Point2d;

import tt.euclidtime3i.Point;

public class SpatialProjectionTo2d implements tt.vis.ProjectionTo2d<Point> {

    @Override
    public Point2d project(Point point) {
        return new Point2d(point.x, point.y);
    }

}
