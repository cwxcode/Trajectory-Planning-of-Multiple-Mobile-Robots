package tt.euclidtime3i;

import javax.vecmath.Point3i;

public class Point extends Point3i {

    public Point(int x, int y, int t) {
        super(x, y, t);
    }

    public Point(tt.euclid2i.Point spatialPoint, int time) {
        super(spatialPoint.x, spatialPoint.y, time);
    }

    public tt.euclid2i.Point getPosition() {
        return new tt.euclid2i.Point(x, y);
    }

    public int getTime() {
        return z;
    }

    public double[] toDoubleArray() {
        return new double[]{x, y, z};
    }

}
