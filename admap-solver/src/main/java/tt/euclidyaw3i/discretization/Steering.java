package tt.euclidyaw3i.discretization;

import tt.euclidyaw3i.Point;

interface Steering {
    PathSegment getSteering(Point x, Point y);
}
