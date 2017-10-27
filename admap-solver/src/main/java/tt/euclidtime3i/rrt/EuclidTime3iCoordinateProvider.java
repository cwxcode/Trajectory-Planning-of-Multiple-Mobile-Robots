package tt.euclidtime3i.rrt;

import tt.euclidtime3i.Point;
import tt.util.EuclideanCoordinatesProvider;

/**
 * Created by Vojtech Letal on 3/7/14.
 */
public class EuclidTime3iCoordinateProvider implements EuclideanCoordinatesProvider<Point> {

    @Override
    public double[] getEuclideanCoordinates(Point point) {
        return new double[]{point.x, point.y};
    }

    @Override
    public int getSpaceDimension() {
        return 2;
    }
}
