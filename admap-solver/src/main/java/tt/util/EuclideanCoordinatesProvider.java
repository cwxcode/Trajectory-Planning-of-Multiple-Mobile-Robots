package tt.util;

public interface EuclideanCoordinatesProvider<V> {

    public int getSpaceDimension();

    public double[] getEuclideanCoordinates(V vertex);

}
