package tt.euclidyaw3i.discretization;

import tt.euclidyaw3d.dubins.DubinsCurve;
import tt.euclidyaw3i.Point;

public class DubinsSteering implements Steering {
    double rho;
    double interpolationStep;

    public DubinsSteering(double rho, double interpolationStep) {
        this.rho = rho;
        this.interpolationStep = interpolationStep;
    }

    @Override
    public PathSegment getSteering(Point p1, Point p2) {
        DubinsCurve dc = new DubinsCurve(
                new tt.euclidyaw3d.Point(p1.getPos().x, p1.getPos().y, p1.getYawInRads()),
                new tt.euclidyaw3d.Point(p2.getPos().x, p2.getPos().y, p2.getYawInRads()),
                rho);

        tt.euclidyaw3d.Point[] interpolation = dc.interpolateUniformBy(interpolationStep);
        Point[] waypoints = new Point[interpolation.length+1];

        for (int i = 0; i < interpolation.length; i++) {
            waypoints[i] = interpolation[i].toEuclidYaw3iPoint();
        }

        waypoints[interpolation.length] = p2;

        return new PathSegment(waypoints);
    }
}
