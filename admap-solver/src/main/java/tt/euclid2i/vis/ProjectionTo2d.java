package tt.euclid2i.vis;

import javax.vecmath.Point2d;

import tt.euclid2i.Point;

public class ProjectionTo2d implements tt.vis.ProjectionTo2d<Point> {

    @Override
    public Point2d project(Point point) {
        return new Point2d(point.x, point.y);
    }

}
