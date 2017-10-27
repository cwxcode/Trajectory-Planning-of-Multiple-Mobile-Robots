package tt.jointeuclid2ni;

import java.util.Arrays;

public class Point {

    final tt.euclid2i.Point[] positions;

    public Point(tt.euclid2i.Point[] positions) {
        this.positions = positions;
    }

    public int nAgents() {
        return positions.length;
    }

    public tt.euclid2i.Point getAgent(int n) {
        return positions[n];
    }

    public tt.euclid2i.Point[] asArray() {
        return positions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i=0; i < positions.length; i++) {
            sb.append(positions[i]);
            sb.append(", ");
        }

        sb.delete(sb.length()-2, sb.length());

        sb.append("]");

        return sb.toString();
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(positions);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (!Arrays.equals(positions, other.positions))
            return false;
        return true;
    }

    /**
     * Return lower bound on duration of transition between this state and other state.
     * Assumes grid.
     */
    public double minDuration(Point other, double speed) {
        return minDuration(other.asArray(), speed);
    }

    public double minDuration(tt.euclid2i.Point[] other, double speed) {
        double longestDistance = 0.0;
        assert(this.nAgents() == other.length);
        for (int i=0; i < this.nAgents(); i++) {
            assert(getAgent(i) != null && other[i]!= null);
            if (positions[i] != null) {
                double distance = getAgent(i).distance(other[i]);
                if (distance > longestDistance) {
                    longestDistance = distance;
                }
            }
        }
        return longestDistance/speed;
    }

}
