package tt.euclidyaw3i.discretization;

import tt.euclidyaw3d.dubins.DubinsCurve;
import tt.euclidyaw3i.Point;

public class DubinsDistance implements Distance {
    double rho;

    public DubinsDistance(double rho) {
        this.rho = rho;
    }

    @Override
    public double getDistance(Point p1, Point p2) {
        DubinsCurve dc = new DubinsCurve(
                new tt.euclidyaw3d.Point(p1.getPos().x, p1.getPos().y, p1.getYawInRads()),
                new tt.euclidyaw3d.Point(p2.getPos().x, p2.getPos().y, p2.getYawInRads()),
                rho
        );

        return dc.getLength();
    }
}
