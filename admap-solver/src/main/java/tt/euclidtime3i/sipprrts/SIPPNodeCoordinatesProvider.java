package tt.euclidtime3i.sipprrts;

import tt.util.EuclideanCoordinatesProvider;

/**
 * Created by Vojtech Letal on 2/28/14.
 */
public class SIPPNodeCoordinatesProvider implements EuclideanCoordinatesProvider<SippRRTNode> {
    @Override
    public int getSpaceDimension() {
        return 2;
    }

    @Override
    public double[] getEuclideanCoordinates(SippRRTNode vertex) {
        return vertex.getPoint().toDoubleArray();
    }
}
