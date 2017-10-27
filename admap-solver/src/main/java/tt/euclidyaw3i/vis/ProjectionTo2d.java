package tt.euclidyaw3i.vis;

import tt.euclidyaw3i.Point;

import javax.vecmath.Point2d;

public class ProjectionTo2d implements tt.vis.ProjectionTo2d<Point> {

    @Override
    public Point2d project(Point point) {
        return new Point2d(point.getPos().x, point.getPos().y);
    }

}
