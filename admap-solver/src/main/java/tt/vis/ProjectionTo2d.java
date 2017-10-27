package tt.vis;

import javax.vecmath.Point2d;

public interface ProjectionTo2d<P> {
    Point2d project(P point);
}
