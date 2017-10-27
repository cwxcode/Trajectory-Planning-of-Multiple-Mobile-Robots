package tt.euclidyaw3d;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class Point extends Point3d {

	public Point(tt.euclid2d.Point position, double yaw) {
		super(position.x, position.y, yaw);
	}

	public Point(double x, double y, double z) {
		super(x, y, z);
	}

	public Point(Tuple3d t1) {
		super(t1);
	}

	public tt.euclid2d.Point getPosition() {
		return new tt.euclid2d.Point(x,y);
	}

	/* Yaw in radians: (-pi to +pi) */
	public double getYaw() {
		return z;
	}

	public tt.euclidyaw3i.Point toEuclidYaw3iPoint() {
        return new tt.euclidyaw3i.Point((int) Math.round(x), (int) Math.round(y), (float) getYaw());
    }

}
